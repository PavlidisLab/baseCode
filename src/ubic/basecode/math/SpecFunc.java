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

import cern.jet.stat.Gamma;

/**
 * Assorted special functions, primarily concerning probability distributions. For cumBinomial use
 * cern.jet.stat.Probability.binomial.
 * <p>
 * Mostly ported from the R source tree (dhyper.c etc.), much due to Catherine Loader.
 * 
 * @see <a href="http://hoschek.home.cern.ch/hoschek/colt/V1.0.3/doc/cern/jet/stat/Gamma.html">cern.jet.stat.gamma </a>
 * @see <a
 *      href="http://hoschek.home.cern.ch/hoschek/colt/V1.0.3/doc/cern/jet/math/Arithmetic.html">cern.jet.math.arithmetic
 *      </a>
 * @author Paul Pavlidis
 * @version $Id$
 */
public class SpecFunc {

    /**
     * See dbinom_raw.
     * <hr>
     * 
     * @param x Number of successes
     * @param n Number of trials
     * @param p Probability of success
     * @return
     */
    public static double dbinom( double x, double n, double p ) {

        if ( p < 0 || p > 1 || n < 0 ) throw new IllegalArgumentException();

        return dbinom_raw( x, n, p, 1 - p );
    }

    /**
     * Ported from R (Catherine Loader)
     * <p>
     * DESCRIPTION
     * <p>
     * Given a sequence of r successes and b failures, we sample n (\le b+r) items without replacement. The
     * hypergeometric probability is the probability of x successes:
     * 
     * <pre>
     * 
     *               choose(r, x) * choose(b, n-x)
     * (x; r,b,n) =  -----------------------------  =
     *                     choose(r+b, n)
     *          
     *    dbinom(x,r,p) * dbinom(n-x,b,p)
     * = --------------------------------
     *       dbinom(n,r+b,p)
     * </pre>
     * 
     * for any p. For numerical stability, we take p=n/(r+b); with this choice, the denominator is not exponentially
     * small.
     */
    public static double dhyper( int x, int r, int b, int n ) {
        double p, q, p1, p2, p3;

        if ( r < 0 || b < 0 || n < 0 || n > r + b ) throw new IllegalArgumentException();

        if ( x < 0 ) return 0.0;

        if ( n < x || r < x || n - x > b ) return 0;
        if ( n == 0 ) return ( ( x == 0 ) ? 1 : 0 );

        p = ( ( double ) n ) / ( ( double ) ( r + b ) );
        q = ( ( double ) ( r + b - n ) ) / ( ( double ) ( r + b ) );

        p1 = dbinom_raw( x, r, p, q );
        p2 = dbinom_raw( n - x, b, p, q );
        p3 = dbinom_raw( n, r + b, p, q );

        return p1 * p2 / p3;
    }

    /**
     * Ported from R phyper.c
     * <p>
     * Sample of n balls from NR red and NB black ones; x are red
     * <p>
     * 
     * @param x - number of reds retrieved == successes
     * @param NR - number of reds in the urn. == positives
     * @param NB - number of blacks in the urn == negatives
     * @param n - the total number of objects drawn == successes + failures
     * @param lowerTail
     * @return cumulative hypergeometric distribution.
     */
    public static double phyper( int x, int NR, int NB, int n, boolean lowerTail ) {

        double d, pd;

        if ( NR < 0 || NB < 0 || n < 0 || n > NR + NB ) {
            throw new IllegalArgumentException( "Need NR>=0, NB>=0,n>=0,n>NR+NB" );
        }

        if ( x * ( NR + NB ) > n * NR ) {
            /* Swap tails. */
            int oldNB = NB;
            NB = NR;
            NR = oldNB;
            x = n - x - 1;
            lowerTail = !lowerTail;
        }

        if ( x < 0 ) return 0.0;

        d = dhyper( x, NR, NB, n );
        pd = pdhyper( x, NR, NB, n );

        return lowerTail ? d * pd : 1.0 - ( d * pd );
    }

    /**
     * Ported from bd0.c in R source.
     * <p>
     * Evaluates the "deviance part"
     * 
     * <pre>
     *    bd0(x,M) :=  M * D0(x/M) = M*[ x/M * log(x/M) + 1 - (x/M) ] =
     *         =  x * log(x/M) + M - x
     * </pre>
     * 
     * where M = E[X] = n*p (or = lambda), for x, M &gt; 0
     * <p>
     * in a manner that should be stable (with small relative error) for all x and np. In particular for x/np close to
     * 1, direct evaluation fails, and evaluation is based on the Taylor series of log((1+v)/(1-v)) with v =
     * (x-np)/(x+np).
     * 
     * @param x
     * @param np
     * @return
     */
    private static double bd0( double x, double np ) {
        double ej, s, s1, v;
        int j;

        if ( Math.abs( x - np ) < 0.1 * ( x + np ) ) {
            v = ( x - np ) / ( x + np );
            s = ( x - np ) * v;/* s using v -- change by MM */
            ej = 2 * x * v;
            v = v * v;
            for ( j = 1;; j++ ) { /* Taylor series */
                ej *= v;
                s1 = s + ej / ( ( j << 1 ) + 1 );
                if ( s1 == s ) /* last term was effectively 0 */
                return ( s1 );
                s = s1;
            }
        }
        /* else: | x - np | is not too small */
        return ( x * Math.log( x / np ) + np - x );
    }

    /**
     * Ported from R dbinom.c
     * <p>
     * Due to Catherine Loader, catherine@research.bell-labs.com.
     * <p>
     * To compute the binomial probability, call dbinom(x,n,p). This checks for argument validity, and calls
     * dbinom_raw().
     * <p>
     * dbinom_raw() does the actual computation; note this is called by other functions in addition to dbinom()).
     * <ol>
     * <li>dbinom_raw() has both p and q arguments, when one may be represented more accurately than the other (in
     * particular, in df()).
     * <li>dbinom_raw() does NOT check that inputs x and n are integers. This should be done in the calling function,
     * where necessary.
     * <li>Also does not check for 0 <= p <= 1 and 0 <= q <= 1 or NaN's. Do this in the calling function.
     * </ol>
     * <hr>
     * 
     * @param x Number of successes
     * @param n Number of trials
     * @param p Probability of success
     * @param q 1 - p
     */
    private static double dbinom_raw( double x, double n, double p, double q ) {
        double f, lc;

        if ( p == 0 ) return ( ( x == 0 ) ? 1 : 0 );
        if ( q == 0 ) return ( ( x == n ) ? 1 : 0 );

        if ( x == 0 ) {
            if ( n == 0 ) return 1;
            lc = ( p < 0.1 ) ? -bd0( n, n * q ) - n * p : n * Math.log( q );
            return ( Math.exp( lc ) );
        }
        if ( x == n ) {
            lc = ( q < 0.1 ) ? -bd0( n, n * p ) - n * q : n * Math.log( p );
            return ( Math.exp( lc ) );
        }
        if ( x < 0 || x > n ) return ( 0 );

        lc = stirlerr( n ) - stirlerr( x ) - stirlerr( n - x ) - bd0( x, n * p ) - bd0( n - x, n * q );
        f = ( 2 * Math.PI * x * ( n - x ) ) / n;

        return Math.exp( lc ) / Math.sqrt( f );
    }

    /**
     * Ported from R phyper.c
     * <p>
     * Calculate
     * 
     * <pre>
     *       phyper (x, NR, NB, n, TRUE, FALSE)
     * [log]  ----------------------------------
     *         dhyper (x, NR, NB, n, FALSE)
     * </pre>
     * 
     * without actually calling phyper. This assumes that
     * 
     * <pre>
     * x * ( NR + NB ) &lt;= n * NR
     * </pre>
     * 
     * <hr>
     * 
     * @param x - number of reds retrieved == successes
     * @param NR - number of reds in the urn. == positives
     * @param NB - number of blacks in the urn == negatives
     * @param n - the total number of objects drawn == successes + failures
     */
    private static double pdhyper( int x, int NR, int NB, int n ) {
        double sum = 0.0;
        double term = 1.0;

        while ( x > 0.0 && term >= Double.MIN_VALUE * sum ) {
            term *= ( double ) x * ( NB - n + x ) / ( n + 1 - x ) / ( NR + 1 - x );
            sum += term;
            x--;
        }

        return 1.0 + sum;
    }

    /**
     * Ported from stirlerr.c (Catherine Loader).
     * <p>
     * Note that this is the same functionality as colt's Arithemetic.stirlingCorrection. I am keeping this version for
     * compatibility with R.
     * 
     * <pre>
     *         stirlerr(n) = log(n!) - log( sqrt(2*pi*n)*(n/e)&circ;n )
     *                    = log Gamma(n+1) - 1/2 * [log(2*pi) + log(n)] - n*[log(n) - 1]
     *                    = log Gamma(n+1) - (n + 1/2) * log(n) + n - log(2*pi)/2
     * </pre>
     */
    private static double stirlerr( double n ) {

        double S0 = 0.083333333333333333333; /* 1/12 */
        double S1 = 0.00277777777777777777778; /* 1/360 */
        double S2 = 0.00079365079365079365079365; /* 1/1260 */
        double S3 = 0.000595238095238095238095238;/* 1/1680 */
        double S4 = 0.0008417508417508417508417508;/* 1/1188 */

        /*
         * error for 0, 0.5, 1.0, 1.5, ..., 14.5, 15.0.
         */
        double[] sferr_halves = new double[] { 0.0, /* n=0 - wrong, place holder only */
        0.1534264097200273452913848, /* 0.5 */
        0.0810614667953272582196702, /* 1.0 */
        0.0548141210519176538961390, /* 1.5 */
        0.0413406959554092940938221, /* 2.0 */
        0.03316287351993628748511048, /* 2.5 */
        0.02767792568499833914878929, /* 3.0 */
        0.02374616365629749597132920, /* 3.5 */
        0.02079067210376509311152277, /* 4.0 */
        0.01848845053267318523077934, /* 4.5 */
        0.01664469118982119216319487, /* 5.0 */
        0.01513497322191737887351255, /* 5.5 */
        0.01387612882307074799874573, /* 6.0 */
        0.01281046524292022692424986, /* 6.5 */
        0.01189670994589177009505572, /* 7.0 */
        0.01110455975820691732662991, /* 7.5 */
        0.010411265261972096497478567, /* 8.0 */
        0.009799416126158803298389475, /* 8.5 */
        0.009255462182712732917728637, /* 9.0 */
        0.008768700134139385462952823, /* 9.5 */
        0.008330563433362871256469318, /* 10.0 */
        0.007934114564314020547248100, /* 10.5 */
        0.007573675487951840794972024, /* 11.0 */
        0.007244554301320383179543912, /* 11.5 */
        0.006942840107209529865664152, /* 12.0 */
        0.006665247032707682442354394, /* 12.5 */
        0.006408994188004207068439631, /* 13.0 */
        0.006171712263039457647532867, /* 13.5 */
        0.005951370112758847735624416, /* 14.0 */
        0.005746216513010115682023589, /* 14.5 */
        0.005554733551962801371038690
        /* 15.0 */
        };

        double nn;

        if ( n <= 15.0 ) {
            nn = n + n;
            if ( nn == ( int ) nn ) return ( sferr_halves[( int ) nn] );
            return ( Gamma.logGamma( n + 1. ) - ( n + 0.5 ) * Math.log( n ) + n - Constants.M_LN_SQRT_2PI );
        }

        nn = n * n;
        if ( n > 500 ) return ( ( S0 - S1 / nn ) / n );
        if ( n > 80 ) return ( ( S0 - ( S1 - S2 / nn ) / nn ) / n );
        if ( n > 35 ) return ( ( S0 - ( S1 - ( S2 - S3 / nn ) / nn ) / nn ) / n );
        /* 15 < n <= 35 : */
        return ( ( S0 - ( S1 - ( S2 - ( S3 - S4 / nn ) / nn ) / nn ) / nn ) / n );
    }

}