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
package ubic.basecode.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.ArrayUtils;

import junit.framework.TestCase;

/**
 * @version $Id$
 * @author pavlidis
 */
public class TestByteArrayConverter extends TestCase {
    public double x = 424542.345;
    public double y = 25425.5652;

    public double z = 24524523.254;
    public int a = 424542;
    public int b = 25425;

    public int c = 24524523;
    public char u = 'k';
    public char v = 'i';

    public char w = 'r';
    ByteArrayConverter bac;
    StringConverter sc;

    double[] testD = new double[] { x, y, z };

    int[] testI = new int[] { a, b, c };

    char[] testC = new char[] { u, v, w };

    boolean[] testbools = new boolean[] { true, false, true, true, false, true };

    long[] testlong = new long[] { 100L, 489102L, 45828901L, -184381L, 949L, 0L, 0L, 1893L, -249L };

    byte[] expectedBfD = new byte[] { 65, 25, -23, 121, 97, 71, -82, 20, 64, -40, -44, 100, 44, 60, -98, -19, 65, 119,
            99, 110, -76, 16, 98, 78 };

    byte[] expectedBfI = new byte[] { 0, 6, 122, 94, 0, 0, 99, 81, 1, 118 };

    byte[] expectedBfC = new byte[] { 0, 107, 0, 105, 0, 114 };

    byte[] boolbytes = new byte[] { 1, 0, 1, 1, 0, 1 };

    byte[] expectedLong = new byte[] { 0, 0, 0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 0, 7, 118, -114, 0, 0, 0, 0, 2, -69, 75,
            37, -1, -1, -1, -1, -1, -3, 47, -61, 0, 0, 0, 0, 0, 0, 3, -75, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 7, 101, -1, -1, -1, -1, -1, -1, -1, 7 };

    String longDoubleString = "";

    String[] testStrings = new String[] { "foo", "bar", "profiglio", "schwartz", "000", "0", "" };

    int[] testInts = new int[] { 100, 489102, 45828901, -184381, 949, 0, 0, 1893, -249 };

    double[] wholeBunchOfDoubles;

    // test blob -> double[]
    public void testByteArrayToDoubleConversionSpeed() throws Exception {
        byte[] lottaBytes = bac.doubleArrayToBytes( wholeBunchOfDoubles );
        bac.byteArrayToDoubles( lottaBytes );
    }

    /**
     * 
     *
     */
    public void testByteArrayToDoubles() throws Exception {
        double[] actualReturn = bac.byteArrayToDoubles( bac.doubleArrayToBytes( testD ) );
        double[] expectedValue = testD;
        for ( int i = 0; i < actualReturn.length; i++ ) {
            assertEquals( "return value", expectedValue[i], actualReturn[i], 0 );
        }
    }

    public void testByteArrayToDoubleMatrix() throws Exception {
        double[][] testm = new double[][] { { 1, 2 }, { 3, 4 }, { 5, 6 } };
        double[][] actualReturn = bac.byteArrayToDoubleMatrix( bac.doubleMatrixToBytes( testm ), 2 );
        for ( int i = 0; i < testm.length; i++ ) {
            for ( int j = 0; j < testm[i].length; j++ ) {
                assertEquals( testm[i][j], actualReturn[i][j], 0.001 );
            }
        }
    }

    // test double[] -> blob.
    public void testDoubleArrayToByteArrayConversionSpeed() throws Exception {
        bac.doubleArrayToBytes( wholeBunchOfDoubles );
    }

    /**
     * 
     *
     */
    public void testDoubleArrayToBytes() throws Exception {
        byte[] actualReturn = bac.doubleArrayToBytes( testD );
        byte[] expectedValue = expectedBfD;
        for ( int i = 0; i < expectedValue.length; i++ ) {
            assertEquals( "return value", expectedValue[i], actualReturn[i] );
        }
    }

    // test double[] -> delimited string.
    public void testDoubleArrayToDelimitedStringConversionSpeed() throws Exception {
        sc.doubleArrayToString( wholeBunchOfDoubles );
    }

    public void testIntsToBytes() throws Exception {
        int[] actualReturn = bac.byteArrayToInts( bac.intArrayToBytes( testInts ) );
        for ( int i = 0; i < testInts.length; i++ ) {
            assertEquals( testInts[i], actualReturn[i] );
            // System.err.println( actualReturn[i] );
        }
    }

    public void testStringToBytes() throws Exception {
        String[] actualReturn = bac.byteArrayToStrings( bac.stringArrayToBytes( testStrings ) );
        for ( int i = 0; i < testStrings.length; i++ ) {
            assertEquals( testStrings[i], actualReturn[i] );
            // System.err.println( actualReturn[i] );
        }
    }

    public void testByteArrayToBooleans() throws Exception {
        boolean[] actual = bac.byteArrayToBooleans( boolbytes );
        for ( int i = 0; i < testbools.length; i++ ) {
            assertEquals( testbools[i], actual[i] );
        }
    }

    public void testBooleansToByteArray() throws Exception {
        byte[] actual = bac.booleanArrayToBytes( testbools );
        for ( int i = 0; i < boolbytes.length; i++ ) {
            assertEquals( boolbytes[i], actual[i] );
        }
    }

    public void testByteArrayToLongs() throws Exception {
        long[] actual = bac.byteArrayToLongs( expectedLong );
        for ( int i = 0; i < testlong.length; i++ ) {
            assertEquals( testlong[i], actual[i] );
        }
    }

    public void testBooleansToLongArray() throws Exception {
        byte[] actual = bac.longArrayToBytes( testlong );
        for ( int i = 0; i < expectedLong.length; i++ ) {
            assertEquals( expectedLong[i], actual[i] );
        }
    }

    // test string -> double[]
    public void testStringToDoubleArrayConversionSpeed() throws Exception {
        sc.stringToDoubles( longDoubleString );
    }

    public void testByteArrayToTabbedString() throws Exception {
        String bools = bac.byteArrayToTabbedString( boolbytes, Boolean.class );
        assertEquals( "true\tfalse\ttrue\ttrue\tfalse\ttrue", bools );
    }

    public void testObjectToBytes() throws Exception {
        checkBytes( boolbytes, bac.toBytes( ArrayUtils.toObject( testbools ) ) );
        checkBytes( expectedBfD, bac.toBytes( ArrayUtils.toObject( testD ) ) );
        checkBytes( expectedBfC, bac.toBytes( ArrayUtils.toObject( testC ) ) );
        checkBytes( expectedBfI, bac.toBytes( ArrayUtils.toObject( testI ) ) );
    }

    private void checkBytes( byte[] expected, byte[] actual ) {
        for ( int i = 0; i < expected.length; i++ ) {
            assertEquals( expected[i], actual[i] );
        }
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        bac = new ByteArrayConverter();
        sc = new StringConverter();

        ZipInputStream is = new ZipInputStream( TestByteArrayConverter.class
                .getResourceAsStream( "/data/melanoma_and_sarcomaMAS5.zip" ) );

        is.getNextEntry();

        BufferedReader br = new BufferedReader( new InputStreamReader( is ) );

        StringBuffer buf = new StringBuffer();
        String line;
        br.readLine(); // ditch the first row.
        int k = 0;
        while ( ( line = br.readLine() ) != null ) {
            buf.append( line.split( "\t", 2 )[1] + "\t" ); // so we get a very long delimited string, albeit with a
            // trailing tab.
            k++;
            if ( k > 100 ) break;
        }

        longDoubleString = buf.toString();

        if ( longDoubleString == null ) {
            throw new IllegalStateException( "Couldn't setup string" );
        }

        wholeBunchOfDoubles = sc.stringToDoubles( longDoubleString );
        br.close();
        is.close();

    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        longDoubleString = null;
        wholeBunchOfDoubles = null;
        bac = null;
        sc = null;
    }

}