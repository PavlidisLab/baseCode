package baseCodeTest.dataFilter;

import baseCode.dataFilter.AffymetrixProbeNameFilter;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.StringMatrix2DNamed;

/**
 * @author Pavlidis
 * @version $Id: TestAffymetrixProbeNameFilter.java,v 1.5 2004/06/23 22:13:21
 *          pavlidis Exp $
 *  
 */
public class TestAffymetrixProbeNameFilter extends AbstractTestFilter {

   protected void setUp() throws Exception {
      super.setUp();
   }

   protected void tearDown() throws Exception {
      super.tearDown();
   }

   public void testFilter() {
      AffymetrixProbeNameFilter fi = new AffymetrixProbeNameFilter( new int[] {
            AffymetrixProbeNameFilter.AFFX, AffymetrixProbeNameFilter.X,
            AffymetrixProbeNameFilter.ST, AffymetrixProbeNameFilter.F } );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) fi
            .filter( testdata );
      int expectedReturn = teststringdata.rows() - 5; // file contains on AFFX,
                                                      // and two _f_ tags.
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testStringFilter() {
      AffymetrixProbeNameFilter fi = new AffymetrixProbeNameFilter( new int[] {
            AffymetrixProbeNameFilter.AFFX, AffymetrixProbeNameFilter.X,
            AffymetrixProbeNameFilter.ST, AffymetrixProbeNameFilter.F } );
      StringMatrix2DNamed filtered = ( StringMatrix2DNamed ) fi
            .filter( teststringdata );
      int expectedReturn = teststringdata.rows() - 5; // file contains on AFFX,
                                                      // and two _f_ tags.
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

}