package baseCode.math.metaanalysis;

import junit.framework.TestCase;
import cern.colt.list.DoubleArrayList;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestMeanDifferenceMetaAnalysis extends TestCase {

   MeanDifferenceMetaAnalysis uf;
   MeanDifferenceMetaAnalysis ur;

   DoubleArrayList ds2nc;
   DoubleArrayList ds2cv;
   DoubleArrayList ds2d;

//   DenseDoubleMatrix2DNamed catheter;
//   DoubleArrayList catheterNumT;
//   DoubleArrayList catheterNumC;

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();

      // data set 2 from Appendix A of Cooper and Hedges. They give us the conditoinal variances (standard errors)
      // instead of the raw ns.

      // these are actually the standard errors (sqrt var)
      ds2cv = new DoubleArrayList( new double[] {
            0.125, 0.147, 0.167, 0.373, 0.369, 0.103, 0.103, 0.220, 0.164,
            0.251, 0.302, 0.223, 0.289, 0.290, 0.159, 0.167, 0.139, 0.094,
            0.174
      } );
      // convert into variances.
      for ( int i = 0; i < ds2cv.size(); i++ ) {
         ds2cv.setQuick( i, Math.pow( ds2cv.getQuick( i ), 2 ) );
      }

      ds2d = new DoubleArrayList( new double[] {
            0.03, 0.12, -0.14, 1.18, 0.26, -0.06, -0.02, -0.32, 0.27, 0.80,
            0.54, 0.18, -0.02, 0.23, -0.18, -0.06, 0.3, 0.07, -0.07
      } );

      uf = new MeanDifferenceMetaAnalysis( true );
      ur = new MeanDifferenceMetaAnalysis( false );

  //    DoubleMatrixReader f = new DoubleMatrixReader();

      // Catheter data set swiped from rmeta.
      //      Name : Name of principal author
      //      n.trt : number of coated catheters
      //      n.ctrl : number of standard catheters
      //      col.trt : number of coated catheters colonised
      //                   by bacteria
      //      col.ctrl : number of standard catheters colonised
      //                   by bacteria
      //      inf.trt : number of coated catheters resulting in
      //                   bloodstream infection
      //      inf.ctrl : number of standard catheters resulting in
      //                   bloodstream infection
      //     
      //      Veenstra D et al (1998) "Efficacy of Antiseptic Impregnated
      //      Central Venous Catheters in Preventing Nosocomial Infections: A
      //      Meta-analysis" JAMA 281:261-267

//      catheter = ( DenseDoubleMatrix2DNamed ) f.read( AbstractTestFilter.class
//            .getResourceAsStream( "/data/catheter.txt" ) );
//
//      catheterNumT = new DoubleArrayList(catheter.getColByName("n.trt")); 
//      catheterNumC = new DoubleArrayList(catheter.getColByName("n.ctrl")); 
       

   }

   /*
    * @see TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
   }

   public void testRunFixedVar() {
      uf.run( ds2d, ds2cv );
      double actualReturn = uf.getV();
      double expectedReturn = 0.00133;
      assertEquals( "return value", expectedReturn, actualReturn, 0.0001 );
   }

   public void testRunFixedE() {
      uf.run( ds2d, ds2cv );
      double actualReturn = uf.getE();
      double expectedReturn = 0.06;
      assertEquals( "return value", expectedReturn, actualReturn, 0.001 );
   }

   public void testRunFixedQ() {
      uf.run( ds2d, ds2cv );
      double actualReturn = uf.getQ();
      double expectedReturn = 35.83;
      assertEquals( "return value", expectedReturn, actualReturn, 0.01 );
   }

   public void testRunFixedZ() {
      uf.run( ds2d, ds2cv );
      double actualReturn = uf.getZ();
      double expectedReturn = 1.65;
      assertEquals( "return value", expectedReturn, actualReturn, 0.01 );
   }

   // note we are using the weighted variance model of 18-3.
   public void testRunRandomVar() {
      ur.run( ds2d, ds2cv );
      double actualReturn = ur.getV();
      double expectedReturn = 0.0031136;
      assertEquals( "return value", expectedReturn, actualReturn, 0.0001 );
   }

   public void testRunRandomE() {
      ur.run( ds2d, ds2cv );
      double actualReturn = ur.getE();
      double expectedReturn = 0.0893;
      assertEquals( "return value", expectedReturn, actualReturn, 0.001 );
   }

   public void testRunRandomBSV() {
      ur.run( ds2d, ds2cv );
      double actualReturn = ur.getBsv();
      double expectedReturn = 0.026; // using eqn 18-23
      assertEquals( "return value", expectedReturn, actualReturn, 0.001 );
   }

   public void testRunRandomZ() {
      ur.run( ds2d, ds2cv );
      double actualReturn = ur.getZ();
      double expectedReturn = 1.60;
      assertEquals( "return value", expectedReturn, actualReturn, 0.01 );
   }

}