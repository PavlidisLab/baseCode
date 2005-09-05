package baseCode.dataFilter;

import baseCode.dataStructure.matrix.DoubleMatrixNamed;
import baseCode.dataStructure.matrix.StringMatrix2DNamed;

/**
 * @author Pavlidis
 * @version $Id: TestAffymetrixProbeNameFilter.java,v 1.5 2004/06/23 22:13:21
 *          pavlidis Exp $
 *  
 */
public class TestRowAffyNameFilter extends AbstractTestFilter {

   protected void setUp() throws Exception {
      super.setUp();
   }

   protected void tearDown() throws Exception {
      super.tearDown();
   }

   public void testFilter() {
      RowAffyNameFilter fi = new RowAffyNameFilter( new int[] {
            RowAffyNameFilter.AFFX, RowAffyNameFilter.X, RowAffyNameFilter.ST,
            RowAffyNameFilter.F } );
      DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) fi
            .filter( testdata );
      int expectedReturn = teststringdata.rows() - 5; // file contains on AFFX,
      // and two _f_ tags.
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testStringFilter() {
      RowAffyNameFilter fi = new RowAffyNameFilter( new int[] {
            RowAffyNameFilter.AFFX, RowAffyNameFilter.X, RowAffyNameFilter.ST,
            RowAffyNameFilter.F } );
      StringMatrix2DNamed filtered = ( StringMatrix2DNamed ) fi
            .filter( teststringdata );
      int expectedReturn = teststringdata.rows() - 5; // file contains on AFFX,
      // and two _f_ tags.
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

}