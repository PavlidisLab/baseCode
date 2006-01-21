package baseCode.util;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2006 University of British Columbia
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestStringUtil extends TestCase {

    private static Log log = LogFactory.getLog( TestStringUtil.class.getName() );

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // simple case
    public void testCsvSplitA() {
        String i = "foo,bar,aloo,balloo";
        String[] actualReturn = StringUtil.csvSplit( 4, i );
        String[] expectedReturn = new String[] { "foo", "bar", "aloo", "balloo" };
        assertEquals( expectedReturn[3], actualReturn[3] );
    }

    // field has comma
    public void testCsvSplitB() {
        String i = "foo,bar,aloo,\"bal,loo\"";
        String[] actualReturn = StringUtil.csvSplit( 4, i );
        String[] expectedReturn = new String[] { "foo", "bar", "aloo", "bal,loo" };
        assertEquals( expectedReturn[3], actualReturn[3] );
    }

    // two fields have commas
    public void testCsvSplitC() {
        String i = "\"f,oo\",bar,aloo,\"bal,loo\"";
        String[] actualReturn = StringUtil.csvSplit( 4, i );
        String[] expectedReturn = new String[] { "f,oo", "bar", "aloo", "bal,loo" };
        assertEquals( expectedReturn[3], actualReturn[3] );
        assertEquals( expectedReturn[1], actualReturn[1] );
        assertEquals( expectedReturn[0], actualReturn[0] );
    }

    // two commas in one field.
    public void testCsvSplitD() {
        String i = "foo,\"b,a,r\",aloo,balloo";
        String[] actualReturn = StringUtil.csvSplit( 4, i );
        String[] expectedReturn = new String[] { "foo", "b,a,r", "aloo", "balloo" };
        assertEquals( expectedReturn[3], actualReturn[3] );
        assertEquals( expectedReturn[1], actualReturn[1] );
    }

    // comma at start of field
    public void testCsvSplitE() {
        String i = "foo,\",bar\",aloo,balloo";
        String[] actualReturn = StringUtil.csvSplit( 4, i );
        String[] expectedReturn = new String[] { "foo", ",bar", "aloo", "balloo" };
        assertEquals( expectedReturn[3], actualReturn[3] );
        assertEquals( expectedReturn[1], actualReturn[1] );
        assertEquals( expectedReturn[0], actualReturn[0] );
    }

    // empty quoted field
    public void testCsvSplitF() {
        String i = "foo,\"\",aloo,balloo";
        String[] actualReturn = StringUtil.csvSplit( 4, i );
        String[] expectedReturn = new String[] { "foo", "\"\"", "aloo", "balloo" };
        assertEquals( expectedReturn[3], actualReturn[3] );
    }

    public void testTwoStringHashKey() throws Exception {
        String i = "foo";
        String j = "bar";

        Long icode = new Long( i.hashCode() );
        Long jcode = new Long( j.hashCode() );

        log.debug( Long.toBinaryString( jcode.longValue() ) + " " + Long.toBinaryString( icode.longValue() << 32 ) );

        long expectedResult = jcode.longValue() | ( icode.longValue() << 32 );

        Long result = ( Long ) StringUtil.twoStringHashKey( j, i );

        log.debug( Long.toBinaryString( expectedResult ) );

        assertEquals( Long.toBinaryString( expectedResult ), Long.toBinaryString( result.longValue() ) );

    }

    public void testTwoStringHashKeyB() {
        String i = "foo";
        String j = "bar";
        assertEquals( StringUtil.twoStringHashKey( i, j ), StringUtil.twoStringHashKey( j, i ) );
    }

    public void testSpeedTwoStringHashKey() {
        String a = "barblyfoo";
        String b = "fooblybar";
        StopWatch timer = new StopWatch();
        timer.start();
        int iters = 100000;
        for ( int i = 0; i < iters; i++ ) {
            StringUtil.twoStringHashKey( a, b );
        }
        timer.stop();
        log.debug( "Bitwise hash took " + timer.getTime() + " milliseconds" );
        timer.reset();
        timer.start();
        for ( int i = 0; i < iters; i++ ) {
            if ( a.hashCode() < b.hashCode() ) {
                String r = b + "___" + a;
            } else {
                String r = a + "___" + b;
            }
        }
        timer.stop();
        log.debug( "String concat " + timer.getTime() + " milliseconds" );
    }
}
