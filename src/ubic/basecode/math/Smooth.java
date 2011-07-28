/*
 * The baseCode project
 * 
 * Copyright (c) 2011 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.basecode.math;

import java.util.LinkedList;
import java.util.Queue;

import cern.colt.matrix.DoubleMatrix1D;

/**
 * Methods for moving averages etc.
 * 
 * @author paul
 * @version $Id$
 */
public class Smooth {

    public static DoubleMatrix1D movingAverage( DoubleMatrix1D m, int windowSize ) {

        Queue<Double> window = new LinkedList<Double>();

        double sum = 0.0;

        assert windowSize > 0;

        DoubleMatrix1D result = m.like();
        for ( int i = 0; i < m.size(); i++ ) {

            double num = m.get( i );
            sum += num;
            window.add( num );
            if ( window.size() > windowSize ) {
                // this is bad because at the beginning, we're averaging too few points, and at the end we're
                sum -= window.remove();
            }

            if ( !window.isEmpty() ) {
                result.set( i, sum / window.size() );
            }
        }

        return result;

    }

}
