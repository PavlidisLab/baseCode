/*
 * The basecode project
 * 
 * Copyright (c) 2007-2019 Columbia University
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

/**
 * Note: this only handle 'someof' and 'allof' restrictions, not cardinality.
 * 
 * @author Paul
 * 
 */
public interface OntologyRestriction extends OntologyTerm {

    public OntologyProperty getRestrictionOn();

}
