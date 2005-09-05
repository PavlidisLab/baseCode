package baseCode.dataFilter;

import java.util.HashSet;
import java.util.Set;

import baseCode.dataStructure.matrix.DoubleMatrixNamed;
import baseCode.dataStructure.matrix.StringMatrix2DNamed;

/**
 * @author Pavlidis
 * @version $Id$
 */

public class TestRowNameFilter extends AbstractTestFilter {

    protected Set testfilterlist;

    protected void setUp() throws Exception {
        super.setUp();

        testfilterlist = new HashSet();
        testfilterlist.add( "gene1_at" );
        testfilterlist.add( "gene4_at" );
        testfilterlist.add( "gene13_s_at" );
        testfilterlist.add( "AFFXgene30_at" );
        testfilterlist.add( "fooblydoobly" ); // shouldn't care.
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        testfilterlist = null;
    }

    public void testFilter() {
        RowNameFilter fi = new RowNameFilter( testfilterlist );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) fi.filter( testdata );
        int expectedReturn = 4;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterExclude() {
        RowNameFilter fi = new RowNameFilter( testfilterlist, true );
        DoubleMatrixNamed filtered = ( DoubleMatrixNamed ) fi.filter( testdata );
        int expectedReturn = testdata.rows() - 4;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterString() {
        RowNameFilter fi = new RowNameFilter( testfilterlist );
        StringMatrix2DNamed filtered = ( StringMatrix2DNamed ) fi.filter( teststringdata );
        int expectedReturn = 4;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

    public void testFilterStringExclude() {
        RowNameFilter fi = new RowNameFilter( testfilterlist, true );
        StringMatrix2DNamed filtered = ( StringMatrix2DNamed ) fi.filter( teststringdata );
        int expectedReturn = teststringdata.rows() - 4;
        int actualReturn = filtered.rows();
        assertEquals( "return value", expectedReturn, actualReturn );
    }

}