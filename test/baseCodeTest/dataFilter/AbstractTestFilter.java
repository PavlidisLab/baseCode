package baseCodeTest.dataFilter;

import junit.framework.TestCase;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.reader.DoubleMatrixReader;
import baseCode.dataStructure.reader.StringMatrixReader;
import baseCode.dataStructure.StringMatrix2DNamed;
/**
 * @author Pavlidis
 * @version $Id$
 *
 */

public abstract class AbstractTestFilter extends TestCase {

   protected DenseDoubleMatrix2DNamed testdata;
   protected StringMatrix2DNamed teststringdata;

   public AbstractTestFilter() {
      super();
   }

   protected void setUp() throws Exception {
      super.setUp();
      DoubleMatrixReader f = new DoubleMatrixReader();
      StringMatrixReader s = new StringMatrixReader();
      testdata =
         (DenseDoubleMatrix2DNamed)f.read(
            AbstractTestFilter.class.getResourceAsStream(
               "/data/testdata.txt"));
      teststringdata =
         (StringMatrix2DNamed)s.read(
            AbstractTestFilter.class.getResourceAsStream(
               "/data/testdata.txt"));
   }

   protected void tearDown() throws Exception {
      super.tearDown();
      testdata = null;
      teststringdata = null;
   }

}
