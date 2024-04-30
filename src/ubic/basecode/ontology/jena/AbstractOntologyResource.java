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

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.vocabulary.OWL2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ubic.basecode.ontology.model.OntologyResource;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author pavlidis
 */
abstract class AbstractOntologyResource implements OntologyResource {

    protected static final Logger log = LoggerFactory.getLogger( AbstractOntologyResource.class );

    private final OntResource res;

    private String _label;
    private boolean _isLabelNull = false;

    private String _label;
    private boolean _isLabelNull = false;

    protected AbstractOntologyResource( OntResource resource ) {
        this.res = resource;
    }

    @Override
    public String getUri() {
        return res.getURI();
    }

    @Override
    public String getLocalName() {
        return res.getLocalName();
    }

    @Override
    public String getLabel() {
        if ( _label != null || _isLabelNull ) {
            return _label;
        }
        String label = res.getLabel( "EN" );
        if ( label == null ) {
            label = res.getLabel( null );
        }
        _label = label;
        _isLabelNull = label == null;
        return label;
    }

    @Nullable
    @Override
    public String getComment() {
        String label = res.getComment( "EN" );
        if ( label == null ) {
            label = res.getLabel( null );
        }
        return label;
    }

    @Override
    public boolean isObsolete() {
        return res.hasLiteral( OWL2.deprecated, true );
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !( obj instanceof OntologyResource ) ) {
            return false;
        }
        final OntologyResource other = ( OntologyResource ) obj;
        if ( getUri() == null && other.getUri() == null ) {
            return Objects.equals( getLabel(), other.getLabel() );
        } else {
            return Objects.equals( getUri(), other.getUri() );
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash( getUri() );
    }

    @Override
    public String toString() {
        String s = getLabel();
        if ( s == null ) {
            s = res.getLocalName();
        }
        if ( s == null ) {
            s = res.getURI();
        }
        if ( s == null ) {
            s = res.toString();
        }
        return s;
    }
}