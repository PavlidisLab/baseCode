package baseCode.io.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import baseCode.dataStructure.matrix.NamedMatrix;
import baseCode.dataStructure.matrix.RCDoubleMatrix1D;
import baseCode.dataStructure.matrix.SparseRaggedDoubleMatrix2DNamed;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;
import cern.colt.matrix.DoubleMatrix1D;

/**
 * Best data structure for reading really big, really sparse matrices when a matrix represetation is needed. *
 * <p>
 * The standard format looks like this:
 * 
 * <pre>
 * 
 *  
 *   
 *                            2          &lt;--- number of items - the first line of the file only. NOTE - this line is often blank or not present.
 *                            1 2        &lt;--- items 1 has 2 edges
 *                            1 2        &lt;--- edge indices are to items 1 &amp; 2
 *                            0.1 100    &lt;--- with the following weights
 *                            2 2        &lt;--- items 2 also has 2 edges
 *                            1 2        &lt;--- edge indices are also to items 1 &amp; 2 (fully connected)
 *                            100 0.1    &lt;--- with the following weights
 *   
 *  
 * </pre>
 * 
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class SparseRaggedDouble2DNamedMatrixReader extends
      AbstractNamedMatrixReader {

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.io.reader.AbstractNamedMatrixReader#read(java.lang.String)
    */
   public NamedMatrix read( String filename ) throws IOException {
      File infile = new File( filename );
      if ( !infile.exists() || !infile.canRead() ) {
         throw new IOException( "Could not read from file " + filename );
      }
      FileInputStream stream = new FileInputStream( infile );
      return read( stream );
   }

   public NamedMatrix readOneRow( BufferedReader dis ) throws IOException {
      return this.readOneRow( dis, 0 );
   }

   /**
    * Use this to read one row from a matrix. It does not close the reader. (this actually has to read several lines to
    * get the data for one matrix row)
    * 
    * @param stream
    * @param offset A value indicating the lowest value for the indexes listed. This is here in case the indexes in the stream
    * are numbered starting from 1 instead of zero.
    * @return @throws IOException
    */
   public NamedMatrix readOneRow( BufferedReader dis, int offset )
         throws IOException {
      SparseRaggedDoubleMatrix2DNamed returnVal = new SparseRaggedDoubleMatrix2DNamed();

      String row = dis.readLine(); // line containing the id and the number of edges.
      StringTokenizer tok = new StringTokenizer( row, " \t" );

      int index = Integer.parseInt( tok.nextToken() );
      int amount = Integer.parseInt( tok.nextToken() );
      String rowName = new Integer( index ).toString();
      returnVal.addRow( rowName, readOneRow( dis, amount, offset ) );
      return returnVal;
   }

   /**
    * Read an entire sparse matrix from a stream.
    * 
    * @param stream
    * @return @throws IOException
    */
   public NamedMatrix read( InputStream stream ) throws IOException {
      return this.read( stream, 0 );
   }

   /**
    * Read an entire sparse matrix from a stream.
    * 
    * @param stream
    * @param offset A value indicating the lowest value for the indexes listed. This is here in case the indexes in the stream
    * are numbered starting from 1 instead of zero.
    * @return @throws IOException
    */
   public NamedMatrix read( InputStream stream, int offset ) throws IOException {
      BufferedReader dis = new BufferedReader( new InputStreamReader( stream ) );
      SparseRaggedDoubleMatrix2DNamed returnVal = new SparseRaggedDoubleMatrix2DNamed();

      String row;
      int k = 1;

      while ( ( row = dis.readLine() ) != null ) {

         if ( row.equals( "" ) ) { // incase there is a blank line.
            continue;
         }

         StringTokenizer tok = new StringTokenizer( row, " \t" );

         if ( tok.countTokens() != 2 ) { // in case the row count is there.
            continue;
         }

         int index = Integer.parseInt( tok.nextToken() );

         int amount = Integer.parseInt( tok.nextToken() );

         if ( ( index % 500 ) == 0 ) {
            log.info( new String( "loading  " + index + "th entry" ) );
         }

         returnVal.addRow( new Integer( k ).toString(), readOneRow( dis,
               amount, offset ) );

         k++;
      }

      dis.close();
      return returnVal;
   }

   private DoubleMatrix1D readOneRow( BufferedReader dis, int amount, int offset )
         throws IOException {

      String rowInd = dis.readLine(); // row with indices.
      String rowWei = dis.readLine(); // row with weights.

      StringTokenizer tokw = new StringTokenizer( rowWei, " \t" );
      StringTokenizer toki = new StringTokenizer( rowInd, " \t" );

      OpenIntIntHashMap map = new OpenIntIntHashMap( amount, 0.4, 0.8 );
      DoubleArrayList values = new DoubleArrayList( amount );
      DoubleArrayList finalValues = new DoubleArrayList( amount );

      int i = 0;
      while ( toki.hasMoreTokens() ) {

         double eval = Double.parseDouble( tokw.nextToken() );
         int ind = Integer.parseInt( toki.nextToken() ) - offset;

         map.put( ind, i );
         values.add( eval );
         i++;
      }

      IntArrayList indexes = map.keys();
      indexes.sort();
      int[] ix = indexes.elements();
      for ( int j = ix.length - 1; j >= 0; j-- ) {
         finalValues.add( values.get( map.get( ix[j] ) ) );
      }

      return new RCDoubleMatrix1D( indexes, finalValues );
   }

}