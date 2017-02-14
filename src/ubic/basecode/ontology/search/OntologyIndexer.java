/*
 * The Gemma project
 * 
 * Copyright (c) 2007 University of British Columbia
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
package ubic.basecode.ontology.search;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.larq.IndexBuilderSubject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import ubic.basecode.util.Configuration;

/**
 * @author pavlidis
 * @version $Id$
 */
public class OntologyIndexer {

    private static Logger log = LoggerFactory.getLogger( OntologyIndexer.class );

    /**
     * @param name
     * @return indexlarq with default analyzer (English)
     */
    @SuppressWarnings("resource")
    public static SearchIndex getSubjectIndex( String name ) {
        Analyzer analyzer = new EnglishAnalyzer( Version.LUCENE_36 );
        return getSubjectIndex( name, analyzer );
    }

    /**
     * @param name
     * @param model
     * @return
     */
    public static SearchIndex indexOntology( String name, OntModel model ) {
        return indexOntology( name, model, false );
    }

    /**
     * Loads or creates an index from an existing OntModel. Any existing index will loaded unless force=true.
     * 
     * @param name
     * @param model
     * @param force if true, the index will be redone
     * @return
     */
    public static SearchIndex indexOntology( String name, OntModel model, boolean force ) {

        if ( force ) {
            return index( name, model );
        }

        try {
            return getSubjectIndex( name );
        } catch ( Exception e ) {
            log.info( "Error loading index from disk, re-indexing " + name );
            return index( name, model );
        }
    }

    /**
     * @param name
     * @return
     */
    private static File getIndexPath( String name ) {
        String ontologyDir = Configuration.getString( "ontology.index.dir" ); // e.g., /something/gemmaData/compass
        if ( StringUtils.isBlank( ontologyDir ) ) {
            ontologyDir = System.getProperty( "java.io.tmpdir" );
        }

        assert ontologyDir != null;

        String path = ontologyDir + File.separator + "ontology" + File.separator + name;

        File indexdir = new File( path );
        return indexdir;
    }

    /**
     * @param name
     * @param analyzer
     * @return
     */
    @SuppressWarnings("resource")
    private static SearchIndex getSubjectIndex( String name, Analyzer analyzer ) {
        log.debug( "Loading index: " + name );
        File indexdir = getIndexPath( name );
        File indexdirstd = getIndexPath( name + ".std" );
        try {
            FSDirectory directory = FSDirectory.open( indexdir );
            FSDirectory directorystd = FSDirectory.open( indexdirstd );
            if ( !IndexReader.indexExists( directory ) ) {
                directorystd.close();
                throw new IllegalArgumentException( "No index with name " + indexdir );
            }
            if ( !IndexReader.indexExists( directorystd ) ) {
                directory.close();
                throw new IllegalArgumentException( "No index with name " + indexdirstd );
            }

            IndexReader reader = IndexReader.open( directory );
            IndexReader readerstd = IndexReader.open( directorystd );
            MultiReader r = new MultiReader( reader, readerstd );
            return new SearchIndex( r, analyzer );

        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Create an on-disk index from an existing OntModel. Any existing index will be deleted/overwritten.
     * 
     * @see {@link http://jena.apache.org/documentation/larq/}
     * @param datafile or uri
     * @param name used to refer to this index later
     * @param model
     * @return
     */
    @SuppressWarnings("resource")
    private static synchronized SearchIndex index( String name, OntModel model ) {

        File indexdir = getIndexPath( name );

        try {
            FSDirectory dir = FSDirectory.open( indexdir );
            log.info( "Index to: " + indexdir );

            /*
             * adjust the analyzer ...
             */
            Analyzer analyzer = new EnglishAnalyzer( Version.LUCENE_36 );
            IndexWriterConfig config = new IndexWriterConfig( Version.LUCENE_36, analyzer );
            IndexWriter indexWriter = new IndexWriter( dir, config );
            indexWriter.deleteAll(); // start with clean slate.
            assert 0 == indexWriter.numDocs();

            IndexBuilderSubject larqSubjectBuilder = new IndexBuilderSubject( indexWriter );
            StmtIterator listStatements = model.listStatements( new IndexerSelector() );
            larqSubjectBuilder.indexStatements( listStatements );
            indexWriter.commit();
            log.info( indexWriter.numDocs() + " Statements indexed..." );
            indexWriter.close();

            Directory dirstd = indexStd( name, model );

            MultiReader r = new MultiReader( IndexReader.open( dir ), IndexReader.open( dirstd ) );

            // workaround to get the EnglishAnalyzer.
            SearchIndex index = new SearchIndex( r, new EnglishAnalyzer( Version.LUCENE_36 ) );
            // larqSubjectBuilder.getIndex(); // always returns a StandardAnalyazer
            assert index.getLuceneQueryParser().getAnalyzer() instanceof EnglishAnalyzer;
            return index;
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * We need to also analyze using the Standard analyzer, which doesn't do stemming and allows wildcard.
     */
    private static Directory indexStd( String name, OntModel model ) throws IOException {

        File file = getIndexPath( name + ".std" );

        FSDirectory dir = FSDirectory.open( file );
        dir.getLockFactory().clearLock( dir.getLockID() );
        log.info( "Index to: " + file );
        Analyzer analyzer = new StandardAnalyzer( Version.LUCENE_36 );
        IndexWriterConfig config = new IndexWriterConfig( Version.LUCENE_36, analyzer );
        IndexWriter indexWriter = new IndexWriter( dir, config );
        indexWriter.deleteAll();
        IndexBuilderSubject larqSubjectBuilder = new IndexBuilderSubject( indexWriter );
        StmtIterator listStatements = model.listStatements( new IndexerSelector() );
        larqSubjectBuilder.indexStatements( listStatements );
        indexWriter.commit();
        log.info( indexWriter.numDocs() + " Statements indexed..." );
        indexWriter.close();
        analyzer.close(); // to satisfy potential resource leak warning
        return dir;
    }
    /*
     * Allow adding custom stopwords for particular ontologies like "disease" for DO.
     */
    // private static Set<?> getStopWords( String name ) {
    // /*
    // * Look for a file like 'name.stopwordsfile'
    // */
    // Set<String> stopWords = new HashSet<String>();
    // String path = Configuration.getString( name + ".stopwordsfile" );
    // if ( StringUtils.isNotBlank( path ) ) {
    //
    // } else {
    //
    // }
    //
    // return stopWords;
    // }
}
