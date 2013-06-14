/*
 * The baseCode project
 * 
 * Copyright (c) 2008 University of British Columbia
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
package ubic.basecode.io.writer;

import hep.aida.ref.Histogram1D;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;
import ubic.basecode.util.RegressionTesting;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TestHistogramWriter extends TestCase {

    Histogram1D m = new Histogram1D( "test", 10, 1, 10 );
    HistogramWriter w = new HistogramWriter();

    public final void testWriter() {
        String expectedReturn = "";
        String actualReturn = "";
        Writer k;
        try {
            k = new StringWriter();
            w.write( m, k );
            k.close();
            actualReturn = k.toString();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        try {
            expectedReturn = RegressionTesting.readTestResult( "/data/histogramwritertestoutput.txt" );
        } catch ( IOException e1 ) {
            e1.printStackTrace();
        }

        assertEquals( "return value", expectedReturn, actualReturn );
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        m.fill( 1.0 );
        m.fill( 1.0 );
        m.fill( 2.0 );
        m.fill( 1.0 );
        m.fill( 5.0 );
        m.fill( 11.0 );
        m.fill( 6.0 );
        m.fill( 6.0 );
        m.fill( 4.0 );
        m.fill( 4.0 );
        m.fill( 4.0 );
        m.fill( 4.0 );

    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}