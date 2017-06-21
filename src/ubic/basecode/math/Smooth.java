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
 */
public class Smooth {

    /**
     * Simple moving average that sums the points "backwards".
     * 
     * @param m
     * @param windowSize
     * @return
     */
    public static DoubleMatrix1D movingAverage( DoubleMatrix1D m, int windowSize ) {

        Queue<Double> window = new LinkedList<>();

        double sum = 0.0;

        assert windowSize > 0;

        DoubleMatrix1D result = m.like();
        for ( int i = 0; i < m.size(); i++ ) {

            double num = m.get( i );
            sum += num;
            window.add( num );
            if ( window.size() > windowSize ) {

                sum -= window.remove();
            }

            if ( !window.isEmpty() ) {
                // if ( window.size() == windowSize ) {
                result.set( i, sum / window.size() );
            } else {
                result.set( i, Double.NaN );
            }
        }

        return result;

    }
}
