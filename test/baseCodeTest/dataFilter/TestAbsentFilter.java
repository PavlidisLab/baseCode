package baseCodeTest.dataFilter;

import baseCode.dataFilter.AbsentFilter;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.StringMatrix2DNamed;
import baseCode.dataStructure.reader.StringMatrixReader;

/**
 * Copyright (c) 2004 Columbia University
 * @author Owner
 * @version $Id$
 */
public class TestAbsentFilter extends AbstractTestFilter {

    StringMatrix2DNamed testpdata = null;
    AbsentFilter f = null;
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        f = new AbsentFilter();
        StringMatrixReader s = new StringMatrixReader();
        testpdata = (StringMatrix2DNamed)s.read(
                AbstractTestFilter.class.getResourceAsStream(
                   "/data/test-presence-data.txt"));
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        testpdata = null;
        super.tearDown();
    }


    /*
     * Class under test for DenseDoubleMatrix2DNamed filter(DenseDoubleMatrix2DNamed)
     */
    public void testFilter() {
        f.setFlagMatrix(testpdata);
        f.setMinPresentCount(10);
        DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed )f.filter(testdata);
        int expectedReturn = testdata.rows() - 4;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

}
