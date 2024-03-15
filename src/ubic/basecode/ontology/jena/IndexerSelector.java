/*
 * The baseCode project
 *
 * Copyright (c) 2008-2019 University of British Columbia
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

import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;

/**
 * Used to limit which parts of ontologies get indexed for searching. This avoids indexing some parts of ontologies such
 * as "examples" and "definitions" but this is set up in a partly ontology-specific way (that is, hard-coded).
 *
 * @author paul
 */
class IndexerSelector implements Selector {

    private static final Logger log = LoggerFactory.getLogger( IndexerSelector.class );

    private static final Collection<Property> wantedForIndexing;

    static {
        wantedForIndexing = new HashSet<>();
        wantedForIndexing.add( RDFS.label );
        wantedForIndexing.add( RDFS.comment );

        wantedForIndexing.add( OBO.id );
        wantedForIndexing.add( OBO.hasDbXref );
        wantedForIndexing.add( OBO.hasSynonym );
        wantedForIndexing.add( OBO.hasExactSynonym );
        wantedForIndexing.add( OBO.hasBroadSynonym );
        wantedForIndexing.add( OBO.hasNarrowSynonym );
        wantedForIndexing.add( OBO.hasRelatedSynonym );
        wantedForIndexing.add( OBO.alternativeLabel );
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
        if ( s.getSubject().getURI() == null ) {
            return false;
        }

        boolean retain = wantedForIndexing.contains( s.getPredicate() );

        // bit of a special case ...
        if ( s.getPredicate().equals( OWL2.annotatedProperty ) && s.getObject().canAs( Property.class ) ) {
            try {
                retain = wantedForIndexing.contains( s.getObject().as( Property.class ) );
            } catch ( ConversionException e ) {
                log.error( "Conversion failed for " + s.getObject(), e );
            }
        }

        return retain;
    }
}
