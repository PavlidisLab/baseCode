/*
 * The Gemma project
 * 
 * Copyright (c) 2007 Columbia University
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
 
import com.hp.hpl.jena.ontology.Restriction;

/**
 * Represents a restriction on instances that are subclasses of this.
 * 
 * @author Paul
 * @version $Id$
 */
public abstract class OntologyRestrictionImpl extends OntologyTermImpl implements OntologyRestriction {

    protected OntologyProperty restrictionOn = null;

    public OntologyRestrictionImpl( Restriction resource  ) {
        super( resource  );
        this.restrictionOn = PropertyFactory.asProperty( resource.getOnProperty()  );
    }

    public OntologyProperty getRestrictionOn() {
        return restrictionOn;
    }

}
