/*
 * Created on Jun 16, 2004
 *
 */
package baseCodeTest.dataFilter;

import java.util.HashSet;
import java.util.Set;

import baseCode.dataFilter.RowNameFilter;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.reader.DoubleMatrixReader;
import junit.framework.TestCase;

/**
 * @author Owner
 * 
 */
public class TestRowNameFilter extends TestCase {

	DenseDoubleMatrix2DNamed testdata;
	DoubleMatrixReader f;
	Set testfilterlist;
	
	/**
	 * Constructor for RowNameFilterTest.
	 * @param arg0
	 */
	public TestRowNameFilter(String arg0) {
		super(arg0);
	}
	

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		f = new DoubleMatrixReader();
		testdata = (DenseDoubleMatrix2DNamed)f.read("C:/Program Files/eclipse/workspace/baseCode/testreallybig.txt");
		testfilterlist = new HashSet();
		
		testfilterlist.add("AB002380_at");
		testfilterlist.add("L31584_at");
		testfilterlist.add("L32832_s_at");
		testfilterlist.add("L36463_at");
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		testfilterlist = null;
		f = null;
		testdata = null;
	}

	public void testFilter() {
		RowNameFilter fi = new RowNameFilter(testfilterlist);
		DenseDoubleMatrix2DNamed filtered = (DenseDoubleMatrix2DNamed)fi.filter(testdata);
		System.err.print(filtered.toString());
	}

	public void testFilterExclude() {
		RowNameFilter fi = new RowNameFilter(testfilterlist, true);
		fi.filter(testdata);
	}


}
