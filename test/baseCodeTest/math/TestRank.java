package baseCodeTest.math;

import baseCode.math.Rank;
import cern.colt.list.DoubleArrayList;
import junit.framework.TestCase;

/**
 * Copyright (c) 2004 Columbia University
 * @author Owner
 * @version $Id$
 */
public class TestRank extends TestCase {

    DoubleArrayList testdata = null;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        testdata = new DoubleArrayList(new double[]{10.0, 11.0, 12.0, 13.0, 114.0, 5.0});
        
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        testdata = null;
    }

    /*
     * Class under test for DoubleArrayList rankTransform(DoubleArrayList)
     */
    public void testRankTransformDoubleArrayList() {
        DoubleArrayList actualReturn = Rank.rankTransform(testdata);
        DoubleArrayList expectedReturn = new DoubleArrayList(new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 0.0});
        assertEquals( "return value", expectedReturn, actualReturn);
    }

    /*
     * Class under test for Map rankTransform(Map)
     */
    public void testRankTransformMap() {
    }

}
