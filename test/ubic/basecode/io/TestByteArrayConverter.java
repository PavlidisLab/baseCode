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

import cern.colt.list.DoubleArrayList;

import junit.framework.TestCase;

/**
 * @version $Id$
 * @author pavlidis
 */
public class TestByteArrayConverter extends TestCase {
    private int a = 424542;
    private int b = 25425;

    private int c = 24524523;
    private char u = 'k';
    private char v = 'i';

    private char w = 'r';
    private double x = 424542.345;
    private double y = 25425.5652;

    private double z = 24524523.254;
    private ByteArrayConverter bac;
    private byte[] boolbytes = new byte[] { 1, 0, 1, 1, 0, 1 };

    private byte[] expectedBfC = new byte[] { 0, 107, 0, 105, 0, 114 };

    private byte[] expectedBfD = new byte[] { 65, 25, -23, 121, 97, 71, -82, 20, 64, -40, -44, 100, 44, 60, -98, -19,
            65, 119, 99, 110, -76, 16, 98, 78 };

    private byte[] expectedBfI = new byte[] { 0, 6, 122, 94, 0, 0, 99, 81, 1, 118 };

    private byte[] expectedLong = new byte[] { 0, 0, 0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 0, 7, 118, -114, 0, 0, 0, 0, 2,
            -69, 75, 37, -1, -1, -1, -1, -1, -3, 47, -61, 0, 0, 0, 0, 0, 0, 3, -75, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 101, -1, -1, -1, -1, -1, -1, -1, 7 };

    private String longDoubleString = "";

    private StringConverter sc;

    private boolean[] testbools = new boolean[] { true, false, true, true, false, true };

    private char[] testC = new char[] { u, v, w };

    private double[] testD = new double[] { x, y, z };

    private Double[] testDO = new Double[] { x, y, z };

    private int[] testI = new int[] { a, b, c };

    private DoubleArrayList tesDAL = new DoubleArrayList( testD );

    private int[] testInts = new int[] { 100, 489102, 45828901, -184381, 949, 0, 0, 1893, -249 };

    private long[] testlong = new long[] { 100L, 489102L, 45828901L, -184381L, 949L, 0L, 0L, 1893L, -249L };

    private String[] testStrings = new String[] { "foo", "bar", "profiglio", "schwartz", "000", "0", "" };

    private double[] wholeBunchOfDoubles;

    public void testBooleansToByteArray() {
        byte[] actual = bac.booleanArrayToBytes( testbools );
        for ( int i = 0; i < boolbytes.length; i++ ) {
            assertEquals( boolbytes[i], actual[i] );
        }
    }

    public void testBooleansToLongArray() {
        byte[] actual = bac.longArrayToBytes( testlong );
        for ( int i = 0; i < expectedLong.length; i++ ) {
            assertEquals( expectedLong[i], actual[i] );
        }
    }

    public void testByteArrayToBooleans() {
        boolean[] actual = bac.byteArrayToBooleans( boolbytes );
        for ( int i = 0; i < testbools.length; i++ ) {
            assertEquals( testbools[i], actual[i] );
        }
    }

    // test blob -> double[]
    public void testByteArrayToDoubleConversionSpeed() {
        byte[] lottaBytes = bac.doubleArrayToBytes( wholeBunchOfDoubles );
        bac.byteArrayToDoubles( lottaBytes );
    }

    public void testByteArrayToDoubleMatrix() {
        double[][] testm = new double[][] { { 1, 2 }, { 3, 4 }, { 5, 6 } };
        double[][] actualReturn = bac.byteArrayToDoubleMatrix( bac.doubleMatrixToBytes( testm ), 2 );
        for ( int i = 0; i < testm.length; i++ ) {
            for ( int j = 0; j < testm[i].length; j++ ) {
                assertEquals( testm[i][j], actualReturn[i][j], 0.001 );
            }
        }
    }

    /**
     * 
     *
     */
    public void testByteArrayToDoubles() {
        double[] actualReturn = bac.byteArrayToDoubles( bac.doubleArrayToBytes( testD ) );
        double[] expectedValue = testD;
        for ( int i = 0; i < actualReturn.length; i++ ) {
            assertEquals( "return value", expectedValue[i], actualReturn[i], 0 );
        }
    }

    public void testByteArrayToLongs() {
        long[] actual = bac.byteArrayToLongs( expectedLong );
        for ( int i = 0; i < testlong.length; i++ ) {
            assertEquals( testlong[i], actual[i] );
        }
    }

    public void testByteArrayToTabbedString() {
        String bools = bac.byteArrayToTabbedString( boolbytes, Boolean.class );
        assertEquals( "true\tfalse\ttrue\ttrue\tfalse\ttrue", bools );
    }

    // test double[] -> blob.
    public void testDoubleArrayToByteArrayConversionSpeed() {
        bac.doubleArrayToBytes( wholeBunchOfDoubles );
    }

    /**
     * 
     *
     */
    public void testDoubleArrayToBytes() {
        byte[] actualReturn = bac.doubleArrayToBytes( testD );
        byte[] expectedValue = expectedBfD;
        for ( int i = 0; i < expectedValue.length; i++ ) {
            assertEquals( "return value", expectedValue[i], actualReturn[i] );
        }

        actualReturn = bac.doubleArrayToBytes( testDO );
        for ( int i = 0; i < expectedValue.length; i++ ) {
            assertEquals( "return value", expectedValue[i], actualReturn[i] );
        }
        actualReturn = bac.doubleArrayToBytes( tesDAL );
        for ( int i = 0; i < expectedValue.length; i++ ) {
            assertEquals( "return value", expectedValue[i], actualReturn[i] );
        }
    }

    // test double[] -> delimited string.
    public void testDoubleArrayToDelimitedStringConversionSpeed() {
        sc.doubleArrayToString( wholeBunchOfDoubles );
    }

    public void testIntsToBytes() {
        int[] actualReturn = bac.byteArrayToInts( bac.intArrayToBytes( testInts ) );
        for ( int i = 0; i < testInts.length; i++ ) {
            assertEquals( testInts[i], actualReturn[i] );
            // System.err.println( actualReturn[i] );
        }
    }

    public void testObjectToBytes() {
        checkBytes( boolbytes, bac.toBytes( ArrayUtils.toObject( testbools ) ) );
        checkBytes( expectedBfD, bac.toBytes( ArrayUtils.toObject( testD ) ) );
        checkBytes( expectedBfC, bac.toBytes( ArrayUtils.toObject( testC ) ) );
        checkBytes( expectedBfI, bac.toBytes( ArrayUtils.toObject( testI ) ) );
    }

    public void testStringToBytes() {
        String[] actualReturn = bac.byteArrayToStrings( bac.stringArrayToBytes( testStrings ) );
        for ( int i = 0; i < testStrings.length; i++ ) {
            assertEquals( testStrings[i], actualReturn[i] );
            // System.err.println( actualReturn[i] );
        }
    }

    // test string -> double[]
    public void testStringToDoubleArrayConversionSpeed() {
        sc.stringToDoubles( longDoubleString );
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        bac = new ByteArrayConverter();
        sc = new StringConverter();

        ZipInputStream is = new ZipInputStream(
                TestByteArrayConverter.class.getResourceAsStream( "/data/melanoma_and_sarcomaMAS5.zip" ) );

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

    private void checkBytes( byte[] expected, byte[] actual ) {
        for ( int i = 0; i < expected.length; i++ ) {
            assertEquals( expected[i], actual[i] );
        }
    }

}