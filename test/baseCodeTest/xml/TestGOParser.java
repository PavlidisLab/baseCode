package baseCodeTest.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestCase;
import org.xml.sax.SAXException;
import baseCode.xml.GOParser;
import baseCode.util.RegressionTesting;

/**
 * <p>Copyright (c) 2004</p>
 * <p>Institution: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */

public class TestGOParser
    extends TestCase {
   private GOParser gOParser = null;

   protected void setUp() throws Exception {
      super.setUp();
      InputStream i =
         GOParser.class.getResourceAsStream( "/data/go-termdb-sample.xml" );
    //  GOParser.class.getResourceAsStream( "/data/go_200406-termdb.xml" );
      if ( i == null ) {
         throw new Exception( "Couldn't read the sample file" );
      }
      gOParser = new GOParser( i );

   }

   protected void tearDown() throws Exception {
      gOParser = null;
      super.tearDown();
   }

   /** @todo set up mechanism for writing results if they don't exist.*/
   public void testGOParser() throws IOException, SAXException, ParserConfigurationException {
      String actualReturn = gOParser.getGraph().toString();
      String expectedReturn = RegressionTesting.readTestResult( "/data/goparsertestoutput.txt" );
      assertEquals( "Diffs: " + RegressionTesting.regress( expectedReturn, actualReturn ),
                    expectedReturn, actualReturn );
   }



}
