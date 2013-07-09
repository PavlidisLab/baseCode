package ubic.basecode.math.distribution;

import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import ubic.basecode.util.RegressionTesting;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.jet.random.engine.MersenneTwister;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TestWishart {

    private static Log log = LogFactory.getLog( TestWishart.class.getName() );

    DoubleMatrix2D cov;
    Wishart t1;
    Wishart t2;
    Wishart t3;
    Wishart t4;

    @Before
    public void setUp() throws Exception {
        cov = new DenseDoubleMatrix2D( new double[][] { { 1, 2 }, { 1, 2 } } );

        t1 = new Wishart( 5, cov, new MersenneTwister() );
        // t2 = new Wishart(2, 8, cov);
        // t3 = new Wishart(2, 20, cov);
        // t4 = new Wishart(8, 5, cov);
    }

    @Test
    public void testNextDoubleMatrix() {
        DoubleMatrix2D actualReturn = t1.nextDoubleMatrix();
        log.debug( actualReturn );
        DoubleMatrix2D expectedReturn = new DenseDoubleMatrix2D( new double[][] { { 1.426553, 4.848117 },
                { 4.848117, 16.9009 } } );
        assertTrue( RegressionTesting.closeEnough( expectedReturn, actualReturn, 0.0001 ) );
    }

}