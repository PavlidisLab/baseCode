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

import java.util.Collection;

/**
 * @author Paul
 */
public interface OntologyTerm extends OntologyResource {

    Collection<String> getAlternativeIds();

    Collection<AnnotationProperty> getAnnotations();

    Collection<OntologyTerm> getChildren( boolean direct );

    /**
     * @param direct return only the immediate children; if false, return all of them down to the leaves.
     * @param includePartOf include terms matched via
     * @return
     */
    Collection<OntologyTerm> getChildren( boolean direct, boolean includePartOf );

    String getComment();

    Collection<OntologyIndividual> getIndividuals();

    Collection<OntologyIndividual> getIndividuals( boolean direct );

    String getLocalName();

    Object getModel();

    /**
     * Note that any restriction superclasses are not returned, unless they are has_proper_part
     *
     * @param direct
     * @return
     */
    Collection<OntologyTerm> getParents( boolean direct );

    Collection<OntologyTerm> getParents( boolean direct, boolean includePartOf );

    Collection<OntologyRestriction> getRestrictions();

    String getTerm();

    @Override
    String getUri();

    boolean isRoot();

    /**
     * check to see if the term is obsolete, if it is it should not be used
     */
    boolean isTermObsolete();
}
