package baseCodeTest.dataStructure.reader;

import junit.framework.*;
import baseCode.dataStructure.reader.*;
import java.io.*;
import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2004</p>
 * <p>Institution: Columbia University</p>
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
      String filename = TestMapReader.class.getResource("/data/testmap.txt").getFile();
      int expectedReturn = 100;
      int actualReturn = mapReader.read(filename).size();
      assertEquals("return value", expectedReturn, actualReturn);
      /**@todo fill in the test code*/
   }

}
