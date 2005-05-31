package baseCode.io.reader;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright (c) 2004
 * </p>
 * <p>
 * Institution: Columbia University
 * </p>
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */

public class TestMapReader extends TestCase {
   private MapReader mapReader = null;

   protected void setUp() throws Exception {
      super.setUp();
      mapReader = new MapReader();
   }

   protected void tearDown() throws Exception {
      mapReader = null;
      super.tearDown();
   }

   public void testRead() throws IOException {
      InputStream m = TestMapReader.class
            .getResourceAsStream( "/data/testmap.txt" );
      int expectedReturn = 100;
      int actualReturn = mapReader.read( m, true ).size(); // file has header
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testReadNoHeader() throws IOException {
      InputStream m = TestMapReader.class
            .getResourceAsStream( "/data/testmap.txt" );
      int expectedReturn = 101;
      int actualReturn = mapReader.read( m ).size(); // file has header
      assertEquals( "return value", expectedReturn, actualReturn );
   }

}