package ubic.basecode.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ubic.basecode.util.RegressionTesting;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TestMathUtil {

    @Test
    public void testFillRange() {
        int[] actualReturn = MathUtil.fillRange( 0, 10 );
        int[] expectedReturn = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        assertTrue( RegressionTesting.sameArray( expectedReturn, actualReturn ) );
    }

    @Test
    public void testFillRangeDown() {
        int[] actualReturn = MathUtil.fillRange( 10, 0 );
        int[] expectedReturn = new int[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
        assertTrue( RegressionTesting.sameArray( expectedReturn, actualReturn ) );
    }

    @Test
    public void testFillRangeNo() {
        int[] actualReturn = MathUtil.fillRange( 10, 10 );
        int[] expectedReturn = new int[] { 10 };
        assertTrue( RegressionTesting.sameArray( expectedReturn, actualReturn ) );
    }

    @Test
    public void testMax() {
        assertEquals( 1000, MathUtil.max( new double[] { 1d, 2d, 3d, 1000d, 4d, 5d, 6d, 7d, 9d, 10d } ),
                Double.MIN_VALUE );
    }

    @Test
    public void testSumArray() {
        int actualReturn = MathUtil.sumArray( new int[] { 1, 2, 3, 4 } );
        int expectedReturn = 10;
        assertEquals( expectedReturn, actualReturn );
    }

}
