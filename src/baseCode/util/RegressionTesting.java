package baseCode.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.InputStream;

public class RegressionTesting {

   private RegressionTesting() {}

 //  private String resourcePath = "";

   public static void writeTestResult( String result, String fileName ) throws IOException {

      BufferedWriter buf = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new
          File( fileName ) ) ) );
      BufferedReader resultBuf = new BufferedReader( new StringReader( result ) );

      String line = null;
      while ( ( line = resultBuf.readLine() ) != null ) {
         buf.write( line + "\n" );
      }
      buf.close();
      resultBuf.close();
   }


   /**
    * @param resourceName
    * @return
    * @throws IOException
    */
   public static String readTestResult( String resourceName ) throws IOException {
      InputStream istream = RegressionTesting.class.
          getResourceAsStream( resourceName );

      if ( istream == null ) {
         throw new IllegalStateException( "Resource " + resourceName + " not found" );
      }

      BufferedReader buf = new BufferedReader( new InputStreamReader( istream ) );
      String line = "";
      StringBuffer testOutput = new StringBuffer( line );
      while ( ( line = buf.readLine() ) != null ) {
         testOutput.append( line + "\n" );
      }
      buf.close();
      return testOutput.toString();
   }

   /**
    * Convenience for using Stuart D. Gathman's Diff.
    *
    * @param expected String
    * @param actual String
    * @return String edit list
    */
 /**  public static String regress( String expected, String actual ) {
      Diff diff = new Diff( new Object[] {expected}
                            , new Object[] {actual} );
      Diff.change script = diff.diff_2( false );
      DiffPrint.Base p = new DiffPrint.UnifiedPrint( new Object[] {expected}
          , new Object[] {actual} );
      StringWriter wtr = new StringWriter();
      p.setOutput( wtr );
      p.print_script( script );
      return wtr.toString();
   }
   */

}
