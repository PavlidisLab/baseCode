package baseCodeTest.dataFilter;

import java.util.HashSet;
import java.util.Set;

import baseCode.dataFilter.RowNameFilter;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.StringMatrix2DNamed;

/**
 * @author Pavlidis
 * @version $Id$
 *
 */

public class TestRowNameFilter
    extends AbstractTestFilter {

   protected Set testfilterlist;

   protected void setUp() throws Exception {
      super.setUp();

      testfilterlist = new HashSet();
      testfilterlist.add( "AB002380_at" );
      testfilterlist.add( "L31584_at" );
      testfilterlist.add( "L32832_s_at" );
      testfilterlist.add( "L36463_at" );
      testfilterlist.add( "fooblydoobly" ); // shouldn't care.
   }

   protected void tearDown() throws Exception {
      super.tearDown();
      testfilterlist = null;
   }

   public void testFilter() {
      RowNameFilter fi = new RowNameFilter( testfilterlist );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) fi.filter( testdata );
      int expectedReturn = 4;
      int actualReturn = filtered.rows();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testFilterExclude() {
      RowNameFilter fi = new RowNameFilter( testfilterlist, true );
      DenseDoubleMatrix2DNamed filtered = ( DenseDoubleMatrix2DNamed ) fi.filter( testdata );
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
