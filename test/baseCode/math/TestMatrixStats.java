package baseCode.math;

import java.io.IOException;

import junit.framework.TestCase;
import baseCode.dataFilter.AbstractTestFilter;
import baseCode.dataStructure.matrix.AbstractNamedDoubleMatrix;
import baseCode.dataStructure.matrix.DenseDoubleMatrix2DNamed;
import baseCode.io.reader.DoubleMatrixReader;
import baseCode.util.RegressionTesting;

/**
 * 
 *
 * <hr>
 * <p>Copyright (c) 2004 Columbia University
 * @author pavlidis
 * @version $Id$
 */
public class TestMatrixStats extends TestCase {
   
   protected AbstractNamedDoubleMatrix testdata = null;
   protected AbstractNamedDoubleMatrix testdatahuge = null;
   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      DoubleMatrixReader f = new DoubleMatrixReader();

      testdata = ( DenseDoubleMatrix2DNamed ) f.read( AbstractTestFilter.class
            .getResourceAsStream( "/data/testdata.txt" ) );
      
   //  testdatahuge = ( DenseDoubleMatrix2DNamed ) f.read( AbstractTestFilter.class
    //       .getResourceAsStream( "/data/melanoma_and_sarcomaMAS5.txt" ) );
   }

   /*
    * @see TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
      testdata = null;
      testdatahuge = null;
   }
   
   public final void testMin() {
      double expectedReturn = -965.3;
      double actualReturn = MatrixStats.min( testdata );
      assertEquals( "return value", expectedReturn, actualReturn, 0.01 );
   }

   public final void testMax() {
      double expectedReturn = 44625.7;
      double actualReturn = MatrixStats.max( testdata );
      assertEquals( "return value", expectedReturn, actualReturn, 0.01 );
   }
   
   public final void testCorrelationMatrix() {
      DenseDoubleMatrix2DNamed actualReturn = MatrixStats.correlationMatrix(testdata);
      DoubleMatrixReader f = new DoubleMatrixReader();
      DenseDoubleMatrix2DNamed expectedReturn = null;
      try {
         expectedReturn = (DenseDoubleMatrix2DNamed)f.read( AbstractTestFilter.class
         .getResourceAsStream( "/data/correlation-matrix-testoutput.txt" ) );
      } catch ( IOException e ) {
         e.printStackTrace();
      } 
      assertEquals( true, RegressionTesting.closeEnough(expectedReturn, actualReturn, 0.001 ));
   }
   
   public final void testRbfNormalize() {
      MatrixStats.rbfNormalize(testdata, 100);
      
  //   System.err.println(testdata);
      
  //    assertEquals( true, RegressionTesting.closeEnough(expectedReturn, testdata, 0.001 ));
   }
   
   
 /**  public final void testCorrelationMatrixHuge() {
      SparseDoubleMatrix2DNamed actualReturn = MatrixStats.correlationMatrix(testdatahuge, 0.9);
      DoubleMatrixReader f = new DoubleMatrixReader();
      DenseDoubleMatrix2DNamed expectedReturn = null;
      try {
         expectedReturn = (DenseDoubleMatrix2DNamed)f.read( AbstractTestFilter.class
         .getResourceAsStream( "/data/correlation-matrix-testoutput.txt" ) );
      } catch ( IOException e ) {
         e.printStackTrace();
      } catch ( OutOfMemoryError e ) {
         e.printStackTrace();
      }
     // assertEquals( true, RegressionTesting.closeEnough(expectedReturn, actualReturn, 0.001 ));
   }
   
*/
}
