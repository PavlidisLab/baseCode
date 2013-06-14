/*
 * The baseCode project
 * 
 * Copyright (c) 2007 University of British Columbia
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
package ubic.basecode.math.distribution;

import hep.aida.IAxis;
import hep.aida.ref.FixedAxis;
import hep.aida.ref.Histogram1D;
import cern.jet.random.Uniform;

/**
 * Implements a simple (not very efficient) sampling algorithm for empirical distributions.
 * 
 * @author Paul
 * @version $Id$
 */
public class HistogramSampler {

    private double[] cdf;
    private IAxis histogramAxis;
    private Uniform uniformDist;

    /**
     * @param histogram
     */
    public HistogramSampler( Histogram1D histogram ) {
        this.uniformDist = new Uniform( 0, 1, ( int ) System.currentTimeMillis() );
        this.histogramAxis = histogram.xAxis();
        this.cdf = new double[histogramAxis.bins()];

        double sum = histogram.sumBinHeights();
        double prev = 0.0;
        for ( int i = 0; i < histogramAxis.bins(); i++ ) {
            cdf[i] = histogram.binHeight( i ) / sum + prev;
            prev = cdf[i];
        }
    }

    /**
     * @param counts Array of counts of how many events there are for each bin, from min to max.
     * @param min Minimum of range histogram covers (value of the start of the first bin)
     * @param max Maximum of range histogram covers (value at the start of the last bin)
     */
    public HistogramSampler( int[] counts, double min, double max ) {
        histogramAxis = new FixedAxis( counts.length - 1, min, max );
        uniformDist = new Uniform( 0, 1, ( int ) System.currentTimeMillis() );
        int sum = 0;
        for ( int element : counts ) {
            sum += element;
        }

        this.cdf = new double[counts.length];

        double prev = 0.0;
        for ( int i = 0; i < counts.length; i++ ) {
            cdf[i] = ( double ) counts[i] / sum + prev;
            prev = cdf[i];
        }

    }

    public double nextSample() {
        /*
         * Get a value from the uniform density. Find the cdf bin which contains that value. Maybe there is a faster way
         * to do this.
         */
        double u = uniformDist.nextDouble();
        for ( int i = 0; i < cdf.length; i++ ) {
            if ( cdf[i] >= u ) {
                double bin = histogramAxis.binLowerEdge( i );
                return bin;
            }
        }
        return histogramAxis.binLowerEdge( cdf.length - 1 );

    }
}
