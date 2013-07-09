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
package ubic.basecode.ontology.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;

/**
 * @author pavlidis
 * @version $Id$
 */
public class PropertyFactory {

    private static Log log = LogFactory.getLog( PropertyFactory.class.getName() );

    /**
     * Convert a Jena property.
     * 
     * @param property
     * @param source
     * @return
     */
    public static ubic.basecode.ontology.model.OntologyProperty asProperty( OntProperty property ) {

        if ( property.isObjectProperty() ) {
            return new ObjectPropertyImpl( property.asObjectProperty() );
        } else if ( property.isDatatypeProperty() ) {
            return new DatatypePropertyImpl( property.asDatatypeProperty() );
        } else {
            log.warn( "Sorry, can't convert " + property.getClass().getName() + ": " + property );
            return null;
        }

    }

    public static Class<?> convertType( DatatypeProperty resource ) {
        Class<?> type = java.lang.String.class;

        OntResource range = resource.getRange();
        if ( range != null ) {
            String uri = range.getURI();

            if ( uri == null ) {
                log.warn( "Can't get type for " + resource + " with range " + range );
                type = java.lang.String.class;
            } else if ( uri.equals( "http://www.w3.org/2001/XMLSchema#&xsd;string" ) ) {
                type = java.lang.String.class;
            } else if ( uri.equals( "http://www.w3.org/2001/XMLSchema#&xsd;boolean" ) ) {
                type = java.lang.Boolean.class;
            } else if ( uri.equals( "http://www.w3.org/2001/XMLSchema#&xsd;float" ) ) {
                type = java.lang.Double.class;
            } else if ( uri.equals( "http://www.w3.org/2001/XMLSchema#&xsd;double" ) ) {
                type = java.lang.Double.class;
            } else if ( uri.equals( "http://www.w3.org/2001/XMLSchema#&xsd;int" ) ) {
                type = java.lang.Integer.class;
            } else if ( uri.equals( "http://www.w3.org/2001/XMLSchema#&xsd;date" ) ) {
                type = java.util.Date.class;
            }
        }
        return type;
    }
}
