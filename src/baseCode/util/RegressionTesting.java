/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
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
package baseCode.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import baseCode.dataStructure.matrix.DoubleMatrixNamed;
import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * Tools to help make regression testing easier, but also useful for other purposes.
 * 
 * @author pavlidis
 * @version $Id$
 */
public class RegressionTesting {

    private RegressionTesting() { /* block instantiation */
    }

    // private String resourcePath = "";

    /**
     * Test whether two AbstractNamedDoubleMatrix are 'close enough' to call equal.
     * 
     * @param a
     * @param b
     * @param tolerance
     * @return try if all the values in both matrices are within 'tolerance' of each other.
     */
    public static boolean closeEnough( DoubleMatrixNamed a, DoubleMatrixNamed b, double tolerance ) {
        if ( a.rows() != b.rows() || a.columns() != b.columns() ) return false;

        for ( int i = 0; i < a.rows(); i++ ) {
            for ( int j = 0; j < a.columns(); j++ ) {
                if ( Math.abs( a.getQuick( i, j ) - b.getQuick( i, j ) ) > tolerance ) return false;
            }
        }
        return true;
    }

    /**
     * @param a
     * @param b
     * @param tolerance
     */
    public static boolean closeEnough( double[] a, double[] b, double tolerance ) {
        if ( a.length != b.length ) return false;

        for ( int i = 0; i < a.length; i++ ) {
            if ( Math.abs( a[i] - b[i] ) > tolerance ) return false;
        }
        return true;

    }

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
            if ( Math.abs( a.getQuick( i ) - b.getQuick( i ) ) > tolerance ) return false;
        }
        return true;
    }

    public static boolean closeEnough( DoubleMatrix2D a, DoubleMatrix2D b, double tolerance ) {
        if ( a.rows() != b.rows() || a.columns() != b.columns() ) return false;

        for ( int i = 0; i < a.rows(); i++ ) {
            for ( int j = 0; j < a.columns(); j++ ) {
                if ( Math.abs( a.getQuick( i, j ) - b.getQuick( i, j ) ) > tolerance ) return false;
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
    public static boolean containsSame( Collection a, Collection b ) {
        if ( a.size() != b.size() ) return false;

        if ( !a.containsAll( b ) ) return false;

        return true;
    }

    /**
     * Test whether two lists contain the same items in the <em>same</em> order
     * 
     * @param a
     * @param b
     * @return
     */
    public static boolean containsSame( List a, List b ) {
        if ( a.size() != b.size() ) return false;

        Iterator ita = a.iterator();
        for ( Iterator iter = b.iterator(); iter.hasNext(); ) {
            Object elb = iter.next();
            Object ela = ita.next();
            if ( !ela.equals( elb ) ) {
                return false;
            }
        }
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

        ArrayList av = new ArrayList( a.length );
        ArrayList bv = new ArrayList( b.length );
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

        ArrayList av = new ArrayList( a.length );
        ArrayList bv = new ArrayList( b.length );

        for ( int i = 0; i < b.length; i++ ) {
            av.add( a[i] );
            bv.add( b[i] );
        }

        return av.containsAll( bv );

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