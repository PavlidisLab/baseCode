package baseCodeTest.io.reader;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import baseCode.dataStructure.matrix.SparseDoubleMatrix2DNamed;
import baseCode.io.reader.SparseDoubleMatrixReader;
import baseCode.util.RegressionTesting;

/**
 * 
 *
 * <hr>
 * <p>Copyright (c) 2004 Columbia University
 * @author pavlidis
 * @version $Id$
 */
public class TestSparseDoubleMatrixReader extends TestCase {
   SparseDoubleMatrix2DNamed matrix = null;
   InputStream is = null;
   InputStream isa = null;
   SparseDoubleMatrixReader reader = null;
   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      reader = new SparseDoubleMatrixReader();
      is = TestSparseDoubleMatrixReader.class
            .getResourceAsStream( "/data/JW-testmatrix.txt" );
      isa = TestSparseDoubleMatrixReader.class
      .getResourceAsStream( "/data/adjacencylist-testmatrix.txt" );
   }

   /*
    * Class under test for NamedMatrix read(String)
    */
   public void testReadStream() {
      try {
         matrix = ( SparseDoubleMatrix2DNamed ) reader.read ( isa, null );
         String actualReturn = matrix.toString();
         String expectedReturn = RegressionTesting.readTestResult(TestSparseDoubleMatrixReader.class
               .getResourceAsStream("/data/JW-testoutputSym.txt"));
     //    System.err.println("testReadStream\nHere it is:\n" + actualReturn + "\nExpected:\n" + expectedReturn);
         assertEquals( "return value", expectedReturn, actualReturn );
         
      } catch ( IOException e ) {
         e.printStackTrace();
      } 
  
   }

   public void testReadJW() {
      try {
         matrix = ( SparseDoubleMatrix2DNamed ) reader.readJW( is );
         String actualReturn = matrix.toString();
         String expectedReturn = RegressionTesting.readTestResult(TestSparseDoubleMatrixReader.class
               .getResourceAsStream("/data/JW-testoutput.txt"));
     //    System.err.println("testReadJW\nHere it is:\n" + actualReturn + "\nExpected:\n" + expectedReturn);
         assertEquals( "return value", expectedReturn, actualReturn );
       // 
      } catch ( IOException e ) {
         e.printStackTrace();
      } catch ( IllegalAccessException e ) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch ( NoSuchFieldException e ) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

}
