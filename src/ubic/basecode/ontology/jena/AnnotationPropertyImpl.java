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

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;
import ubic.basecode.ontology.model.AnnotationProperty;

import javax.annotation.Nullable;

/**
 * Note that this is a concrete instance of the annotation.
 *
 * @author pavlidis
 */
class AnnotationPropertyImpl extends AbstractOntologyResource implements AnnotationProperty {

    private final com.hp.hpl.jena.ontology.AnnotationProperty property;
    @Nullable
    private final RDFNode object;

    /**
     * @param prop   property for the statement
     * @param object of the statement
     */
    public AnnotationPropertyImpl( com.hp.hpl.jena.ontology.AnnotationProperty prop, @Nullable RDFNode object ) {
        super( prop );
        this.property = prop;
        this.object = object;
    }

    @Override
    public boolean equals( @Nullable Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final AnnotationPropertyImpl other = ( AnnotationPropertyImpl ) obj;
        if ( object == null ) {
            if ( other.object != null ) return false;
        } else if ( !object.equals( other.object ) ) return false;
        if ( property == null ) {
            return other.property == null;
        } else return property.equals( other.property );
    }

    @Override
    public String getProperty() {
        if ( property.getLabel( null ) != null ) {
            return property.getLabel( null );
        } else if ( property.getLocalName() != null ) {
            return property.getLocalName();
        } else {
            return property.toString();
        }
    }

    @Override
    public String getContents() {
        if ( this.object == null ) {
            return null;
        } else if ( object.isResource() ) {
            // need a level of indirection...
            Resource r = ( Resource ) object;
            Statement s = r.getProperty( RDFS.label );
            if ( s != null ) {
                return s.getObject().toString();
            } else {
                return null;
            }
        } else {
            return JenaUtils.asString( object );
        }
    }

    @Override
    public boolean isObsolete() {
        return super.isObsolete() || property.hasSuperProperty( OBO.ObsoleteProperty, false );
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( object == null ) ? 0 : object.hashCode() );
        result = PRIME * result + ( ( property == null ) ? 0 : property.hashCode() );
        return result;
    }

    @Override
    public String toString() {
        return property.getLocalName() + " " + object;
    }

}
