/*
 * The Gemma project
 * 
 * Copyright (c) 2008 University of British Columbia
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

import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Used to limit which parts of ontologies get indexed for searching. This avoids indexing some parts of ontologies such
 * as "examples" and "definitions" but this is set up in a partly ontology-specific way (that is, hard-coded).
 * 
 * @author paul
 * @version $Id$
 */
public class IndexerSelector implements Selector {

    private static Logger log = LoggerFactory.getLogger( IndexerSelector.class );
    private final Collection<String> unwantedForIndexing;

    public IndexerSelector() {
        // these are predicates that in general should not be useful for indexing
        unwantedForIndexing = new HashSet<String>();
        unwantedForIndexing.add( RDFS.comment.getURI() );
        unwantedForIndexing.add( RDFS.seeAlso.getURI() );
        unwantedForIndexing.add( RDFS.isDefinedBy.getURI() );
        unwantedForIndexing.add( "http://purl.org/dc/elements/1.1/creator" );
        unwantedForIndexing.add( "http://purl.org/dc/elements/1.1/contributor" );
        unwantedForIndexing.add( "http://purl.org/dc/elements/1.1/source" );
        unwantedForIndexing.add( "http://purl.org/dc/elements/1.1/title" );
        unwantedForIndexing.add( "http://purl.org/dc/elements/1.1/description" );

        unwantedForIndexing.add( "http://www.w3.org/2002/07/owl#inverseOf" );
        unwantedForIndexing.add( "http://www.w3.org/2002/07/owl#disjointWith" );
        unwantedForIndexing.add( "http://www.w3.org/2004/02/skos/core#example" );
        unwantedForIndexing.add( "http://www.w3.org/2004/02/skos/core#editorialNote" );
        unwantedForIndexing.add( "http://www.w3.org/2004/02/skos/core#historyNote" );
        unwantedForIndexing.add( "http://www.w3.org/2004/02/skos/core#definition" );
        unwantedForIndexing.add( "http://neurolex.org/wiki/Special:URIResolver/Property-3AExample" );
        unwantedForIndexing.add( "http://www.ebi.ac.uk/efo/definition" );
        unwantedForIndexing.add( "http://www.ebi.ac.uk/efo/bioportal_provenance" );
        unwantedForIndexing.add( "http://www.ebi.ac.uk/efo/gwas_trait" );
        unwantedForIndexing.add( "http://www.ebi.ac.uk/efo/definition_editor" );
        unwantedForIndexing.add( "http://www.ebi.ac.uk/efo/example_of_usage" );
        unwantedForIndexing.add( "http://www.geneontology.org/formats/oboInOwl#Definition" );
        unwantedForIndexing.add( "http://purl.obolibrary.org/obo/IAO_0000115" ); // 'definition' - too often has extra
                                                                                 // junk.
        unwantedForIndexing.add( "http://purl.obolibrary.org/obo/IAO_0000112" ); // 'example of usage
        unwantedForIndexing.add( "http://purl.obolibrary.org/obo/IAO_0000116" ); // editor note.
        unwantedForIndexing.add( "http://purl.obolibrary.org/obo/IAO_0000117" ); // term editor
        unwantedForIndexing.add( "http://purl.obolibrary.org/obo/IAO_0000114" ); // curation status.
        unwantedForIndexing.add( "http://purl.obolibrary.org/obo/IAO_0000232" ); // curator note.
        unwantedForIndexing
                .add( "http://ontology.neuinfo.org/NIF/Backend/OBO_annotation_properties.owl#externallySourcedDefinition" );
        unwantedForIndexing
                .add( "http://ontology.neuinfo.org/NIF/Backend/BIRNLex_annotation_properties.owl#birnlexDefinition" );

        unwantedForIndexing
                .add( "http://ontology.neuinfo.org/NIF/Backend/BIRNLex_annotation_properties.owl#hasBirnlexCurator" );

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hp.hpl.jena.rdf.model.Selector#getObject()
     */
    @Override
    public RDFNode getObject() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hp.hpl.jena.rdf.model.Selector#getPredicate()
     */
    @Override
    public Property getPredicate() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hp.hpl.jena.rdf.model.Selector#getSubject()
     */
    @Override
    public Resource getSubject() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hp.hpl.jena.rdf.model.Selector#isSimple()
     */
    @Override
    public boolean isSimple() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hp.hpl.jena.rdf.model.Selector#test(com.hp.hpl.jena.rdf.model.Statement)
     */
    @Override
    public boolean test( Statement s ) {
        boolean retain = !unwantedForIndexing.contains( s.getPredicate().getURI() );

        // bit of a special case ...
        if ( s.getPredicate().getURI().equals( "http://www.w3.org/2002/07/owl#annotatedProperty" ) ) {
            retain = !unwantedForIndexing.contains( s.getObject().toString() );
        }

        if ( !retain && log.isDebugEnabled() ) {
            log.debug( "Removed: " + s );
        }

        return retain;
    }
}
