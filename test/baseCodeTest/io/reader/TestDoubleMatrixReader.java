package baseCodeTest.io.reader;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import baseCode.dataStructure.matrix.DenseDoubleMatrix2DNamed;
import baseCode.io.reader.DoubleMatrixReader;

/**
 * 
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id: TestDoubleMatrixReader.java,v 1.2 2004/06/24 17:48:03 pavlidis
 *          Exp $
 */
public class TestDoubleMatrixReader extends TestCase {

   DenseDoubleMatrix2DNamed matrix = null;
   InputStream is = null;
   DoubleMatrixReader reader = null;
   InputStream ism = null;
   InputStream ismb = null; // missing, with bad rows.
   InputStream isbig = null; // missing, with bad rows.
   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      reader = new DoubleMatrixReader();
      is = TestStringMatrixReader.class
            .getResourceAsStream( "/data/testdata.txt" );

      ism = TestStringMatrixReader.class
            .getResourceAsStream( "/data/testdatamissing.txt" );

      ismb = TestStringMatrixReader.class
            .getResourceAsStream( "/data/testdatamissing-badrows.txt" );
      
      isbig = TestStringMatrixReader.class
      .getResourceAsStream( "/data/melanoma_and_sarcomaMAS5.txt" );
   }

   /*
    * @see TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
      is.close();
      ism.close();
      ismb.close();
      isbig.close();
      matrix = null;
   }

   public void testReadInputStreamMissing() {
      try {
         matrix = ( DenseDoubleMatrix2DNamed ) reader.read( ism );
         int actualReturn = matrix.rows();
         int expectedReturn = 30;
         assertEquals( "return value", expectedReturn, actualReturn );
      } catch ( IOException e ) {
         e.printStackTrace();
      }
   }

   public void testReadInputStreamMissingBad() {
      try {
         matrix = ( DenseDoubleMatrix2DNamed ) reader.read( ismb );
         fail( "Should have gotten an IO error" );
      } catch ( IOException e ) {
      }
   }

   /*
    * Class under test for NamedMatrix read(InputStream)
    */
   public void testReadInputStreamRowCount() {
      try {
         matrix = ( DenseDoubleMatrix2DNamed ) reader.read( is );
         int actualReturn = matrix.rows();
         int expectedReturn = 30;
         assertEquals( "return value", expectedReturn, actualReturn );
      } catch ( IOException e ) {
         e.printStackTrace();
      }
   }

   public void testReadInputStreamColumnCount() {
      try {
         matrix = ( DenseDoubleMatrix2DNamed ) reader.read( is );
         int actualReturn = matrix.columns();
         int expectedReturn = 12;
         assertEquals( "return value", expectedReturn, actualReturn );
      } catch ( IOException e ) {
         e.printStackTrace();
      }
   }

   public void testReadInputStreamGotRowName() {
      try {
         matrix = ( DenseDoubleMatrix2DNamed ) reader.read( is );
         boolean actualReturn = matrix.containsRowName( "gene1_at" )
               && matrix.containsRowName( "AFFXgene30_at" );
         boolean expectedReturn = true;
         assertEquals( "return value", expectedReturn, actualReturn );
      } catch ( IOException e ) {
         e.printStackTrace();
      }
   }

   public void testReadInputStreamGotColName() {
      try {
         matrix = ( DenseDoubleMatrix2DNamed ) reader.read( is );
         boolean actualReturn = matrix.containsColumnName( "sample1" )
               && matrix.containsColumnName( "sample12" );
         boolean expectedReturn = true;
         assertEquals( "return value (for sample1 and sample12)",
               expectedReturn, actualReturn );
      } catch ( IOException e ) {
         e.printStackTrace();
      }
   }
   
//   public void testReadInputStreamBig() {
//      try {
//         matrix = ( DenseDoubleMatrix2DNamed ) reader.read( isbig );
//         int actualReturn = matrix.rows();
//         int expectedReturn = 12533;
//         assertEquals( "return value ",
//               expectedReturn, actualReturn );
//      } catch ( IOException e ) {
//         e.printStackTrace();
//      } catch ( OutOfMemoryError e) {
//         e.printStackTrace();
//      }
//   }

}