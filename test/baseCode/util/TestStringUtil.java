package baseCode.util;

import junit.framework.TestCase;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestStringUtil extends TestCase {

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
    

}
