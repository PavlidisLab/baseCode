package baseCodeTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import baseCodeTest.dataStructure.TestQueue;
import baseCodeTest.dataStructure.TestStack;
import baseCodeTest.dataStructure.graph.TestDirectedGraph;
import baseCodeTest.dataStructure.matrix.TestRCDoubleMatrix1D;
import baseCodeTest.dataStructure.matrix.TestSparseRaggedDoubleMatrix2DNamed;
import baseCodeTest.io.reader.TestDoubleMatrixReader;
import baseCodeTest.io.reader.TestMapReader;
import baseCodeTest.io.reader.TestSparseDoubleMatrixReader;
import baseCodeTest.io.reader.TestSparseRaggedDouble2DNamedMatrixReader;
import baseCodeTest.io.reader.TestStringMatrixReader;
import baseCodeTest.io.writer.TestHistogramWriter;
import baseCodeTest.math.TestCorrelationStats;
import baseCodeTest.math.metaanalysis.TestCorrelationEffectMetaAnalysis;
import baseCodeTest.math.metaanalysis.TestMeanDifferenceMetaAnalysis;
import baseCodeTest.xml.TestGOParser;

public class AllTests extends TestCase {

   public AllTests( String s ) {
      super( s );
   }

   public static Test suite() {
      TestSuite suite = new TestSuite();

      /* File reading/writing tests */
      suite.addTestSuite( TestStringMatrixReader.class );
      suite.addTestSuite( TestDoubleMatrixReader.class );
      suite.addTestSuite( TestSparseDoubleMatrixReader.class );
      suite.addTestSuite( TestSparseRaggedDouble2DNamedMatrixReader.class );
      suite.addTestSuite( TestHistogramWriter.class );
      suite.addTestSuite( TestMapReader.class );

      /* data structure tests */
      suite.addTestSuite( TestDirectedGraph.class );
      suite.addTestSuite( TestSparseRaggedDoubleMatrix2DNamed.class );
      suite.addTestSuite( TestRCDoubleMatrix1D.class );
      suite.addTestSuite( TestQueue.class );
      suite.addTestSuite( TestStack.class );

      /* data filter tests */
      suite.addTestSuite( baseCodeTest.dataFilter.TestRowAffyNameFilter.class );
      suite.addTestSuite( baseCodeTest.dataFilter.TestRowNameFilter.class );
      suite.addTestSuite( baseCodeTest.dataFilter.TestRowAbsentFilter.class );
      suite.addTestSuite( baseCodeTest.dataFilter.TestRowMissingFilter.class );
      suite.addTestSuite( baseCodeTest.dataFilter.TestRowLevelFilter.class );
      suite.addTestSuite( baseCodeTest.dataFilter.TestItemLevelFilter.class );

      /* math tests */
      suite.addTestSuite( baseCodeTest.math.TestDescriptiveWithMissing.class );
      suite.addTestSuite( baseCodeTest.math.TestKSTest.class );
      suite.addTestSuite( TestCorrelationStats.class );
      suite.addTestSuite( baseCodeTest.math.TestRank.class );
      suite.addTestSuite( baseCodeTest.math.TestMatrixRowStats.class );
      suite.addTestSuite( baseCodeTest.math.TestMatrixStats.class );
      suite.addTestSuite( baseCodeTest.math.TestStats.class );
      suite.addTestSuite( baseCodeTest.math.TestSpecFunc.class );
      suite.addTestSuite( baseCodeTest.math.TestROC.class );
      suite.addTestSuite( TestCorrelationEffectMetaAnalysis.class );
      suite.addTestSuite( TestMeanDifferenceMetaAnalysis.class );

      /* XML tests */
      suite.addTestSuite( TestGOParser.class );

      return suite;
   }

}