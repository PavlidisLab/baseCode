package baseCodeTest.dataFilter;

import baseCode.dataFilter.AffymetrixProbeNameFilter;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.StringMatrix2DNamed;

/**
 * @author Pavlidis
 * @version $Id$
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
      AffymetrixProbeNameFilter fi = new AffymetrixProbeNameFilter();
      DenseDoubleMatrix2DNamed filtered =
         (DenseDoubleMatrix2DNamed)fi.filter(testdata);
      int expectedReturn = filtered.rows();
      int actualReturn = filtered.rows();
      assertEquals("return value", expectedReturn, actualReturn);
   }

   public void testStringFilter() {
      AffymetrixProbeNameFilter fi = new AffymetrixProbeNameFilter();
      StringMatrix2DNamed filtered =
         (StringMatrix2DNamed)fi.filter(teststringdata);
      int expectedReturn = filtered.rows();
      int actualReturn = filtered.rows();
      assertEquals("return value", expectedReturn, actualReturn);
   }

}
