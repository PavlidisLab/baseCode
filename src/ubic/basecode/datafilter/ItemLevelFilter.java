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

import ubic.basecode.dataStructure.matrix.DoubleMatrix2DNamedFactory;
import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;
import ubic.basecode.dataStructure.matrix.NamedMatrix;

/**
 * Filter that removes individual values that are outside of a range. Removed values are set to NaN.
 * 
 * @author Pavlidis
 * @version $Id$
 */
public class ItemLevelFilter extends AbstractLevelFilter {

    public NamedMatrix filter( NamedMatrix data ) {
        if ( !( data instanceof DoubleMatrixNamed ) ) {
            throw new IllegalArgumentException( "Only valid for DoubleMatrixNamed" );
        }

        if ( lowCut == -Double.MAX_VALUE && highCut == Double.MAX_VALUE ) {
            log.info( "No filtering requested" );
            return data;
        }

        int numRows = data.rows();
        int numCols = data.columns();
        DoubleMatrixNamed returnval = DoubleMatrix2DNamedFactory.dense( numRows, numCols );
        for ( int i = 0; i < numRows; i++ ) {

            for ( int j = 0; j < numCols; j++ ) {

                double newVal = ( ( DoubleMatrixNamed ) data ).get( i, j );
                if ( newVal < lowCut || newVal > highCut ) {
                    newVal = Double.NaN;
                }

                returnval.set( i, j, newVal );
            }
        }
        returnval.setColumnNames( data.getColNames() );
        returnval.setRowNames( data.getRowNames() );

        return returnval;
    }

}