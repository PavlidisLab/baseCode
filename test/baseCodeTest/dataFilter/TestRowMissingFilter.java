package baseCodeTest.dataFilter;

import baseCode.dataFilter.RowMissingFilter;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.StringMatrix2DNamed;

/**
 * See the file test/data/matrix-testing.xls for the validation.
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id: TestRowMissingFilter.java,v 1.1 2004/06/28 11:15:32 pavlidis
 *          Exp $
 */
public class TestRowMissingFilter extends AbstractTestFilter {

   RowMissingFilter f = null;

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      f = new RowMissingFilter();
   }

   /*
    * @see TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      f = null;
      super.tearDown();
   }

   public void testFilter() {
      f.setMinPresentCount( 12 );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int expectedReturn = testdata.rows();
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testFilterNoFiltering() {
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int expectedReturn = testdata.rows();
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testFilterWithMissing() {
      f.setMinPresentCount( 12 );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testmissingdata );
      int expectedReturn = 21;
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testFilterWithMissingLowMaxFraction() {
      f.setMaxFractionRemoved( 0.1 );
      f.setMinPresentCount( 12 );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testmissingdata );
      int expectedReturn = 21;
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testFilterWithMissingLessStringent() {

      f.setMinPresentCount( 10 );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testmissingdata );
      int expectedReturn = 29;
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testFilterStringMatrix() {
      f.setMinPresentCount( 12 );
      StringMatrix2DNamed filtered = ( StringMatrix2DNamed ) f
            .filter( teststringmissingdata );
      int expectedReturn = 21;
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testFilterFraction() {
      f.setMinPresentFraction( 1.0 );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testmissingdata );
      int expectedReturn = 21;
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testFilterFractionInvalid() {
      try {
         f.setMinPresentFraction( 934109821 );
         fail( "Should have gotten an exception" );
      } catch ( IllegalArgumentException e ) {
      }

   }

   public void testFilterFractionInvalid2() {
      try {
         f.setMinPresentCount( -1093 );
         fail( "Should have gotten an exception" );
      } catch ( IllegalArgumentException e ) {
      }

   }

   public void testMaxFractionRemovedInvalid() {
      try {
         f.setMaxFractionRemoved( 934109821 );
         fail( "Should have gotten an exception" );
      } catch ( IllegalArgumentException e ) {
      }

   }

   public void testFilterPresentCountInvalid() {
      try {
         f.setMinPresentCount( 129 );
         DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
               .filter( testmissingdata );
         fail( "Should have gotten an exception" );
      } catch ( IllegalStateException e ) {
      }

   }

}