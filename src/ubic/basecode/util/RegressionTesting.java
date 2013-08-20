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
package ubic.basecode.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * Tools to help make regression testing easier, but also useful for other purposes.
 * 
 * @author pavlidis
 * @version $Id$
 */
public class RegressionTesting {

    private static Logger log = LoggerFactory.getLogger( RegressionTesting.class );

    /**
     * @param a expected
     * @param b measured
     * @param tolerance
     */
    public static boolean closeEnough( double[] a, double[] b, double tolerance ) {
        if ( a.length != b.length ) return false;

        for ( int i = 0; i < a.length; i++ ) {
            if ( Math.abs( a[i] - b[i] ) > tolerance ) {
                log.error( "Expected " + a[i] + " got " + b[i] + " at " + i );
                return false;
            }
        }
        return true;

    }

    // private String resourcePath = "";

    /**
     * Test whether two DoubleArrayLists are 'close enough' to call equal.
     * 
     * @param a
     * @param b
     * @param tolerance
     * @return
     */
    public static boolean closeEnough( DoubleArrayList a, DoubleArrayList b, double tolerance ) {
        if ( a.size() != b.size() ) return false;

        for ( int i = 0; i < a.size(); i++ ) {
            if ( Math.abs( a.get( i ) - b.get( i ) ) > tolerance ) return false;
        }
        return true;
    }

    /**
     * Test whether two AbstractNamedDoubleMatrix are 'close enough' to call equal.
     * 
     * @param expected
     * @param actual
     * @param tolerance
     * @return try if all the values in both matrices are within 'tolerance' of each other.
     */
    public static boolean closeEnough( DoubleMatrix<?, ?> a, DoubleMatrix<?, ?> b, double tolerance ) {
        if ( a.rows() != b.rows() || a.columns() != b.columns() ) {
            log.error( "Unequal rows and/or columns" );
            return false;
        }

        for ( int i = 0; i < a.rows(); i++ ) {
            for ( int j = 0; j < a.columns(); j++ ) {
                if ( Math.abs( a.get( i, j ) - b.get( i, j ) ) > tolerance ) {
                    log.error( "Expected: " + a.get( i, j ) + ", actual=" + b.get( i, j ) );
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param a
     * @param b
     * @param tolerance
     * @return
     */
    public static boolean closeEnough( DoubleMatrix1D a, DoubleMatrix1D b, double tolerance ) {
        return closeEnough( a.toArray(), b.toArray(), tolerance );
    }

    public static boolean closeEnough( DoubleMatrix2D a, DoubleMatrix2D b, double tolerance ) {
        if ( a.rows() != b.rows() || a.columns() != b.columns() ) return false;

        for ( int i = 0; i < a.rows(); i++ ) {
            for ( int j = 0; j < a.columns(); j++ ) {
                if ( Math.abs( a.get( i, j ) - b.get( i, j ) ) > tolerance ) return false;
            }
        }
        return true;
    }

    /**
     * Test whether two object arrays are the same.
     * 
     * @param a
     * @param b
     * @return
     */
    public static boolean closeEnough( Object[] a, Object[] b ) {
        if ( a.length != b.length ) {
            return false;
        }

        for ( int i = 0; i < a.length; i++ ) {
            if ( !a[i].equals( b[i] ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Test whether two collections contain the same items.
     * 
     * @param a
     * @param b
     * @return
     */
    public static boolean containsSame( Collection<? extends Object> a, Collection<? extends Object> b ) {
        if ( a.size() != b.size() ) return false;

        if ( !a.containsAll( b ) ) return false;

        return true;
    }

    /**
     * Test whether two double arrays contain the same items in any order (tolerance is ZERO)
     * 
     * @param a
     * @param b
     * @return
     */
    public static boolean containsSame( double[] a, double[] b ) {
        if ( a.length != b.length ) return false;

        List<Double> av = new ArrayList<Double>( a.length );
        List<Double> bv = new ArrayList<Double>( b.length );
        for ( int i = 0; i < b.length; i++ ) {
            av.add( new Double( a[i] ) );
            bv.add( new Double( b[i] ) );
        }

        return av.containsAll( bv );
    }

    /**
     * Test whether two object arrays contain the same items in any order. The arrays are treated as Sets - repeats are
     * not considered.
     * 
     * @param a
     * @param b
     * @return
     */
    public static boolean containsSame( Object[] a, Object[] b ) {
        if ( a.length != b.length ) return false;

        List<Object> av = new ArrayList<Object>( a.length );
        List<Object> bv = new ArrayList<Object>( b.length );

        for ( int i = 0; i < b.length; i++ ) {
            av.add( a[i] );
            bv.add( b[i] );
        }

        return av.containsAll( bv );

    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    public static String readTestResult( File file ) throws IOException {
        BufferedReader buf = new BufferedReader( new FileReader( file ) );
        String line = "";
        StringBuffer testOutput = new StringBuffer( line );
        while ( ( line = buf.readLine() ) != null ) {
            testOutput.append( line + "\n" );
        }
        buf.close();
        return testOutput.toString();
    }

    /**
     * @param istream
     * @return
     * @throws IOException
     */
    public static String readTestResult( InputStream istream ) throws IOException {
        if ( istream == null ) {
            throw new IllegalStateException( "Null stream" );
        }

        BufferedReader buf = new BufferedReader( new InputStreamReader( istream ) );
        String line = "";
        StringBuffer testOutput = new StringBuffer( line );
        while ( ( line = buf.readLine() ) != null ) {
            testOutput.append( line + "\n" );
        }
        buf.close();
        return testOutput.toString();
    }

    /**
     * @param resourceName
     * @return the contents of the resource as a String
     * @throws IOException
     */
    public static String readTestResult( String resourceName ) throws IOException {
        InputStream istream = RegressionTesting.class.getResourceAsStream( resourceName );

        if ( istream == null ) return null;

        String result = readTestResult( istream );
        istream.close();
        return result;

    }

    /**
     * @throws IOException
     * @param fileName - the full path of the file to be read.
     * @return
     */
    public static String readTestResultFromFile( String fileName ) throws IOException {
        InputStream is = new FileInputStream( fileName );
        return readTestResult( is );
    }

    /**
     * Test whether two double arrays contain the same items in the same order
     * 
     * @param a
     * @param b
     * @return
     */
    public static boolean sameArray( int[] a, int[] b ) {
        if ( a.length != b.length ) return false;
        for ( int i = 0; i < b.length; i++ ) {
            if ( b[i] != a[i] ) return false;
        }
        return true;
    }

    public static void writeTestResult( String result, String fileName ) throws IOException {

        BufferedWriter buf = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File( fileName ) ) ) );
        BufferedReader resultBuf = new BufferedReader( new StringReader( result ) );

        String line = null;
        while ( ( line = resultBuf.readLine() ) != null ) {
            buf.write( line + "\n" );
        }
        buf.close();
        resultBuf.close();
    }

    private RegressionTesting() { /* block instantiation */
    }

    /**
     * Convenience for using Stuart D. Gathman's Diff.
     * 
     * @param expected String
     * @param actual String
     * @return String edit list
     */
    /**
     * public static String regress( String expected, String actual ) { Diff diff = new Diff( new Object[] {expected} ,
     * new Object[] {actual} ); Diff.change script = diff.diff_2( false ); DiffPrint.Base p = new
     * DiffPrint.UnifiedPrint( new Object[] {expected} , new Object[] {actual} ); StringWriter wtr = new StringWriter();
     * p.setOutput( wtr ); p.print_script( script ); return wtr.toString(); }
     */

}