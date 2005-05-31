package baseCode.math.distribution;

import junit.framework.TestCase;
import baseCode.util.RegressionTesting;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.jet.random.engine.MersenneTwister;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestWishart extends TestCase {

   Wishart t1;
   Wishart t2;
   Wishart t3;
   Wishart t4;
   DoubleMatrix2D cov;

   protected void setUp() throws Exception {
      cov = new DenseDoubleMatrix2D( new double[][] {
            {
                  1, 2
            }, {
                  1, 2
            }
      } );

      t1 = new Wishart( 5, cov, new MersenneTwister() );
      //t2 = new Wishart(2, 8, cov);
      // t3 = new Wishart(2, 20, cov);
      // t4 = new Wishart(8, 5, cov);
      super.setUp();
   }

   public void testNextDoubleMatrix() {
      DoubleMatrix2D actualReturn = t1.nextDoubleMatrix();
      System.err.println(actualReturn);
      DoubleMatrix2D expectedReturn = new DenseDoubleMatrix2D( new double[][] {
            {
                  1.426553, 4.848117
            }, {
                  4.848117, 16.9009
            }
      } );
      assertTrue( RegressionTesting.closeEnough( expectedReturn, actualReturn, 0.0001 ) );
   }

}