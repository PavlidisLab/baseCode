/*
 * The baseCode project
 * 
 * Copyright (c) 2012 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.basecode.util;

import junit.framework.TestCase;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TestStringUtil extends TestCase {

    private static Log log = LogFactory.getLog( TestStringUtil.class.getName() );

    // simple case
    public void testCsvSplitA() {
        String i = "foo,bar,aloo,balloo";
        String[] actualReturn = StringUtil.csvSplit( i );
        String[] expectedReturn = new String[] { "foo", "bar", "aloo", "balloo" };
        assertEquals( expectedReturn[0], actualReturn[0] );
        assertEquals( expectedReturn[1], actualReturn[1] );
        assertEquals( expectedReturn[2], actualReturn[2] );
        assertEquals( expectedReturn[3], actualReturn[3] );
    }

    // field has comma
    public void testCsvSplitB() {
        String i = "foo,bar,aloo,\"bal,loo\"";
        String[] actualReturn = StringUtil.csvSplit( i );
        String[] expectedReturn = new String[] { "foo", "bar", "aloo", "bal,loo" };
        assertEquals( expectedReturn[0], actualReturn[0] );
        assertEquals( expectedReturn[1], actualReturn[1] );
        assertEquals( expectedReturn[2], actualReturn[2] );
        assertEquals( expectedReturn[3], actualReturn[3] );
    }

    // two fields have commas
    public void testCsvSplitC() {
        String i = "\"f,oo\",bar,aloo,\"bal,loo\"";
        String[] actualReturn = StringUtil.csvSplit( i );
        String[] expectedReturn = new String[] { "f,oo", "bar", "aloo", "bal,loo" };
        assertEquals( expectedReturn[3], actualReturn[3] );
        assertEquals( expectedReturn[1], actualReturn[1] );
        assertEquals( expectedReturn[0], actualReturn[0] );
    }

    // two commas in one field.
    public void testCsvSplitD() {
        String i = "foo,\"b,a,r\",aloo,balloo";
        String[] actualReturn = StringUtil.csvSplit( i );
        String[] expectedReturn = new String[] { "foo", "b,a,r", "aloo", "balloo" };
        assertEquals( expectedReturn[3], actualReturn[3] );
        assertEquals( expectedReturn[1], actualReturn[1] );
    }

    // comma at start of field
    public void testCsvSplitE() {
        String i = "foo,\",bar\",aloo,balloo";
        String[] actualReturn = StringUtil.csvSplit( i );
        String[] expectedReturn = new String[] { "foo", ",bar", "aloo", "balloo" };
        assertEquals( expectedReturn[3], actualReturn[3] );
        assertEquals( expectedReturn[1], actualReturn[1] );
        assertEquals( expectedReturn[0], actualReturn[0] );
    }

    // empty quoted field
    public void testCsvSplitF() {
        String i = "foo,\"\",aloo,balloo";
        String[] actualReturn = StringUtil.csvSplit( i );
        String[] expectedReturn = new String[] { "foo", "", "aloo", "balloo" };
        assertEquals( expectedReturn[0], actualReturn[0] );
        assertEquals( expectedReturn[1], actualReturn[1] );
        assertEquals( expectedReturn[2], actualReturn[2] );
        assertEquals( expectedReturn[3], actualReturn[3] );
    }

    public void testSpeedTwoStringHashKey() {
        String a = "barblyfoo";
        String b = "fooblybar";
        StopWatch timer = new StopWatch();
        timer.start();
        int iters = 1000000;
        for ( int i = 0; i < iters; i++ ) {
            StringUtil.twoStringHashKey( a, b );
        }

        timer.stop();
        if ( timer.getTime() > 2000 ) {
            fail();
        }
        log.debug( "String concat " + timer.getTime() + " milliseconds" );
    }

    public void testTwoStringHashKey() {
        String i = "foo";
        String j = "bar";

        Long icode = ( long ) i.hashCode();
        Long jcode = ( long ) j.hashCode();

        log.debug( Long.toBinaryString( jcode.longValue() ) + " " + Long.toBinaryString( icode.longValue() << 32 ) );

        long expectedResult = jcode.longValue() | icode.longValue() << 32;

        Long result = StringUtil.twoStringHashKey( j, i );

        log.debug( Long.toBinaryString( expectedResult ) );

        assertEquals( Long.toBinaryString( expectedResult ), Long.toBinaryString( result.longValue() ) );

    }

    public void testTwoStringHashKeyB() {
        String i = "foo";
        String j = "bar";
        assertEquals( StringUtil.twoStringHashKey( i, j ), StringUtil.twoStringHashKey( j, i ) );
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
