package ubic.basecode.io.reader;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import ubic.basecode.dataStructure.matrix.StringMatrix2DNamed;

/**
 * 
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id: TestStringMatrixReader.java,v 1.1 2004/06/23 22:13:21 pavlidis
 *          Exp $
 */
public class TestStringMatrixReader extends TestCase {

   StringMatrix2DNamed matrix = null;
   InputStream is = null;
   StringMatrixReader reader = null;

   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      reader = new StringMatrixReader();
      is = TestStringMatrixReader.class
            .getResourceAsStream( "/data/testdata.txt" );
   }

   /*
    * @see TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
   }

   /*
    * Class under test for NamedMatrix read(InputStream)
    */
   public void testReadInputStreamRowCount() {
      try {
         matrix = ( StringMatrix2DNamed ) reader.read( is );
         int actualReturn = matrix.rows();
         int expectedReturn = 30;
         assertEquals( "return value", expectedReturn, actualReturn );
      } catch ( IOException e ) {
         e.printStackTrace();
      }
   }

   public void testReadInputStreamColumnCount() {
      try {
         matrix = ( StringMatrix2DNamed ) reader.read( is );
         int actualReturn = matrix.columns();
         int expectedReturn = 12;
         assertEquals( "return value", expectedReturn, actualReturn );
      } catch ( IOException e ) {
         e.printStackTrace();
      }
   }

   public void testReadInputStreamGotRowName() {
      try {
         matrix = ( StringMatrix2DNamed ) reader.read( is );
         boolean actualReturn = matrix.containsRowName( "gene1_at" )
               && matrix.containsRowName( "AFFXgene30_at" );
         boolean expectedReturn = true;
         assertEquals( "return value", expectedReturn, actualReturn );
      } catch ( IOException e ) {
         e.printStackTrace();
      }
   }

   public void testReadInputStreamGotColName() {
      try {
         matrix = ( StringMatrix2DNamed ) reader.read( is );
         boolean actualReturn = matrix.containsColumnName( "sample1" )
               && matrix.containsColumnName( "sample12" );
         boolean expectedReturn = true;
         assertEquals( "return value (for sample1 and sample12)",
               expectedReturn, actualReturn );
      } catch ( IOException e ) {
         e.printStackTrace();
      }
   }

}