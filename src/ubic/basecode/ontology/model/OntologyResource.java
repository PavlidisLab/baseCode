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
package ubic.basecode.ontology.model;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * @author pavlidis
 */
public interface OntologyResource extends Serializable, Comparable<OntologyResource> {

    /**
     * A label, if known, otherwise null.
     */
    String getLabel();

    /**
     * A URI if known, otherwise null.
     */
    String getUri();

    /**
     * Whether the resource is marked as obsolete.
     */
    boolean isObsolete();

    /**
     * If this is result from a free-text search, a corresponding score, otherwise null.
     */
    @Nullable
    Double getScore();
}
