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
package ubic.basecode.math;

import cern.colt.list.DoubleArrayList;

/**
 * Alternative distance and similarity metrics for vectors.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class Distance {

    /**
     * Calculate the Manhattan distance between two vectors.
     * 
     * @param x DoubleArrayList
     * @param y DoubleArrayList
     * @return Manhattan distance between x and y
     */
    public double manhattanDistance( DoubleArrayList x, DoubleArrayList y ) {
        int j;
        double sum = 0.0;
        int numused = 0;

        if ( x.size() != y.size() ) {
            throw new ArithmeticException();
        }

        int length = x.size();
        for ( j = 0; j < length; j++ ) {
            if ( !Double.isNaN( x.elements()[j] ) && !Double.isNaN( y.elements()[j] ) ) {
                sum += Math.abs( x.elements()[j] - y.elements()[j] );
                numused++;
            }
        }
        return sum;
    }

    /**
     * Calculate the Euclidean distance between two vectors.
     * 
     * @param x DoubleArrayList
     * @param y DoubleArrayList
     * @return Euclidean distance between x and y
     */
    public double euclDistance( DoubleArrayList x, DoubleArrayList y ) {
        int j;
        double sum;
        int numused;
        sum = 0.0;
        numused = 0;

        if ( x.size() != y.size() ) {
            throw new ArithmeticException();
        }

        int length = x.size();

        for ( j = 0; j < length; j++ ) {
            if ( !Double.isNaN( x.elements()[j] ) && !Double.isNaN( y.elements()[j] ) ) {
                sum += Math.pow( ( x.elements()[j] - y.elements()[j] ), 2 );
                numused++;
            }
        }
        if ( sum == 0.0 ) {
            return 0.0;
        }
        return Math.sqrt( sum );
    }

    /**
     * Spearman Rank Correlation. This does the rank transformation of the data.
     * 
     * @param x DoubleArrayList
     * @param y DoubleArrayList
     * @return Spearman's rank correlation between x and y.
     */
    public static double spearmanRankCorrelation( DoubleArrayList x, DoubleArrayList y ) {
        double sum = 0.0;

        int n = x.size();
        if ( n != y.size() ) {
            throw new ArithmeticException( "Unequal vector lengths" );
        }

        DoubleArrayList rx = Rank.rankTransform( x );
        DoubleArrayList ry = Rank.rankTransform( y );

        for ( int j = 0; j < n; j++ ) {
            sum += ( rx.elements()[j] - ry.elements()[j] * ( rx.elements()[j] - ry.elements()[j] ) );
        }

        return 1.0 - 6.0 * sum / ( Math.pow( n, 3 ) - n );
    }

    /**
     * Highly optimized implementation of the Pearson correlation. The inputs must be standardized - mean zero, variance
     * one, without any missing values.
     * 
     * @param xe A standardized vector
     * @param ye A standardized vector
     * @return Pearson correlation coefficient.
     */
    public static double correlationOfStandardized( double[] xe, double[] ye ) {
        double sxy = 0.0;
        for ( int i = 0, n = xe.length; i < n; i++ ) {
            double xj = xe[i];
            double yj = ye[i];
            sxy += xj * yj;
        }

        return sxy / xe.length;
    }

    /**
     * Like correlationofNormedFast, but takes DoubleArrayLists as inputs, handles missing values correctly, and does
     * more error checking. Assumes the data has been converted to z scores already.
     * 
     * @param x A standardized vector
     * @param y A standardized vector
     * @return The Pearson correlation between x and y.
     */
    public static double correlationOfStandardized( DoubleArrayList x, DoubleArrayList y ) {

        if ( x.size() != y.size() ) {
            throw new IllegalArgumentException( "Array lengths must be the same" );
        }

        double[] xe = x.elements();
        double[] ye = y.elements();
        double sxy = 0.0;
        int length = 0;
        for ( int i = 0, n = x.size(); i < n; i++ ) {
            double xj = xe[i];
            double yj = ye[i];

            if ( Double.isNaN( xj ) || Double.isNaN( yj ) ) {
                continue;
            }

            sxy += xj * yj;
            length++;
        }

        if ( length == 0 ) {
            return -2.0; // flag of illegal value.
        }
        return sxy / length;
    }
}