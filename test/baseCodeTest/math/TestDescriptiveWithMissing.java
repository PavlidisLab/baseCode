package baseCodeTest.Math;

import junit.framework.*;
import baseCode.Math.*;
import cern.colt.list.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author not attributable
 * @version $Id$
 */

public class TestDescriptiveWithMissing extends TestCase {
    private DescriptiveWithMissing descriptiveWithMissing = null;
    private DoubleArrayList data;
    private DoubleArrayList data1missing;
    private DoubleArrayList data2missing;

    protected void setUp() throws Exception {
        super.setUp();
        descriptiveWithMissing = new DescriptiveWithMissing();
        data1missing = new DoubleArrayList(new double[]{1.0, Double.NaN, 3.0, 4.0, 5.0, 6.0});
        data2missing = new DoubleArrayList(new double[]{1.0, 2.0, 3.0, Double.NaN, 3.5, 4.0});
    }

    protected void tearDown() throws Exception {
        descriptiveWithMissing = null;
        data = null;
        super.tearDown();
    }

    public void testMin() {
        double expectedReturn = 1.0;
        double actualReturn = descriptiveWithMissing.min(data1missing);
        assertEquals("Minimum should be 1.0", expectedReturn, actualReturn, Double.MIN_VALUE);
    }

    public void testMax() {
     double expectedReturn = 6.0;
     double actualReturn = descriptiveWithMissing.max(data1missing);
     assertEquals("return value", expectedReturn, actualReturn, Double.MIN_VALUE);
 }


}
