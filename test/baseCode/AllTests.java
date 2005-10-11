package baseCode;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import baseCode.bio.geneset.TestGeneAnnotations;
import baseCode.dataStructure.TestQueue;
import baseCode.dataStructure.TestStack;
import baseCode.dataStructure.graph.TestDirectedGraph;
import baseCode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamedTest;
import baseCode.dataStructure.matrix.TestRCDoubleMatrix1D;
import baseCode.dataStructure.matrix.TestSparseRaggedDoubleMatrix2DNamed;
import baseCode.io.TestByteArrayConverter;
import baseCode.io.reader.TestDoubleMatrixReader;
import baseCode.io.reader.TestMapReader;
import baseCode.io.reader.TestSparseDoubleMatrixReader;
import baseCode.io.reader.TestSparseRaggedDouble2DNamedMatrixReader;
import baseCode.io.reader.TestStringMatrixReader;
import baseCode.io.writer.TestHistogramWriter;
import baseCode.math.LinearModelTest;
import baseCode.math.StringDistanceTest;
import baseCode.math.TestCorrelationStats;
import baseCode.math.TestWilcoxon;
import baseCode.math.distribution.TestWishart;
import baseCode.math.metaanalysis.TestCorrelationEffectMetaAnalysis;
import baseCode.math.metaanalysis.TestMeanDifferenceMetaAnalysis;
import baseCode.util.FileToolsTest;
import baseCode.util.RCommandTest;
import baseCode.util.TestStringUtil;
import baseCode.xml.TestGOParser;

public class AllTests extends TestCase {

    public AllTests( String s ) {
        super( s );
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTestSuite( TestByteArrayConverter.class );

        /* File reading/writing tests */
        suite.addTestSuite( TestStringMatrixReader.class );
        suite.addTestSuite( TestDoubleMatrixReader.class );
        suite.addTestSuite( TestSparseDoubleMatrixReader.class );
        suite.addTestSuite( TestSparseRaggedDouble2DNamedMatrixReader.class );
        suite.addTestSuite( TestHistogramWriter.class );
        suite.addTestSuite( TestMapReader.class );
        suite.addTestSuite( TestGeneAnnotations.class );

        /* data structure tests */
        suite.addTestSuite( TestDirectedGraph.class );
        suite.addTestSuite( FastRowAccessDoubleMatrix2DNamedTest.class );
        suite.addTestSuite( TestSparseRaggedDoubleMatrix2DNamed.class );
        suite.addTestSuite( TestRCDoubleMatrix1D.class );
        suite.addTestSuite( TestQueue.class );
        suite.addTestSuite( TestStack.class );

        /* data filter tests */
        suite.addTestSuite( baseCode.dataFilter.TestRowAffyNameFilter.class );
        suite.addTestSuite( baseCode.dataFilter.TestRowNameFilter.class );
        suite.addTestSuite( baseCode.dataFilter.TestRowAbsentFilter.class );
        suite.addTestSuite( baseCode.dataFilter.TestRowMissingFilter.class );
        suite.addTestSuite( baseCode.dataFilter.TestRowLevelFilter.class );
        suite.addTestSuite( baseCode.dataFilter.TestItemLevelFilter.class );

        /* math tests */
        suite.addTestSuite( StringDistanceTest.class );
        suite.addTestSuite( LinearModelTest.class );
        suite.addTestSuite( TestWilcoxon.class );
        suite.addTestSuite( baseCode.math.TestDescriptiveWithMissing.class );
        suite.addTestSuite( baseCode.math.TestKSTest.class );
        suite.addTestSuite( TestCorrelationStats.class );
        suite.addTestSuite( baseCode.math.TestRank.class );
        suite.addTestSuite( baseCode.math.TestMatrixRowStats.class );
        suite.addTestSuite( baseCode.math.TestMatrixStats.class );
        suite.addTestSuite( baseCode.math.TestStats.class );
        suite.addTestSuite( baseCode.math.TestSpecFunc.class );
        suite.addTestSuite( baseCode.math.TestROC.class );
        suite.addTestSuite( TestCorrelationEffectMetaAnalysis.class );
        suite.addTestSuite( TestMeanDifferenceMetaAnalysis.class );
        suite.addTestSuite( TestWishart.class );

        /* XML tests */
        suite.addTestSuite( TestGOParser.class );

        /* util */
        suite.addTestSuite( TestStringUtil.class );
        suite.addTestSuite( FileToolsTest.class );
        suite.addTestSuite( RCommandTest.class );
        return suite;
    }

}