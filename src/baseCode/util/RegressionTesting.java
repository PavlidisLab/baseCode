package baseCode.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.SparseDoubleMatrix2DNamed;

import cern.colt.list.DoubleArrayList;

/**
 * Tools to help make regression testing easier.
 * 
 *
 * <hr>
 * <p>Copyright (c) 2004 Columbia University
 * @author pavlidis
 * @version $Id$
 */
public class RegressionTesting {

   private RegressionTesting() { /* block instantiation */ 
   }

   //  private String resourcePath = "";

   public static void writeTestResult( String result, String fileName )
         throws IOException {

      BufferedWriter buf = new BufferedWriter( new OutputStreamWriter(
            new FileOutputStream( new File( fileName ) ) ) );
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
    * @return the contents of the resource as a String
    * @throws IOException
    */
   public static String readTestResult( String resourceName )
         throws IOException {
      InputStream istream = RegressionTesting.class
            .getResourceAsStream( resourceName );

      if ( istream == null ) {
         throw new IllegalStateException( "Resource " + resourceName
               + " not found" );
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
    * Test whether two DoubleArrayLists are 'close enough' to call equal.
    * @param a
    * @param b
    * @param tolerance
    * @return
    */
   public static boolean closeEnough(DoubleArrayList a, DoubleArrayList b, double tolerance) {
      if (a.size() != b.size()) return false;
      
      for (int i = 0; i < a.size(); i++) {
         if (Math.abs(a.getQuick(i) - b.getQuick(i)) > tolerance) 
            return false;
      }
      return true;
   }
   
   /**
    * Test whether two DenseDoubleMatrix2DNamed's are 'close enough' to call equal.
    * @param a
    * @param b
    * @param tolerance
    * @return try if all the values in both matrices are within 'tolerance' of each other.
    */
   public static boolean closeEnough(DenseDoubleMatrix2DNamed a, DenseDoubleMatrix2DNamed b, double tolerance) {
      if (a.rows() != b.rows() || a.columns() != b.columns()) return false;
      
      for (int i = 0; i < a.rows(); i++) {
         for (int j = 0; j < a.columns(); j++) {
         if (Math.abs(a.getQuick(i,j) - b.getQuick(i,j)) > tolerance) 
            return false;
         }
      }
      return true;
   }
   
   /**
    * Test whether two SparseDoubleMatrix2DNamed are 'close enough' to call equal.
    * @param a
    * @param b
    * @param tolerance
    * @return
    */
   public static boolean closeEnough(SparseDoubleMatrix2DNamed a, SparseDoubleMatrix2DNamed b, double tolerance) {
      if (a.rows() != b.rows() || a.columns() != b.columns()) return false;
      
      for (int i = 0; i < a.rows(); i++) {
         for (int j = 0; j < a.columns(); j++) {
         if (Math.abs(a.getQuick(i,j) - b.getQuick(i,j)) > tolerance) 
            return false;
         }
      }
      return true;
   }
   
   

   /**
    * Convenience for using Stuart D. Gathman's Diff.
    * 
    * @param expected String
    * @param actual String
    * @return String edit list
    */
   /**
    * public static String regress( String expected, String actual ) { Diff diff =
    * new Diff( new Object[] {expected} , new Object[] {actual} ); Diff.change
    * script = diff.diff_2( false ); DiffPrint.Base p = new
    * DiffPrint.UnifiedPrint( new Object[] {expected} , new Object[] {actual} );
    * StringWriter wtr = new StringWriter(); p.setOutput( wtr ); p.print_script(
    * script ); return wtr.toString(); }
    */

}