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

import java.util.Collection;

/**
 * Functions for calculating Receiver operator characteristics.
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
    public static double aroc( int totalSize, Collection<Double> ranks ) {
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
    public static double aroc( int totalSize, Collection<Double> ranks, int maxFP ) {
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
            if ( ranks.contains( i ) ) { // if the ith item in the ranked list is a positive.
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
    public static double rocpval( int totalSize, Collection<Double> ranks ) {
        if ( totalSize == 0 && ( ranks == null || ranks.size() == 0 ) ) return 1.0;
        return Wilcoxon.wilcoxonP( totalSize, ranks );
    }

}