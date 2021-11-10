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
package ubic.basecode.math;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.util.RegressionTesting;
import cern.colt.list.DoubleArrayList;

/**
 * @author pavlidis
 * 
 */
public class TestStats {

    private DoubleArrayList data1Nomissing;

    private DoubleArrayList longtest;

    @Before
    public void setUp() throws Exception {

        /* versions of the above, but without the NaNs */
        data1Nomissing = new DoubleArrayList( new double[] { 1.0, 3.0, 4.0, 5.0, 6.0 } );

        longtest = new DoubleArrayList( new double[] { 0.944582576545844, 0.779251841525345, 0.243662620408789,
                0.221922034715125, 0.707629937237768, 0.844940612116396, 0.37307859512114, 0.216209684719754,
                0.606640632705043, 0.0289807962282107, 0.831726757257319, 0.599810024125913, 0.945573940587206,
                0.692997506949111, 0.139842747221286, 0.606947460245238, 0.0423340684872135, 0.974213612924272,
                0.963219529277481, 0.990324034140914, 0.18366629791934, 0.998038865833498, 0.95102835078158,
                0.260760025894898, 0.135541313794304, 0.362569773855302, 0.00907603291856485, 0.347046293225362,
                0.552972766279235, 0.956864628364488, 0.578842167640368, 0.0202553960552123, 0.139571658240153,
                0.838049580661271, 0.696258674341387, 0.167767706757327, 0.857975063707331, 0.384427669696752,
                0.675471091800467, 0.012919572385802, 0.0944474009613609, 0.420980230040455, 0.658166227311119,
                0.0618454224981813, 0.595221354638128, 0.880250901050482, 0.155426216787906, 0.652202074026153,
                0.487896010852323, 0.714714352227883, } );

    } /*
       * @see TestCase#tearDown()
       */

    @Test
    public final void testCdf() {
        DoubleArrayList expectedReturn = new DoubleArrayList( new double[] { 1, 0.947368421052632, 0.789473684210526,
                0.578947368421053, 0.315789473684211 } );
        DoubleArrayList actualReturn = Stats.cdf( data1Nomissing );
        assertEquals( true, RegressionTesting.closeEnough( actualReturn, expectedReturn, 0.0001 ) );
    }

    @Test
    public final void testCumulate() {
        DoubleArrayList expectedReturn = new DoubleArrayList( new double[] { 1, 4, 8, 13, 19 } );
        DoubleArrayList actualReturn = Stats.cumulate( data1Nomissing );
        assertEquals( true, RegressionTesting.closeEnough( actualReturn, expectedReturn, 0.0001 ) );
    }

    @Test
    public final void testCumulateRight() {
        DoubleArrayList expectedReturn = new DoubleArrayList( new double[] { 19, 18, 15, 11, 6 } );
        DoubleArrayList actualReturn = Stats.cumulateRight( data1Nomissing );
        assertEquals( true, RegressionTesting.closeEnough( actualReturn, expectedReturn, 0.0001 ) );
    }

    @Test
    public final void testCv() {
        double expectedReturn = 1 / 1.975525931;
        double actualReturn = Stats.cv( data1Nomissing );
        assertEquals( expectedReturn, actualReturn, 0.000001 );
    }

    @Test
    public final void testIsValidFraction() {
        boolean actualReturn = Stats.isValidFraction( 12 );
        assertEquals( false, actualReturn );
    }

    @Test
    public final void testIsValidFraction2() {
        boolean actualReturn = Stats.isValidFraction( 0.5 );
        assertEquals( true, actualReturn );
    }

    @Test
    public final void testMeanAboveQuantile() {
        double expectedReturn = 0.806953261;
        double actualReturn = Stats.meanAboveQuantile( 25, longtest.elements(), 50 );
        assertEquals( expectedReturn, actualReturn, 1e-5 );
    }

    /*
     * Class under test for DoubleArrayList normalize(DoubleArrayList)
     */
    @Test
    public final void testNormalizeDoubleArrayList() {
        DoubleArrayList expectedReturn = new DoubleArrayList( new double[] { 0.0526315789473684, 0.157894736842105,
                0.210526315789474, 0.263157894736842, 0.315789473684211 } );

        DoubleArrayList actualReturn = Stats.normalize( data1Nomissing );
        assertEquals( true, RegressionTesting.closeEnough( actualReturn, expectedReturn, 0.0001 ) );
    }

    /*
     * Class under test for DoubleArrayList normalize(DoubleArrayList, double)
     */
    @Test
    public final void testNormalizeDoubleArrayListdouble() {
        DoubleArrayList expectedReturn = new DoubleArrayList( new double[] { 0.263157894736842, 0.789473684210526,
                1.05263157894737, 1.31578947368421, 1.57894736842105, } );

        DoubleArrayList actualReturn = Stats.normalize( data1Nomissing, 3.8 );
        assertEquals( true, RegressionTesting.closeEnough( actualReturn, expectedReturn, 0.0001 ) );
    }

    @Test
    public final void testNumberOfDistinctValues() {
        int actualReturn = Stats.numberofDistinctValues( new DoubleArrayList( new double[] { 1.0, 1.0, 3.0, 4.0, 5.0,
                6.0 } ), 0.01 );
        assertEquals( 5, actualReturn );

        actualReturn = Stats.numberofDistinctValues( data1Nomissing, 0.01 );
        assertEquals( 5, actualReturn );
        actualReturn = Stats.numberofDistinctValues( new DoubleArrayList( new double[] { 1.0, 1.0, 3.0, 4.0, 4.00001,
                5.0, 6.0 } ), 0.0001 );
        assertEquals( 5, actualReturn );
        actualReturn = Stats.numberofDistinctValues( new DoubleArrayList( new double[] { 1.0, 1.0, 3.0, 4.0, 4.00001,
                5.0, 6.0 } ), 0.00001 );
        assertEquals( 6, actualReturn );
    }


    @Test
    public final void testNumberOfDistinctValuesNonNA() {
        int actualReturn = Stats.numberofDistinctValuesNonNA( new DoubleArrayList( new double[] { 1.0, 1.0, 3.0, 4.0, 5.0,
                6.0, Double.NaN } ), 0.01 );
        assertEquals( 5, actualReturn );

        actualReturn = Stats.numberofDistinctValuesNonNA( data1Nomissing, 0.01 );
        assertEquals( 5, actualReturn );
        actualReturn = Stats.numberofDistinctValuesNonNA( new DoubleArrayList( new double[] { 1.0, 1.0, 3.0, 4.0, 4.00001,
                5.0, 6.0 } ), 0.0001 );
        assertEquals( 5, actualReturn );
        actualReturn = Stats.numberofDistinctValuesNonNA( new DoubleArrayList( new double[] { 1.0, 1.0, 3.0, 4.0, 4.00001,
                5.0, Double.NaN, 6.0 } ), 0.00001 );
        assertEquals( 6, actualReturn );
    }


    @Test
    public final void testQuantile() {
        double expectedReturn = 0.595221355;
        double actualReturn = Stats.quantile( 25, longtest.elements(), 50 );
        assertEquals( expectedReturn, actualReturn, 1e-5 );
    }

    @Test
    public final void testQuantile75() {
        double expectedReturn = 0.831726757;
        double actualReturn = Stats.quantile( 25, longtest.elements(), 37 );
        assertEquals( expectedReturn, actualReturn, 1e-5 );
    }

    @Test
    public final void testRange() {
        double expectedReturn = 5;
        double actualReturn = Stats.range( data1Nomissing );
        assertEquals( expectedReturn, actualReturn, 1e-5 );
    }

  

}