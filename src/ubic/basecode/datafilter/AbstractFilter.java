/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
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
package ubic.basecode.datafilter;

import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.dataStructure.matrix.NamedMatrix;

/**
 * Base implementation of the filter interface. Subclasses must implement the filter() method.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public abstract class AbstractFilter<R, C> implements Filter<R, C> {

    protected static final Log log = LogFactory.getLog( AbstractFilter.class );

    protected NamedMatrix<R, C> getOutputMatrix( NamedMatrix<R, C> data, int numRows, int numCols ) {
        NamedMatrix<R, C> returnval = null;

        try {
            Constructor<? extends NamedMatrix> cr = data.getClass().getConstructor(
                    new Class[] { int.class, int.class } );
            returnval = cr.newInstance( new Object[] { new Integer( numRows ), new Integer( numCols ) } );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return returnval;
    }

}