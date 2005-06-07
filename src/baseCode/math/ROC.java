/*
 * The baseCode project
 * 
 * Copyright (c) 2005 Columbia University
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package baseCode.math;

import java.util.Set;

import cern.jet.stat.Probability;

/**
 * Functions for calculating Receiver operator characteristics.
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class ROC {

    /**
     * Calculate area under ROC. The input is the total number of items in the data, and the ranks of the positives in
     * the current ranking. LOW ranks are considered better. (e.g., rank 0 is the 'best')
     * 
     * @param totalSize int
     * @param ranks Map
     * @return AROC
     */
    public static double aroc( int totalSize, Set ranks ) {
        return ROC.aroc( totalSize, ranks, -1 );
    }

    /**
     * Calculate area under ROC, up to a given number of False positives. The input is the total number of items in the
     * data, and the ranks of the positives in the current ranking. LOW ranks are considered better. (e.g., rank 0 is
     * the 'best')
     * 
     * @param totalSize int
     * @param ranks Map
     * @param maxFP - the maximum number of false positives to see before stopping. Set to 50 to get the Gribskov roc50.
     *        If maxFP <= 0, it is ignored.
     * @return AROC
     */
    public static double aroc( int totalSize, Set ranks, int maxFP ) {
        int numPosSeen = 0;
        int numNegSeen = 0;
        int targetSize = ranks.size();

        if ( targetSize == 0 ) {
            return 0.0;
        }

        if ( totalSize <= 0 ) {
            throw new IllegalArgumentException( "Total size must be positive. ( received " + totalSize + ")" );
        }

        double result = 0.0;
        for ( int i = 0; i < totalSize; i++ ) {
            if ( ranks.contains( new Integer( i ) ) ) { // if the ith item in the ranked list is a positive.
                numPosSeen++;
            } else {
                result += numPosSeen;
                numNegSeen++;
                if ( maxFP > 0 && numNegSeen >= maxFP ) {
                    break;
                }
            }
        }

        if ( numPosSeen == 0 ) return 0.0;

        if ( maxFP > 0 ) {
            return result / ( targetSize * numNegSeen );
        }
        return result / ( numPosSeen * ( totalSize - targetSize ) );

    }

    /**
     * For an AROC value, calculates a p value. Uses fact that ROC is equivalent to the Wilcoxon rank sum test.
     * 
     * @param numpos How many positives are in the data.
     * @param Ranks of objects in the class.
     * @return The p value.
     */
    public static double rocpval( int totalSize, Set ranks ) {
        return Wilcoxon.wilcoxonP( totalSize, ranks );
        // double stdev = Math.exp( -0.5 * ( Math.log( numpos ) + 1 ) );
        // double z = ( aroc - 0.5 ) / stdev;
        //
        // /* We are only interested in the upper tails. */
        // if ( z < 0.0 ) {
        // z = 0.0;
        // }
        // return 1.0 - Probability.normal( z );
    }

}