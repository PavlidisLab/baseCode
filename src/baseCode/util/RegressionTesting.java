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
import java.util.Collection;
import java.util.Vector;

import baseCode.dataStructure.matrix.AbstractNamedDoubleMatrix;
import baseCode.dataStructure.matrix.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.matrix.SparseDoubleMatrix2DNamed;
import cern.colt.list.DoubleArrayList;

/**
 * Tools to help make regression testing easier.
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
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
    * @param istream
    * @return
    * @throws IOException
    */
   public static String readTestResult( InputStream istream )
         throws IOException {
      if ( istream == null ) {
         throw new IllegalStateException( "Null stream" );
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
    * @param resourceName
    * @return the contents of the resource as a String
    * @throws IOException
    */
   public static String readTestResult( String resourceName )
         throws IOException {
      InputStream istream = RegressionTesting.class
            .getResourceAsStream( resourceName );
      String result = readTestResult( istream );
      istream.close();
      return result;

   }

   /**
    * Test whether two DoubleArrayLists are 'close enough' to call equal.
    * 
    * @param a
    * @param b
    * @param tolerance
    * @return
    */
   public static boolean closeEnough( DoubleArrayList a, DoubleArrayList b,
         double tolerance ) {
      if ( a.size() != b.size() ) return false;

      for ( int i = 0; i < a.size(); i++ ) {
         if ( Math.abs( a.getQuick( i ) - b.getQuick( i ) ) > tolerance )
               return false;
      }
      return true;
   }

   /**
    * Test whether two DenseDoubleMatrix2DNamed's are 'close enough' to call equal.
    * 
    * @param a
    * @param b
    * @param tolerance
    * @return try if all the values in both matrices are within 'tolerance' of each other.
    */
   public static boolean closeEnough( AbstractNamedDoubleMatrix a,
         AbstractNamedDoubleMatrix b, double tolerance ) {
      if ( a.rows() != b.rows() || a.columns() != b.columns() ) return false;

      for ( int i = 0; i < a.rows(); i++ ) {
         for ( int j = 0; j < a.columns(); j++ ) {
            if ( Math.abs( a.getQuick( i, j ) - b.getQuick( i, j ) ) > tolerance )
                  return false;
         }
      }
      return true;
   }

   /**
    * Test whether two SparseDoubleMatrix2DNamed are 'close enough' to call equal.
    * 
    * @param a
    * @param b
    * @param tolerance
    * @return
    */
   public static boolean closeEnough( SparseDoubleMatrix2DNamed a,
         SparseDoubleMatrix2DNamed b, double tolerance ) {
      if ( a.rows() != b.rows() || a.columns() != b.columns() ) return false;

      for ( int i = 0; i < a.rows(); i++ ) {
         for ( int j = 0; j < a.columns(); j++ ) {
            if ( Math.abs( a.getQuick( i, j ) - b.getQuick( i, j ) ) > tolerance )
                  return false;
         }
      }
      return true;
   }

   /**
    * Test whether two object arrays are the same.
    * 
    * @param a
    * @param b
    * @return
    */
   public static boolean closeEnough( Object[] a, Object[] b ) {
      if ( a.length != b.length ) {
         return false;
      }

      for ( int i = 0; i < a.length; i++ ) {
         if ( !a[i].equals( b[i] ) ) {
            return false;
         }
      }
      return true;
   }

   /**
    * Test whether two collections contain the same items.
    * 
    * @param a
    * @param b
    * @return
    */
   public static boolean containsSame( Collection a, Collection b ) {
      if ( a.size() != b.size() ) return false;

      if ( !a.containsAll( b ) )
            return false;

      return true;
   }

   /**
    * Test whether two object arrays contain the same items. The arrays are treated as Sets - repeats are not considered.
    * 
    * @param a
    * @param b
    * @return
    */
   public static boolean containsSame( Object[] a, Object[] b ) {
      if ( a.length != b.length ) return false;
      
      Vector av = new Vector(a.length);
      Vector bv = new Vector(b.length);
      
      for ( int i = 0; i < b.length; i++ ) {
         av.add(a[i]);
         bv.add(b[i]);
      }

      return av.containsAll(bv) ;

   }

   /**
    * Test whether two double arrays contain the same items in any order (tolerance is ZERO)
    * 
    * @param a
    * @param b
    * @return
    */
   public static boolean containsSame( double[] a, double[] b ) {
      if ( a.length != b.length ) return false;

      Vector av = new Vector(a.length);
      Vector bv = new Vector(b.length);
      for ( int i = 0; i < b.length; i++ ) {
         av.add(new Double(a[i]));
         bv.add(new Double(b[i]));
      }

      return av.containsAll(bv) ;
   }

   /**
    * Convenience for using Stuart D. Gathman's Diff.
    * 
    * @param expected String
    * @param actual String
    * @return String edit list
    */
   /**
    * public static String regress( String expected, String actual ) { Diff diff = new Diff( new Object[] {expected} ,
    * new Object[] {actual} ); Diff.change script = diff.diff_2( false ); DiffPrint.Base p = new DiffPrint.UnifiedPrint(
    * new Object[] {expected} , new Object[] {actual} ); StringWriter wtr = new StringWriter(); p.setOutput( wtr );
    * p.print_script( script ); return wtr.toString(); }
    */

}