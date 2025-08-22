/*
 * The basecode project
 *
 * Copyright (c) 2007-2019 University of British Columbia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ubic.basecode.ontology.search.OntologySearchException;
import ubic.basecode.util.Configuration;

import org.jspecify.annotations.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ubic.basecode.ontology.jena.JenaUtils.as;

/**
 * A Lucene-based ontology indexer.
 *
 * @author pavlidis
 */
class OntologyIndexer {

    private static final Logger log = LoggerFactory.getLogger( OntologyIndexer.class );

    /**
     * Those are build-in fields that are always indexed.
     */
    private static final String
        ID_FIELD = "_ID",
        LOCAL_NAME_FIELD = "_LOCAL_NAME",
        IS_CLASS_FIELD = "_IS_CLASS",
        IS_INDIVIDUAL_FIELD = "_IS_INDIVIDUAL";

    public static class IndexableProperty {
        private final Property property;
        private final boolean analyzed;

        public IndexableProperty( Property property, boolean analyzed ) {
            this.property = property;
            this.analyzed = analyzed;
        }

        public Property getProperty() {
            return property;
        }

        public boolean isAnalyzed() {
            return analyzed;
        }
    }

    public static final Collection<IndexableProperty> DEFAULT_INDEXABLE_PROPERTIES;

    static {
        DEFAULT_INDEXABLE_PROPERTIES = new HashSet<>();
        DEFAULT_INDEXABLE_PROPERTIES.add( new IndexableProperty( RDFS.label, true ) );
        DEFAULT_INDEXABLE_PROPERTIES.add( new IndexableProperty( OBO.id, true ) );
        DEFAULT_INDEXABLE_PROPERTIES.add( new IndexableProperty( OBO.hasDbXref, true ) );
        DEFAULT_INDEXABLE_PROPERTIES.add( new IndexableProperty( OBO.hasSynonym, true ) );
        DEFAULT_INDEXABLE_PROPERTIES.add( new IndexableProperty( OBO.hasExactSynonym, true ) );
        DEFAULT_INDEXABLE_PROPERTIES.add( new IndexableProperty( OBO.hasBroadSynonym, true ) );
        DEFAULT_INDEXABLE_PROPERTIES.add( new IndexableProperty( OBO.hasNarrowSynonym, true ) );
        DEFAULT_INDEXABLE_PROPERTIES.add( new IndexableProperty( OBO.hasRelatedSynonym, true ) );
        DEFAULT_INDEXABLE_PROPERTIES.add( new IndexableProperty( IAO.alternativeLabel, true ) );
    }

    /**
     * Obtain an ontology index with the default indexable properties.
     */
    @Nullable
    public static SearchIndex getSubjectIndex( String name, Set<String> excludedFromStemming ) {
        return getSubjectIndex( name, DEFAULT_INDEXABLE_PROPERTIES, excludedFromStemming );
    }

    /**
     * Obtain an index with default analyzer (English), or null if no index is available.
     * <p>
     * <b>DOES not create the index if it doesn't exist.</b>
     */
    @Nullable
    public static SearchIndex getSubjectIndex( String name, Collection<IndexableProperty> indexableProperties, Set<String> excludedFromStemming ) {
        log.debug( "Loading index for {}...", name );
        try {
            // we do not put this in the try-with-open because we want these to *stay* open
            FSDirectory directory = FSDirectory.open( getIndexPath( name ).toFile() );
            FSDirectory directoryStd = FSDirectory.open( getIndexPath( name + ".std" ).toFile() );
            if ( !IndexReader.indexExists( directory ) ) {
                return null;
            }
            if ( !IndexReader.indexExists( directoryStd ) ) {
                return null;
            }
            return openIndex( directory, directoryStd, indexableProperties, excludedFromStemming );
        } catch ( IOException e ) {
            log.warn( "Index for {} could not be opened.", name, e );
            return null;
        }
    }

    /**
     * Index an ontology with the default indexable properties.
     */
    public static SearchIndex indexOntology( String name, OntModel model, Set<String> excludedFromStemming, boolean force ) throws JenaException, IOException {
        return indexOntology( name, model, DEFAULT_INDEXABLE_PROPERTIES, excludedFromStemming, force );
    }

    /**
     * Loads or creates an index from an existing OntModel. Any existing index will loaded unless force=true. It will be
     * created if there isn't one already, or if force=true.
     */
    public static SearchIndex indexOntology( String name, OntModel model, Collection<IndexableProperty> indexableProperties, Set<String> excludedFromStemming, boolean force ) throws JenaException, IOException {
        if ( force ) {
            return index( name, model, indexableProperties, excludedFromStemming );
        }
        SearchIndex index = getSubjectIndex( name, excludedFromStemming );
        if ( index == null ) {
            log.warn( "Index not found, or there was an error, re-indexing {}...", name );
            return index( name, model, indexableProperties, excludedFromStemming );
        }
        log.debug( "A valid index for {} already exists, using", name );
        return index;
    }

    private static Path getIndexPath( String name ) {
        if ( StringUtils.isBlank( name ) ) {
            throw new IllegalArgumentException( "The ontology must have a suitable name for being indexed." );
        }
        String ontologyDir = Configuration.getString( "ontology.index.dir" ); // e.g., /something/gemmaData/compass
        if ( StringUtils.isBlank( ontologyDir ) ) {
            return Paths.get( System.getProperty( "java.io.tmpdir" ), "searchIndices", "ontology", name );
        }
        return Paths.get( ontologyDir, "ontology", name );
    }

    /**
     * Create an on-disk index from an existing OntModel. Any existing index will be deleted/overwritten.
     */
    private static SearchIndex index( String name, OntModel model, Collection<IndexableProperty> indexableProperties, Set<String> excludedFromStemming ) throws JenaException, IOException {
        Directory dir = index( name, model, new EnglishAnalyzer( Version.LUCENE_36, EnglishAnalyzer.getDefaultStopSet(), excludedFromStemming ), getIndexPath( name ), indexableProperties );
        // we need to also analyze using the Standard analyzer, which doesn't do stemming and allows wildcard.
        Directory dirStd = index( name, model, new StandardAnalyzer( Version.LUCENE_36 ), getIndexPath( name + ".std" ), indexableProperties );
        return openIndex( dir, dirStd, indexableProperties, excludedFromStemming );
    }

    private static Directory index( String name, OntModel model, Analyzer analyzer, Path indexDir, Collection<IndexableProperty> indexableProperties ) throws IOException {
        StopWatch timer = StopWatch.createStarted();
        FSDirectory dir = FSDirectory.open( indexDir.toFile() );
        log.debug( "Indexing {} to: {}...", name, indexDir );
        IndexWriterConfig config = new IndexWriterConfig( Version.LUCENE_36, analyzer );
        try ( IndexWriter indexWriter = new IndexWriter( dir, config ) ) {
            indexWriter.deleteAll(); // start with clean slate.
            assert 0 == indexWriter.numDocs();
            Map<String, IndexableProperty> indexablePropertiesByField = indexableProperties.stream()
                .collect( Collectors.toMap( p -> p.getProperty().getURI(), p -> p ) );
            ExtendedIterator<Resource> subjects = model.listSubjects()
                .filterDrop( new BnodeFilter<>() );
            while ( subjects.hasNext() ) {
                Resource subject = subjects.next();
                String id = subject.getURI();
                Document doc = new Document();
                doc.add( new Field( ID_FIELD, id, Field.Store.YES, Field.Index.NOT_ANALYZED ) );
                doc.add( new Field( LOCAL_NAME_FIELD, subject.getLocalName(), Field.Store.NO, Field.Index.NOT_ANALYZED ) );
                boolean isClass = as( subject, OntResource.class ).map( OntResource::isClass ).orElse( false );
                boolean isIndividual = as( subject, OntResource.class ).map( OntResource::isIndividual ).orElse( false );
                doc.add( new NumericField( IS_CLASS_FIELD ).setIntValue( isClass ? 1 : 0 ) );
                doc.add( new NumericField( IS_INDIVIDUAL_FIELD ).setIntValue( isIndividual ? 1 : 0 ) );
                for ( IndexableProperty prop : indexableProperties ) {
                    StmtIterator listStatements = subject.listProperties( prop.property );
                    while ( listStatements.hasNext() ) {
                        Statement s = listStatements.next();
                        String field = s.getPredicate().getURI();
                        Fieldable f;
                        if ( s.getObject().isLiteral() ) {
                            Literal l = s.getObject().asLiteral();
                            Object v;
                            try {
                                v = l.getValue();
                            } catch ( DatatypeFormatException e ) {
                                log.warn( "Invalid datatype for literal: {}", l, e );
                                continue;
                            }
                            if ( v instanceof String ) {
                                f = new Field( field, ( String ) v, Field.Store.NO, indexablePropertiesByField.get( field ).isAnalyzed() ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED );
                            } else if ( v instanceof Number ) {
                                NumericField nf = new NumericField( field );
                                if ( v instanceof Integer ) {
                                    nf.setIntValue( ( Integer ) v );
                                } else if ( v instanceof Long ) {
                                    nf.setLongValue( ( Long ) v );
                                } else if ( v instanceof Float ) {
                                    nf.setFloatValue( ( Float ) v );
                                } else if ( v instanceof Double ) {
                                    nf.setDoubleValue( ( Double ) v );
                                } else {
                                    log.warn( "Skipping numeric literal of unsupported type: {}", l );
                                    continue;
                                }
                                f = nf;
                            } else if ( v instanceof XSDDateTime ) {
                                f = new NumericField( field )
                                    .setLongValue( ( ( XSDDateTime ) v ).asCalendar().getTime().getTime() );
                            } else if ( v instanceof Boolean ) {
                                f = new NumericField( field ).setIntValue( Boolean.TRUE.equals( v ) ? 1 : 0 );
                            } else {
                                log.warn( "Skipping literal of unsupported type: {}", l );
                                continue;
                            }
                        } else if ( s.getObject().isURIResource() ) {
                            // index the URI
                            f = new Field( field, s.getObject().asResource().getURI(), Field.Store.NO, Field.Index.NOT_ANALYZED );
                        } else {
                            // could be a blank node
                            continue;
                        }
                        doc.add( f );
                    }
                }
                indexWriter.addDocument( doc );
            }
            indexWriter.commit();
            log.debug( "Done indexing {} subjects of {} in {} s.", indexWriter.numDocs(), name, String.format( "%.2f", timer.getTime() / 1000.0 ) );
        }
        return dir;
    }

    private static SearchIndex openIndex( Directory dir, Directory dirStd, Collection<IndexableProperty> indexableProperties, Set<String> excludedFromStemming ) throws IOException {
        String[] searchableFields = Stream.concat( Stream.of( ID_FIELD, LOCAL_NAME_FIELD ), indexableProperties.stream().map( p -> p.property ).map( Resource::getURI ) )
            .distinct()
            .toArray( String[]::new );
        return new LuceneSearchIndex( searchableFields, new MultiReader( IndexReader.open( dir ), IndexReader.open( dirStd ) ), new EnglishAnalyzer( Version.LUCENE_36, EnglishAnalyzer.getDefaultStopSet(), excludedFromStemming ) );
    }

    private static class LuceneSearchIndex implements SearchIndex {

        private static final Logger log = LoggerFactory.getLogger( LuceneSearchIndex.class );

        private final String[] searchableFields;
        private final IndexReader index;
        private final Analyzer analyzer;

        public LuceneSearchIndex( String[] searchableFields, IndexReader index, Analyzer analyzer ) {
            this.searchableFields = searchableFields;
            this.index = index;
            this.analyzer = analyzer;
        }

        @Override
        public List<JenaSearchResult> search( OntModel model, String queryString, int maxResults ) throws OntologySearchException {
            return search( model, queryString, null, maxResults );
        }

        @Override
        public List<JenaSearchResult> searchClasses( OntModel model, String queryString, int maxResults ) throws OntologySearchException {
            return search( model, queryString, NumericRangeFilter.newIntRange( IS_CLASS_FIELD, 1, 1, true, true ), maxResults );
        }

        @Override
        public List<JenaSearchResult> searchIndividuals( OntModel model, String queryString, int maxResults ) throws OntologySearchException {
            return search( model, queryString, NumericRangeFilter.newIntRange( IS_INDIVIDUAL_FIELD, 1, 1, true, true ), maxResults );
        }

        private List<JenaSearchResult> search( OntModel model, String queryString, @Nullable Filter filter, int maxResults ) throws OntologySearchException {
            if ( StringUtils.isBlank( queryString ) ) {
                throw new IllegalArgumentException( "Query cannot be blank" );
            }
            StopWatch timer = StopWatch.createStarted();
            try {
                Query query = new MultiFieldQueryParser( Version.LUCENE_36, searchableFields, analyzer ).parse( queryString );
                // in general, results are found in both regular and std index, so we divide by 2 the initial capacity
                // we also have to double the number of hits to account for duplicates
                TopDocs hits = new IndexSearcher( index ).search( query, filter, maxResults * 2 );
                Set<String> seenIds = new HashSet<>( hits.totalHits / 2 );
                List<JenaSearchResult> resources = new ArrayList<>( hits.totalHits / 2 );
                for ( int i = 0; i < hits.scoreDocs.length; i++ ) {
                    Document doc = index.document( hits.scoreDocs[i].doc );
                    String id = doc.get( ID_FIELD );
                    if ( seenIds.contains( id ) ) {
                        continue;
                    }
                    Resource res = model.getResource( id );
                    resources.add( new JenaSearchResult( res, hits.scoreDocs[i].score ) );
                    seenIds.add( id );
                    if ( seenIds.size() >= maxResults ) {
                        break;
                    }
                }
                return resources;
            } catch ( ParseException e ) {
                throw new OntologySearchException( "Failed to parse search query.", queryString, e );
            } catch ( IOException e ) {
                throw new OntologySearchException( "An I/O error occured while searching.", queryString, e );
            } finally {
                timer.stop();
                if ( timer.getTime() > 100 ) {
                    log.warn( "Ontology resource search for: {} took {} ms.", queryString, timer.getTime() );
                }
            }
        }

        @Override
        public void close() throws IOException {
            index.close();
        }
    }

}
