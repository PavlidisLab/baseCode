package baseCodeTest.Math;

import baseCode.Math.DescriptiveWithMissing;
import cern.colt.list.DoubleArrayList;
import junit.framework.TestCase;
import cern.jet.stat.Descriptive;

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
    private DoubleArrayList data1Nomissing;
    private DoubleArrayList data2missing;
    private DoubleArrayList data2Nomissing;
    private DoubleArrayList data3shortmissing;
    private DoubleArrayList data3shortNomissing;

    protected void setUp() throws Exception {
        super.setUp();
        descriptiveWithMissing = new DescriptiveWithMissing();
        data1missing = new DoubleArrayList(new double[] {1.0, Double.NaN, 3.0,
                                           4.0, 5.0, 6.0});
        data2missing = new DoubleArrayList(new double[] {Double.NaN, Double.NaN,
                                           3.0,
                                           Double.NaN, 3.5, 4.0});
        data3shortmissing = new DoubleArrayList(new double[] {Double.NaN,
                                                Double.NaN, 3.0});

        data1Nomissing = new DoubleArrayList(new double[] {1.0, 3.0,
                                             4.0, 5.0, 6.0});
        data2Nomissing = new DoubleArrayList(new double[] {3.0, 3.5, 4.0});
        data3shortNomissing = new DoubleArrayList(new double[] {3.0});

    }

    protected void tearDown() throws Exception {
        descriptiveWithMissing = null;
        data = null;
        super.tearDown();
    }

    public void testDurbinWatson() {
        double expectedReturn = Descriptive.durbinWatson(data1Nomissing);
        double actualReturn = descriptiveWithMissing.durbinWatson(data1missing);
        assertEquals("return value", expectedReturn, actualReturn,
                     Double.MIN_VALUE);
    }

    public void testDurbinWatsonTwo() {
        double expectedReturn = Descriptive.durbinWatson(data2Nomissing);
        double actualReturn = descriptiveWithMissing.durbinWatson(data2missing);
        assertEquals("return value", expectedReturn, actualReturn,
                     Double.MIN_VALUE);
    }

    public void testDurbinWatsonShort() {

        try {
            double expectedReturn = Descriptive.durbinWatson(
                    data3shortNomissing);
            double actualReturn = descriptiveWithMissing.durbinWatson(
                    data3shortmissing);
            assertEquals("Short array failure.", expectedReturn, actualReturn,
                         Double.MIN_VALUE);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            return;
        } catch (Exception e) {
            fail("Threw wrong exception: " + e);
        }
    }

    public void testMean() {
        double expectedReturn = Descriptive.mean(data1Nomissing);
        double actualReturn = descriptiveWithMissing.mean(data1missing);
        assertEquals("return value", expectedReturn, actualReturn,
                     Double.MIN_VALUE);

    }

    public void testMin() {
        double expectedReturn = Descriptive.min(data1Nomissing);
        double actualReturn = descriptiveWithMissing.min(data1missing);
        assertEquals("return value", expectedReturn, actualReturn,
                     Double.MIN_VALUE);
    }

    public void testMax() {
        double expectedReturn = Descriptive.max(data1Nomissing);
        double actualReturn = descriptiveWithMissing.max(data1missing);
        assertEquals("return value", expectedReturn, actualReturn,
                     Double.MIN_VALUE);
    }

    public void testSum() {
        double expectedReturn = Descriptive.sum(data1Nomissing);
        double actualReturn = descriptiveWithMissing.sum(data1missing);
        assertEquals("return value", expectedReturn, actualReturn,
                     Double.MIN_VALUE);
    }

    public void testSumOfSquares() {
        double expectedReturn = Descriptive.sumOfSquares(data1Nomissing);
        double actualReturn = descriptiveWithMissing.sumOfSquares(data1missing);
        assertEquals("return value", expectedReturn, actualReturn,
                     Double.MIN_VALUE);
    }

    public void testSampleVariance() {
        double expectedReturn = Descriptive.sampleVariance(data1Nomissing,
                Descriptive.mean(data1Nomissing));
        double actualReturn = descriptiveWithMissing.sampleVariance(
                data1missing, DescriptiveWithMissing.mean(data1missing));
        assertEquals("return value", expectedReturn,
                     actualReturn,
                     Double.MIN_VALUE);

    }

    public void testVariance() {
        double expectedReturn = Descriptive.variance(data1Nomissing.size(),
                Descriptive.sum(data1Nomissing),
                Descriptive.sumOfSquares(data1Nomissing));
        double actualReturn = descriptiveWithMissing.variance(
                descriptiveWithMissing.sizeWithoutMissingValues(data1missing),
                descriptiveWithMissing.sum(data1missing),
                descriptiveWithMissing.sumOfSquares(data1missing));
        assertEquals("return value", expectedReturn,
                     actualReturn,
                     Double.MIN_VALUE);
    }


}
