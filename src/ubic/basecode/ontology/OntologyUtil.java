/*
 * The baseCode project
 * 
 * Copyright (c) 2009 University of British Columbia
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
package ubic.basecode.ontology;

import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFVisitor;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author paul
 * 
 */
public class OntologyUtil {

    /**
     * Use to pretty-print a RDFNode
     * 
     * @param object
     * @return
     */
    public static String asString( RDFNode object ) {
        return ( String ) object.visitWith( new RDFVisitor() {

            @Override
            public Object visitBlank( Resource r, AnonId id ) {
                return r.getLocalName();
            }

            @Override
            public Object visitLiteral( Literal l ) {
                return l.toString().replaceAll( "\\^\\^.+", "" );
            }

            @Override
            public Object visitURI( Resource r, String uri ) {
                return r.getLocalName();
            }
        } );
    }

}
