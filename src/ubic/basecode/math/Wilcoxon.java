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

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cern.colt.list.DoubleArrayList;
import cern.jet.math.Arithmetic;
import cern.jet.stat.Probability;

/**
 * Implements methods from supplementary file I of "Comparing functional annotation analyses with Catmap", Thomas
 * Breslin, Patrik Ed�n and Morten Krogh, BMC Bioinformatics 2004, 5:193 doi:10.1186/1471-2105-5-193
 * <p>
 * Note that in the Catmap code, zero-based ranks are used, but these are converted to one-based before computation of
 * pvalues. Therefore this code uses one-based ranks throughout.
 * 
 * @author pavlidis
 * @version Id
 * @see ROC
 */
public class Wilcoxon {

    private static final Map<CacheKey, BigInteger> cache = new ConcurrentHashMap<CacheKey, BigInteger>();

    /**
     * For smaller sample sizes, we compute exactly. Below 1e5 we start to notice some loss of precision (like one part
     * in 1e5). Setting this too high really slows things down for high-throughput applications.
     */
    private static double LIMIT_FOR_APPROXIMATION = 1e5;

    private static Log log = LogFactory.getLog( Wilcoxon.class.getName() );

    /**
     * Convenience method that computes a p-value using input of two double arrays. They must not contain missing values
     * or ties.
     * 
     * @param a
     * @param b
     * @return
     */
    public static double exactWilcoxonP( double[] a, double[] b ) {
        int fullLength = a.length + b.length;
        // need sum of A's ranks with respect to all
        DoubleArrayList ad = new DoubleArrayList( a );
        DoubleArrayList bb = new DoubleArrayList( b );
        ad.addAllOf( bb );
        DoubleArrayList abR = Rank.rankTransform( ad );
        int aSum = 0;
        for ( int i = 0; i < a.length; i++ ) {
            aSum += abR.get( i );
        }
        return pExact( fullLength, a.length, aSum );
    }

    /**
     * @param N
     * @param n
     * @param R
     * @return
     */
    public static double exactWilcoxonP( int N, int n, int R ) {
        return pExact( N, n, R );
    }

    /**
     * Only use when you know there are no ties.
     * 
     * @param N
     * @param n
     * @param R
     * @return
     */
    public static double wilcoxonP( int N, int n, int R ) {
        return wilcoxonP( N, n, R, false );
    }

    /**
     * @param N number of all Items
     * @param n number of class Items
     * @param R rankSum for items in the class. (one-based)
     * @param ties set to true if you know there are ties
     * @return
     */
    public static double wilcoxonP( int N, int n, int R, boolean ties ) {

        if ( n > N ) throw new IllegalArgumentException( "n must be less than N (n=" + n + ", N=" + N + ")" );

        if ( n == 0 && N == 0 ) return 1.0;

        if ( ( !ties )
                && ( ( long ) N * n * R <= LIMIT_FOR_APPROXIMATION || R < N
                        && n * Math.pow( R, 2 ) <= LIMIT_FOR_APPROXIMATION ) ) {
            if ( log.isDebugEnabled() ) log.debug( "Using exact method (" + N * n * R + ")" );
            return pExact( N, n, R );
        }

        double p = pGaussian( N, n, R );

        if ( p < 0.1 && Math.pow( n, 2 ) * R / N <= 1e5 ) {
            if ( log.isDebugEnabled() ) log.debug( "Using volume method (" + N * n * R + ")" );
            return pVolume( N, n, R );
        }

        if ( log.isDebugEnabled() ) log.debug( "Using gaussian method (" + N * n * R + ")" );
        return p;
    }

    /**
     * @param N total number of items (in and not in the class)
     * @param ranks of items in the class (one-based)
     * @return
     */
    public static double wilcoxonP( int N, List<Double> ranks ) {

        /*
         * Check for ties; cannot compute exact when there are ties.
         */
        Collections.sort( ranks );
        Double p = null;
        boolean ties = false;
        for ( Double r : ranks ) {
            if ( p != null ) {
                if ( r.equals( p ) ) {
                    ties = true;
                    break;
                }
            }
            p = r;
        }

        return wilcoxonP( N, ranks.size(), Rank.rankSum( ranks ), ties );
    }

    private static void addToCache( int N, int n, int R, BigInteger value ) {
        cache.put( new CacheKey( N, n, R ), value );
    }

    /**
     * @param n0
     * @param n02
     * @param r0
     * @return
     */
    private static boolean cacheContains( int N, int n, int R ) {
        return cache.containsKey( new CacheKey( N, n, R ) );
    }

    /**
     * Direct port from catmap code. Exact computation of the number of ways n items can be drawn from a total of N
     * items with a rank sum of R or better (lower).
     * 
     * @param N0
     * @param n0
     * @param R0 rank sum, 1-based (best rank is 1)
     * @return
     */
    private static BigInteger computeA__( int N0, int n0, int R0 ) {
        cache.clear();
        if ( R0 < N0 ) N0 = R0;

        // if ( cacheContains( N0, n0, R0 ) ) {
        // return getFromCache( N0, n0, R0 );
        // }

        if ( N0 == 0 && n0 == 0 ) return BigInteger.ONE;

        for ( int N = 1; N <= N0; N++ ) {
            if ( N > 2 ) removeFromCache( N - 2 );

            /* n has to be less than N */
            int min_n = Math.max( 0, n0 + N - N0 );
            int max_n = Math.min( n0, N );

            assert min_n >= 0;
            assert max_n >= min_n;

            for ( int n = min_n; n <= max_n; n++ ) {

                /* The rank sum is in the interval n(n+1)/2 to n(2N-n+1)/2. Other values need not be looked at. */
                int bestPossibleRankSum = n * ( n + 1 ) / 2;
                int worstPossibleRankSum = n * ( 2 * N - n + 1 ) / 2;

                /* Ensure value looked at is valid for the original set of parameters. */
                int min_r = Math.max( bestPossibleRankSum, R0 - ( N0 + N + 1 ) * ( N0 - N ) / 2 );
                int max_r = Math.min( worstPossibleRankSum, R0 );

                assert min_r >= 0;

                if ( min_r > max_r ) {
                    throw new IllegalStateException( min_r + " > " + max_r );
                }

                assert max_r >= min_r : String.format( "max_r %d < min_r %d for N=%d, n=%d, r=%d", max_r, min_r, N0,
                        n0, R0 );

                /* R greater than this, have already computed it in parts */
                int foo = n * ( 2 * N - n - 1 ) / 2;

                /* R less than this, we have already computed it in parts */
                int bar = N + ( n - 1 ) * n / 2;

                for ( int r = min_r; r <= max_r; r++ ) {

                    if ( n == 0 || n == N || r == bestPossibleRankSum ) {
                        addToCache( N, n, r, BigInteger.ONE );

                    } else if ( r > foo ) {
                        addToCache( N, n, r, getFromCache( N - 1, n, foo ).add( getFromCache( N - 1, n - 1, r - N ) ) );

                    } else if ( r < bar ) {
                        addToCache( N, n, r, getFromCache( N - 1, n, r ) );

                    } else {
                        addToCache( N, n, r, getFromCache( N - 1, n, r ).add( getFromCache( N - 1, n - 1, r - N ) ) );
                    }
                }
            }
        }
        return getFromCache( N0, n0, R0 );
    }

    /**
     * @param N
     * @param n
     * @param R
     * @return
     */
    private static BigInteger getFromCache( int N, int n, int R ) {

        if ( !cacheContains( N, n, R ) ) {
            throw new IllegalStateException( "No value stored for N=" + N + ", n=" + n + ", R=" + R );
        }
        return cache.get( new CacheKey( N, n, R ) );
    }

    /**
     * @param N
     * @param n
     * @param r rank sum, 1-based (best rank is 1).
     * @return
     */
    private static double pExact( int N, int n, int R ) {
        return computeA__( N, n, R ).doubleValue() / Arithmetic.binomial( N, n );
    }

    /**
     * @param N
     * @param n
     * @param R
     * @return Upper-tail probability for Wilcoxon rank-sum test.
     */
    private static double pGaussian( long N, long n, long R ) {
        if ( n > N ) throw new IllegalArgumentException( "n must be smaller than N" );
        double mean = n * ( N + 1 ) / 2.0;
        double var = n * ( N - n ) * ( N + 1 ) / 12.0;
        if ( log.isDebugEnabled() ) log.debug( "Mean=" + mean + " Var=" + var + " R=" + R );
        return Probability.normal( 0.0, var, R - mean );
    }

    /**
     * Directly ported from catmap.
     * 
     * @param N
     * @param n
     * @param R
     * @return
     */
    private static double pVolume( int N, int n, int R ) {

        double t = R / ( double ) N;

        if ( t < 0 ) return 0.0;
        if ( t >= n ) return 1.0;
        double[] logFactors = new double[n + 1];
        logFactors[0] = 0.0;
        logFactors[1] = 0.0;
        for ( int i = 2; i <= n; i++ ) {
            logFactors[i] = logFactors[i - 1] + Math.log( i );
        }

        int kMax = ( int ) t;
        double[][] C = new double[n][];
        for ( int i = 0; i < n; i++ ) {
            C[i] = new double[n + 1];
            C[0][i] = 0.0;
        }

        C[0][n] = Math.exp( -logFactors[n] );
        for ( int k = 1; k <= kMax; k++ ) {
            for ( int a = 0; a < n; a++ ) {
                for ( int j = a; j <= n; j++ ) {
                    C[k][a] += C[k - 1][j] * Math.exp( logFactors[j] - logFactors[a] - logFactors[j - a] );
                }
            }
            double b = Math.exp( -logFactors[k] - logFactors[n - 1 - k] ) / n;
            C[k][n] = k % 2 != 0 ? -b : b;
        }

        double result = 0.0;
        for ( int a = 0; a <= n; a++ ) {
            result += C[kMax][a] * Math.pow( t - kMax, a );
        }
        return result;

    }

    /**
     * @param i
     */
    private static void removeFromCache( int N ) {
        cache.remove( N );
    }

}

class CacheKey {
    private int n;
    private int N;
    private int R;

    public CacheKey( int N, int n, int R ) {
        super();
        this.N = N;
        this.n = n;
        this.R = R;
    }

    @Override
    public boolean equals( Object obj ) {
        CacheKey other = ( CacheKey ) obj;

        if ( N != other.N ) return false;
        if ( n != other.n ) return false;
        if ( R != other.R ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + N;
        result = prime * result + n;
        result = prime * result + R;
        return result;
    }

}