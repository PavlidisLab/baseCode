package baseCodeTest.xml;

import junit.framework.*;
import baseCode.xml.*;
import java.io.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2004</p>
 * <p>Institution: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */

public class TestGOParser extends TestCase {
   private GOParser gOParser = null;

   protected void setUp() throws Exception {
      super.setUp();
      gOParser = new GOParser();
   }

   protected void tearDown() throws Exception {
      gOParser = null;
      super.tearDown();
   }

   public void testGOParser() throws IOException, SAXException, ParserConfigurationException {
      gOParser = new GOParser();
      /**@todo fill in the test code*/
   }

}
