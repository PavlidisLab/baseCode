package baseCodeTest;

import junit.framework.*;

public class AllTests
    extends TestCase {

   public AllTests( String s ) {
      super( s );
   }

   public static Test suite() {
      TestSuite suite = new TestSuite();
      suite.addTestSuite( baseCodeTest.dataFilter.TestAffymetrixProbeNameFilter.class );
      suite.addTestSuite( baseCodeTest.dataFilter.TestRowNameFilter.class );
 //     suite.addTestSuite( baseCodeTest.gui.TestTreePanel.class );
      suite.addTestSuite( baseCodeTest.math.TestDescriptiveWithMissing.class );
      suite.addTestSuite( baseCodeTest.xml.TestGOParser.class );
      suite.addTestSuite( baseCodeTest.util.TestDiff.class );
      return suite;
   }
}
