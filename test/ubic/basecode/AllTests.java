package ubic.basecode;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ubic.basecode.bio.geneset.TestGeneAnnotations;
import ubic.basecode.dataStructure.TestQueue;
import ubic.basecode.dataStructure.TestStack;
import ubic.basecode.dataStructure.graph.TestDirectedGraph;
import ubic.basecode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamedTest;
import ubic.basecode.dataStructure.matrix.TestRCDoubleMatrix1D;
import ubic.basecode.dataStructure.matrix.TestSparseRaggedDoubleMatrix2DNamed;
import ubic.basecode.io.TestByteArrayConverter;
import ubic.basecode.io.reader.TestDoubleMatrixReader;
import ubic.basecode.io.reader.TestMapReader;
import ubic.basecode.io.reader.TestSparseDoubleMatrixReader;
import ubic.basecode.io.reader.TestSparseRaggedDouble2DNamedMatrixReader;
import ubic.basecode.io.reader.TestStringMatrixReader;
import ubic.basecode.io.writer.TestHistogramWriter;
import ubic.basecode.math.LinearModelTest;
import ubic.basecode.math.MultipleTestCorrectionTest;
import ubic.basecode.math.StringDistanceTest;
import ubic.basecode.math.TestCorrelationStats;
import ubic.basecode.math.TestWilcoxon;
import ubic.basecode.math.distribution.TestWishart;
import ubic.basecode.math.metaanalysis.TestCorrelationEffectMetaAnalysis;
import ubic.basecode.math.metaanalysis.TestMeanDifferenceMetaAnalysis;
import ubic.basecode.util.FileToolsTest;
import ubic.basecode.util.RCommandTest;
import ubic.basecode.util.TestStringUtil;
import ubic.basecode.xml.TestGOParser;

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
        suite.addTestSuite( ubic.basecode.datafilter.TestRowAffyNameFilter.class );
        suite.addTestSuite( ubic.basecode.datafilter.TestRowNameFilter.class );
        suite.addTestSuite( ubic.basecode.datafilter.TestRowAbsentFilter.class );
        suite.addTestSuite( ubic.basecode.datafilter.TestRowMissingFilter.class );
        suite.addTestSuite( ubic.basecode.datafilter.TestRowLevelFilter.class );
        suite.addTestSuite( ubic.basecode.datafilter.TestItemLevelFilter.class );

        /* math tests */
        suite.addTestSuite( StringDistanceTest.class );
        suite.addTestSuite( LinearModelTest.class );
        suite.addTestSuite( TestWilcoxon.class );
        suite.addTestSuite( ubic.basecode.math.TestDescriptiveWithMissing.class );
        suite.addTestSuite( MultipleTestCorrectionTest.class );
        suite.addTestSuite( ubic.basecode.math.TestKSTest.class );
        suite.addTestSuite( TestCorrelationStats.class );
        suite.addTestSuite( ubic.basecode.math.TestRank.class );
        suite.addTestSuite( ubic.basecode.math.TestMatrixRowStats.class );
        suite.addTestSuite( ubic.basecode.math.TestMatrixStats.class );
        suite.addTestSuite( ubic.basecode.math.TestStats.class );
        suite.addTestSuite( ubic.basecode.math.TestSpecFunc.class );
        suite.addTestSuite( ubic.basecode.math.TestROC.class );
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