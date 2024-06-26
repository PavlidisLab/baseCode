/*
 * The Gemma21 project
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

package ubic.basecode.ontology.providers;

/**
 * <a href="https://obofoundry.org/ontology/fma.html">Foundational Model of Anatomy Ontology (subset)</a>
 *
 * @author klc
 * @deprecated this ontology is inactive, use <a href="https://obofoundry.org/ontology/uberon.html">UBERON</a> instead
 */
@Deprecated
public class FMAOntologyService extends AbstractBaseCodeOntologyService {

    public FMAOntologyService() {
        super( "Foundational Model of Anatomy Ontology (subset)", "fmaOntology" );
    }
}
