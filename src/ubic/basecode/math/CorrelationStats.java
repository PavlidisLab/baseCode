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
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.jet.math.Arithmetic;
import cern.jet.stat.Probability;

/**
 * Statistical evaluation and transformation tools for correlations.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class CorrelationStats {

    private static DoubleMatrix2D correlationPvalLookup;
    private static final double BINSIZE = 0.005; // resolution of correlation.
    // Differences smaller than this
    // are considered meaningless.
    private static final double STEPSIZE = BINSIZE * 2; // this MUST be more than
    // the binsize.
    private static final int MAXCOUNT = 1000; // maximum number of things.
    private static final double PVALCHOP = 8.0; // value by which log(pvalues)
    // are scaled before storing as
    // bytes. Values less than
    // 10^e-256/PVALCHOP are
    // 'clipped'.

    static {
        int numbins = ( int ) Math.ceil( 1.0 / BINSIZE );
        correlationPvalLookup = new SparseDoubleMatrix2D( numbins, MAXCOUNT + 1 );
    }

    /**
     * @param correl Pearson correlation.
     * @param count Number of items used to calculate the correlation. NOT the degrees of freedom.
     * @return double
     */
    public static double pvalue( double correl, int count ) {

        double acorrel = Math.abs( correl );

        if ( acorrel == 1.0 ) {
            return 0.0;
        }

        if ( acorrel == 0.0 ) {
            return 1.0;
        }

        int dof = count - 2;

        if ( dof <= 0 ) {
            return 1.0;
        }

        int bin = ( int ) Math.ceil( acorrel / BINSIZE );
        if ( count <= MAXCOUNT && correlationPvalLookup.getQuick( bin, dof ) != 0.0 ) {
            return correlationPvalLookup.getQuick( bin, dof );
        }
        double t = correlationTstat( acorrel, dof );
        double p = Probability.studentT( dof, -t );
        if ( count < MAXCOUNT ) {
            correlationPvalLookup.setQuick( bin, dof, p );
        }
        return p;

    }

    /**
     * @param correl double
     * @return int
     */
    public static int correlAsByte( double correl ) {
        if ( correl == -1.0 ) {
            return 0;
        }

        return ( int ) ( Math.ceil( ( correl + 1.0 ) * 128 ) - 1 );
    }

    /**
     * Reverse the Fisher z-transform of correlations.
     * 
     * @param r
     * @return
     */
    public static double unFisherTransform( double r ) {
        return Math.exp( 2.0 * r - 1.0 ) / Math.exp( 2.0 * r + 1.0 );
    }

    /**
     * Compute the Fisher z transform of the Pearson correlation.
     * 
     * @param r Correlation coefficient.
     * @return Fisher transform of the Correlation.
     */
    public static double fisherTransform( double r ) {
        if ( !isValidPearsonCorrelation( r ) ) {
            throw new IllegalArgumentException( "Invalid correlation " + r );
        }

        return 0.5 * Math.log( ( 1.0 + r ) / ( 1.0 - r ) );
    }

    /**
     * Fisher-transform a list of correlations.
     * 
     * @param e
     * @return
     */
    public static DoubleArrayList fisherTransform( DoubleArrayList e ) {
        DoubleArrayList r = new DoubleArrayList( e.size() );
        for ( int i = 0; i < e.size(); i++ ) {
            r.add( CorrelationStats.fisherTransform( e.getQuick( i ) ) );
        }
        return r;
    }

    /**
     * Conver a correlation p value into a value between 0 and 255 inclusive. This is done by taking the log,
     * multiplying it by a fixed value (currently 8). This means that pvalues less than 10^-32 are rounded to 10^-32.
     * 
     * @param correl double
     * @param count int
     * @return int
     */
    public static int pvalueAsByte( double correl, int count ) {
        int p = -( int ) Math.floor( PVALCHOP * Arithmetic.log10( pvalue( correl, count ) ) );

        if ( p < 0 ) {
            return 0;
        } else if ( p > 255 ) {
            return 255;
        }
        return p;
    }

    /**
     * @param pvalByte int
     * @return double
     */
    public static double byteToPvalue( int pvalByte ) {
        return Math.pow( 10.0, -( double ) pvalByte / PVALCHOP );
    }

    /**
     * @param correlByte int
     * @return double
     */
    public static double byteToCorrel( int correlByte ) {
        return correlByte / 128.0 - 1.0;
    }

    /**
     * Compute the t-statistic associated with a Pearson correlation.
     * 
     * @param correl Pearson correlation
     * @param dof Degrees of freedom (n - 2)
     * @return double
     */
    public static double correlationTstat( double correl, int dof ) {
        return correl / Math.sqrt( ( 1.0 - correl * correl ) / dof );
    }

    /**
     * Statistical comparison of two correlations. Assumes data are bivariate normal. Null hypothesis is that the two
     * correlations are equal. See Zar (Biostatistics)
     * 
     * @param correl1 First correlation
     * @param n1 Number of values used to compute correl1
     * @param correl2 Second correlation
     * @param n2 Number of values used to compute correl2
     * @return double p value.
     */
    public static double compare( double correl1, int n1, double correl2, int n2 ) {

        double Z;
        double sigma;
        double p;

        sigma = Math.sqrt( ( 1 / ( ( double ) n1 - 3 ) ) + ( 1 / ( ( double ) n2 - 3 ) ) );

        Z = Math.abs( correl1 - correl2 ) / sigma;

        p = Probability.normal( -Z ); // upper tail.

        if ( p > 0.5 ) {
            return 1.0 - p;
        }
        return p;
    }

    /**
     * Find the approximate correlation required to meet a particular pvalue. This works by simple gradient descent.
     * 
     * @param pval double
     * @param count int
     * @return double
     */
    public static double correlationForPvalue( double pval, int count ) {
        double stop = pval / 100.0;
        double err = 1.0;
        double corrguess = 1.0;
        double step = STEPSIZE;
        double preverr = 0.0;
        int maxiter = 1000;
        int iter = 0;
        while ( Math.abs( err ) > stop && step >= BINSIZE ) {
            double guess = pvalue( corrguess, count );
            if ( guess > pval ) {
                corrguess += step;
            } else {
                corrguess -= step;
            }

            if ( preverr * err < 0 ) { // opposite signs. Means we missed. Make
                // step smaller and keep going.
                step /= 2;
            }

            preverr = err;
            err = pval - guess;
            iter++;

            if ( iter > maxiter ) {
                throw new IllegalStateException( "Too many iterations" );
            }
        }
        return ( corrguess );
    }

    /**
     * Test if a value is a reasonable Pearson correlation (in the range -1 to 1; values outside of this range are
     * acceptable within a small roundoff.
     * 
     * @param r
     * @return
     */
    public static boolean isValidPearsonCorrelation( double r ) {
        return ( r + Constants.SMALL >= -1.0 && r - Constants.SMALL <= 1.0 );
    }
    /**
     * @param ival
     * @param jval
     * @return
     */
    public static double correl(double[] ival, double[] jval) {
        /* do it the old fashioned way */
        int numused = 0;
        double sxy = 0.0, sxx = 0.0, syy = 0.0, sx = 0.0, sy = 0.0;
        int length = Math.min(ival.length, jval.length);
        for ( int k = 0; k < length; k++ ) {
            double xj = ival[k];
            double yj = jval[k];
            if ( !Double.isNaN( ival[k] ) && !Double.isNaN( jval[k] ) ) {
                sx += xj;
                sy += yj;
                sxy += xj * yj;
                sxx += xj * xj;
                syy += yj * yj;
                numused++;
            }
        }
        if(numused < 2) return Double.NaN;
        double denom = ( sxx - sx * sx / numused ) * ( syy - sy * sy / numused );
        if(denom <= 0) return Double.NaN;
        double correl = ( sxy - sx * sy / numused ) / Math.sqrt( denom );
        return correl;
    }

    /**
     * @param ival
     * @param jval
     * @param meani
     * @param meanj
     * @param sqrti
     * @param sqrtj
     * @return
     */
    public static double correlFast( double[] ival, double[] jval, double meani, double meanj, double sqrti, double sqrtj ) {
        double sxy = 0.0;
        for ( int k = 0, n = ival.length; k < n; k++ ) {
            sxy += ( ival[k] - meani ) * ( jval[k] - meanj );
        }
        return sxy / ( sqrti * sqrtj );
    }

}