package baseCode.dataFilter;

import baseCode.dataFilter.RowAbsentFilter;
import baseCode.dataStructure.matrix.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.matrix.StringMatrix2DNamed;
import baseCode.io.reader.StringMatrixReader;

/**
 * See the file test/data/matrix-testing.xls for the validation.
 * 
 * Copyright (c) 2004 Columbia University
 * 
 * @author Owner
 * @version $Id$
 */
public class TestRowAbsentFilter extends AbstractTestFilter {

   StringMatrix2DNamed testpdata = null;

   RowAbsentFilter f = null;

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      f = new RowAbsentFilter();
      StringMatrixReader s = new StringMatrixReader();
      testpdata = ( StringMatrix2DNamed ) s.read( AbstractTestFilter.class
            .getResourceAsStream( "/data/test-presence-data.txt" ) );
   }

   /*
    * @see TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      testpdata = null;
      testdata = null;
      super.tearDown();
   }

   /*
    * Class under test for DenseDoubleMatrix2DNamed
    * filter(DenseDoubleMatrix2DNamed)
    */
   public void testFilter() {
      f.setFlagMatrix( testpdata );
      f.setMinPresentCount( 12 );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int expectedReturn = testdata.rows() - 7; // 7 rows have some absent or
      // marginal.
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testFilterWithMissing() {
      f.setFlagMatrix( testpdata );
      f.setMinPresentCount( 12 );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testmissingdata );
      int expectedReturn = 17;
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testFilterWithMissingLessStringent() {
      f.setFlagMatrix( testpdata );
      f.setMinPresentCount( 8 );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testmissingdata );
      int expectedReturn = 24;
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testFilterStringMatrix() {
      f.setFlagMatrix( testpdata );
      f.setMinPresentCount( 12 );
      StringMatrix2DNamed filtered = ( StringMatrix2DNamed ) f
            .filter( teststringdata );
      int expectedReturn = testdata.rows() - 7; // 7 rows have some missing or
      // marginal OR absent.
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testFilterKeepMarginal() {
      f.setFlagMatrix( testpdata );
      f.setKeepMarginal( true );
      f.setMinPresentCount( 12 );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int expectedReturn = testdata.rows() - 6; // 6 rows have some absent
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testFilterFraction() {
      f.setFlagMatrix( testpdata );
      f.setMinPresentFraction( 1.0 );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) f
            .filter( testdata );
      int expectedReturn = testdata.rows() - 7; // 7 rows have some missing or
      // marginal.
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testFilterFractionInvalid() {
      try {
         f.setFlagMatrix( testpdata );
         f.setMinPresentFraction( 934109821 );
         f.filter( testdata );
         fail( "Should have gotten an exception" );
      } catch ( IllegalArgumentException e ) {
         System.err.println(e);
      }

   }

   public void testFilterPresentCountInvalid() {
      try {
         f.setFlagMatrix( testpdata );
         f.setMinPresentCount( 129 );
         f.filter( testdata );
         fail( "Should have gotten an exception" );
      } catch ( IllegalStateException e ) {
         System.err.println(e);
      }

   }

   public void testFilterNullFlags() {
      try {
         f.setFlagMatrix( null );
         f.setMinPresentCount( 10 );
         f.filter( testdata );
         fail( "Should have gotten an exception" );
      } catch ( IllegalStateException success ) {
         System.err.println(success);
      } catch ( IllegalArgumentException success ) {
         System.err.println(success);
      }
   }

}