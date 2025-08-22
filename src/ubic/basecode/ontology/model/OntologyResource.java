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

import org.jspecify.annotations.Nullable;

/**
 * @author pavlidis
 */
public interface OntologyResource {

    /**
     * A URI if known, otherwise null.
     */
    @Nullable
    String getUri();

    /**
     * A local name for this resource.
     */
    String getLocalName();

    /**
     * A label, if known, otherwise null.
     */
    @Nullable
    String getLabel();

    /**
     * A comment for the resource, if available, otherwise null.
     */
    @Nullable
    String getComment();

    /**
     * Whether the resource is marked as obsolete.
     */
    boolean isObsolete();

    /**
     * Unwrap the underlying implementation of the ontology resource.
     *
     * @throws ClassCastException if the implementation type does not match the given class
     */
    <T> T unwrap( Class<T> clazz ) throws ClassCastException;
}
