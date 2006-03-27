package ubic.basecode.math;

import junit.framework.TestCase;
import ubic.basecode.util.RegressionTesting;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestMathUtil extends TestCase {

    public void testFillRange() {
        int[] actualReturn = MathUtil.fillRange( 0, 10 );
        int[] expectedReturn = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        assertTrue( RegressionTesting.sameArray( expectedReturn, actualReturn ) );
    }

    public void testFillRangeDown() {
        int[] actualReturn = MathUtil.fillRange( 10, 0 );
        int[] expectedReturn = new int[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
        assertTrue( RegressionTesting.sameArray( expectedReturn, actualReturn ) );
    }

    public void testFillRangeNo() {
        int[] actualReturn = MathUtil.fillRange( 10, 10 );
        int[] expectedReturn = new int[] { 10 };
        assertTrue( RegressionTesting.sameArray( expectedReturn, actualReturn ) );
    }

    public void testSumArray() {
        int actualReturn = MathUtil.sumArray( new int[] { 1, 2, 3, 4 } );
        int expectedReturn = 10;
        assertEquals( expectedReturn, actualReturn );
    }

}
