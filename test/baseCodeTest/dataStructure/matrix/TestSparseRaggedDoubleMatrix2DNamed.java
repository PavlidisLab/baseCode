package baseCodeTest.dataStructure.matrix;

import java.io.InputStream;

import junit.framework.TestCase;
import baseCode.dataStructure.matrix.SparseRaggedDoubleMatrix2DNamed;
import baseCode.io.reader.SparseRaggedDouble2DNamedMatrixReader;
import baseCodeTest.io.reader.TestSparseDoubleMatrixReader;
import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestSparseRaggedDoubleMatrix2DNamed extends TestCase {
   SparseRaggedDoubleMatrix2DNamed matrix = null;
   InputStream is = null;
   InputStream isa = null;
   SparseRaggedDouble2DNamedMatrixReader reader = null;

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      reader = new SparseRaggedDouble2DNamedMatrixReader();
      is = TestSparseDoubleMatrixReader.class
            .getResourceAsStream( "/data/JW-testmatrix.txt" );
      isa = TestSparseDoubleMatrixReader.class
            .getResourceAsStream( "/data/adjacencylist-testmatrix.txt" );
      matrix = ( SparseRaggedDoubleMatrix2DNamed ) reader.read( is );
   }

   public void testRows() {

      int actualReturn = matrix.rows();
      int expectedReturn = 3;

      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testColumns() {
      int actualReturn = matrix.columns();
      int expectedReturn = 3;
      assertEquals( "return value", expectedReturn, actualReturn );
   }


   public void testGetRowArrayList() {

      DoubleArrayList actualReturn = matrix.getRowArrayList( 2 );

      DoubleArrayList expectedReturn = new DoubleArrayList( new double[] {
            0.3, 0.8
      } );

      assertEquals( "return value", expectedReturn, actualReturn );
   }

   //
   public void testGetRowMatrix1D() {

      DoubleMatrix1D actualReturn = matrix.getRowMatrix1D( 2 );
      DoubleMatrix1D expectedReturn = new SparseDoubleMatrix1D( new double[] {
            0.3, 0.0, 0.8
      } );
      assertEquals( "return value", expectedReturn, actualReturn );
   }
   
   /* getRow returns a double[] */
   public void testGetRow() {
      DoubleArrayList actualReturn = new DoubleArrayList(matrix.getRow(2));
      DoubleArrayList  expectedReturn = new DoubleArrayList(new double[] {
            0.3, 0.0, 0.8
      });
      assertEquals( "return value", expectedReturn, actualReturn );
   }
   

}