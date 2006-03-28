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

import junit.framework.TestCase;

/**
 * @version $Id$
 * @author pavlidsi
 */
public class TestByteArrayConverter extends TestCase {
    ByteArrayConverter bac;
    StringConverter sc;

    public double x = 424542.345;
    public double y = 25425.5652;
    public double z = 24524523.254;

    public int a = 424542;
    public int b = 25425;
    public int c = 24524523;

    public char u = 'k';
    public char v = 'i';
    public char w = 'r';

    double[] testD = new double[] { x, y, z };

    int[] testI = new int[] { a, b, c };

    char[] testC = new char[] { u, v, w };

    byte[] expectedBfD = new byte[] { 65, 25, -23, 121, 97, 71, -82, 20, 64, -40, -44, 100, 44, 60, -98, -19, 65, 119,
            99, 110, -76, 16, 98, 78 };

    byte[] expectedBfI = new byte[] { 0, 6, 122, 94, 0, 0, 99, 81, 1, 118 };

    byte[] expectedBfC = new byte[] { 0, 107, 0, 105, 0, 114 };

    String longDoubleString = "";
    double[] wholeBunchOfDoubles;

    /*
     * @see TestCase#setUp()
     */
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
    protected void tearDown() throws Exception {
        super.tearDown();
        longDoubleString = null;
        wholeBunchOfDoubles = null;
        bac = null;
        sc = null;
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

    // test blob -> double[]
    public void testByteArrayToDoubleConversionSpeed() throws Exception {
        byte[] lottaBytes = bac.doubleArrayToBytes( wholeBunchOfDoubles );
        bac.byteArrayToDoubles( lottaBytes );
    }

    // test string -> double[]
    public void testStringToDoubleArrayConversionSpeed() throws Exception {
        sc.stringToDoubles( longDoubleString );
    }

    // test double[] -> blob.
    public void testDoubleArrayToByteArrayConversionSpeed() throws Exception {
        bac.doubleArrayToBytes( wholeBunchOfDoubles );
    }

    // test double[] -> delimited string.
    public void testDoubleArrayToDelimitedStringConversionSpeed() throws Exception {
        sc.doubleArrayToString( wholeBunchOfDoubles );
    }

}