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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Used to limit which parts of ontologies get indexed for searching.
 * 
 * @author paul
 * @version $Id$
 */
public class IndexerSelector implements Selector {

    private static Log log = LogFactory.getLog( IndexerSelector.class );

    private final Collection<String> badPredicates;

    public IndexerSelector() {
        // these are predicates that in general should not be useful for indexing
        badPredicates = new HashSet<String>();
        badPredicates.add( RDFS.comment.getURI() );
        badPredicates.add( RDFS.seeAlso.getURI() );
        badPredicates.add( RDFS.isDefinedBy.getURI() );
        badPredicates.add( "http://www.w3.org/2002/07/owl#inverseOf" );
        badPredicates.add( "http://www.w3.org/2004/02/skos/core#example" );
        badPredicates.add( "http://neurolex.org/wiki/Special:URIResolver/Property-3AExample" );
        badPredicates.add( "http://www.ebi.ac.uk/efo/definition" );
        badPredicates.add( "http://www.ebi.ac.uk/efo/bioportal_provenance" );
        badPredicates.add( "http://www.ebi.ac.uk/efo/gwas_trait" );
        badPredicates.add( "http://www.ebi.ac.uk/efo/definition_editor" );
        badPredicates.add( "http://www.ebi.ac.uk/efo/example_of_usage" );
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
        boolean retain = !( badPredicates.contains( s.getPredicate().getURI() ) );

        if ( !retain && log.isDebugEnabled() ) log.debug( "Removed: " + s.getPredicate() + " " + s.getObject() );

        return retain;
    }
}
