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

import java.util.Collection;

/**
 * Compute area under precision recall
 * 
 * @author paul
 * @version $Id$
 */
public class PrecisionRecall {

    /**
     * "Average Precision is the average of the precision value obtained for the set of top $k$ documents existing after
     * each relevant document is retrieved, and this value is then averaged over information needs". The Mean Average
     * Precision is the mean of this value. It approximates the area under the Precision Recall Curve without
     * interpolation. See for example
     * http://nlp.stanford.edu/IR-book/html/htmledition/evaluation-of-ranked-retrieval-results-1.html and
     * http://en.wikipedia.org/wiki/Information_retrieval#Mean_average_precision
     * 
     * @param totalSize
     * @param ranks of the positives; LOW ranks are considered better. (e.g., rank 0 is the 'best')
     * @return
     */
    public static double meanAveragePrecision( int totalSize, Collection<Double> ranksOfPositives ) {

        int numPos = 0;
        double answer = 0.0;

        double sumPrecision = 0.0;
        double sumAP = 0.0;
        double averagePrecision = 0;
        for ( int i = 0; i < totalSize; i++ ) {

            if ( ranksOfPositives.contains( ( double ) i ) ) { // FIXME hash on double.
                /*
                 * Accumulate the precision at this # positives to compute the average.
                 */
                numPos++;
                double precision = numPos / ( i + 1.0 );
                sumPrecision += precision;

                // update the average precision.
                averagePrecision = sumPrecision / numPos;
            }

            sumAP += averagePrecision;
        }

        answer = sumAP / totalSize;

        return answer;

    }

    /**
     * Compute the average precision for the entire list.
     */
    public static double averagePrecision( int totalSize, Collection<Double> ranksOfPositives ) {

        int numPos = 0;
        double answer = 0.0;
        for ( int i = 0; i < totalSize; i++ ) {
            if ( ranksOfPositives.contains( ( double ) i ) ) { // FIXME hash on double.
                numPos++;
                double precision = numPos / ( i + 1.0 );
                answer += precision;
            }
        }

        return answer / ranksOfPositives.size();

    }
}
