package baseCodeTest.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestCase;
import org.xml.sax.SAXException;
import baseCode.xml.GOParser;
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
      InputStream i =
         GOParser.class.getResourceAsStream("/data/go-termdb-sample.xml");

      gOParser = new GOParser(i);

   }

   protected void tearDown() throws Exception {
      gOParser = null;

      super.tearDown();
   }

   public void testGOParser()
      throws IOException, SAXException, ParserConfigurationException {
      //    /**@todo fill in the test code*/
      System.err.print(gOParser.toString());
   }

}
