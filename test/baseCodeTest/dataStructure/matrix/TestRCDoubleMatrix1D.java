package baseCodeTest.dataStructure.matrix;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import baseCode.dataStructure.matrix.RCDoubleMatrix1D;
import junit.framework.TestCase;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestRCDoubleMatrix1D extends TestCase {

   RCDoubleMatrix1D a;
   RCDoubleMatrix1D b;
   DoubleMatrix1D c;

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      
      /*
       * a: 0 1 2 0 5
       * b: 5 3 2 1 (0)
       * 
       */
      
      DoubleArrayList va = new DoubleArrayList( new double[] {
            1, 2, 5
      } );
      IntArrayList ina = new IntArrayList( new int[] {
            1, 2, 4
      } );

      DoubleArrayList vb = new DoubleArrayList( new double[] {
            5, 3, 2, 1
      } );
      IntArrayList inb = new IntArrayList( new int[] {
            0, 1, 2, 3
      } );
      
      DoubleArrayList vc = new DoubleArrayList( new double[] {
            5, 3, 2, 1
      } );
      IntArrayList inc = new IntArrayList( new int[] {
            0, 1, 2, 3
      } );


      a = new RCDoubleMatrix1D( va, ina );
      b = new RCDoubleMatrix1D( vb, inb );
      c = new DenseDoubleMatrix1D( new double[] {5,3,2,1});
      super.setUp();
   }

   public void testForEachNonZero() {
      //TODO Implement forEachNonZero().
      DoubleMatrix1D actualReturn = a
            .forEachNonZero( new cern.colt.function.IntDoubleFunction() {
               public double apply( int column, double value ) {
           //       a.setQuick( column, a.getQuick( column ) / 2.0 );
                  return value / 2.0;
               }
            } );
      DoubleMatrix1D expectedReturn = new RCDoubleMatrix1D( new double[] {
            0, 0.5, 1, 0, 2.5
      } );
      assertEquals( "return value", new DoubleArrayList( expectedReturn
            .toArray() ), new DoubleArrayList( actualReturn.toArray() ) );
   }

   /*
    * Class under test for double zDotProduct(DoubleMatrix1D)
    */
   public void testZDotProductDoubleMatrix1D() {
      double actualReturn = a.zDotProduct( b );
      double expectedReturn = 7;
      assertEquals( "return value", expectedReturn, actualReturn, 0.0001 );
   }
   
   
   /*
    * Class under test for double zDotProduct(DoubleMatrix1D)
    */
   public void testZDotProductDoubleMatrix1DReverse() {
      double actualReturn = b.zDotProduct( a );
      double expectedReturn = 7;
      assertEquals( "return value", expectedReturn, actualReturn, 0.0001 );
   }
   
   /*
    * Class under test for double zDotProduct(DoubleMatrix1D)
    */
   public void testZDotProductDoubleMatrix1DHarder() {
      double actualReturn = a.zDotProduct( c  );
      double expectedReturn =  7;
      assertEquals( "return value", expectedReturn, actualReturn, 0.0001 );
   }
   
   /*
    * Class under test for double zDotProduct(DoubleMatrix1D)
    */
   public void testZDotProductDoubleMatrix1DHarderReverse() {
      double actualReturn = c.zDotProduct( a  );
      double expectedReturn =  7;
      assertEquals( "return value", expectedReturn, actualReturn, 0.0001 );
   }
   
   

   /*
    * Class under test for DoubleMatrix1D assign(DoubleFunction)
    */
   public void testAssignDoubleFunction() {
      DoubleMatrix1D actualReturn = a
            .assign( new cern.colt.function.DoubleFunction() {
               public double apply( double value ) {
                  return 2;
               }
            } );
      DoubleMatrix1D expectedReturn = new RCDoubleMatrix1D( new double[] {
            0, 2, 2, 0, 2
      } );
      assertEquals( "return value", new DoubleArrayList( expectedReturn
            .toArray() ), new DoubleArrayList( actualReturn.toArray() ) );
   }

}