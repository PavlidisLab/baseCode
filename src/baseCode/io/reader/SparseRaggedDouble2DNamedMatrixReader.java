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
 *    
 *     
 *      
 *       
 *        
 *         
 *                    
 *                         2          &lt;--- number of items - the first line of the file only. NOTE - this line is often blank or not present.
 *                         1 2        &lt;--- items 1 has 2 edges
 *                         1 2        &lt;--- edge indices are to items 1 &amp; 2
 *                         0.1 100    &lt;--- with the following weights
 *                         2 2        &lt;--- items 2 also has 2 edges
 *                         1 2        &lt;--- edge indices are also to items 1 &amp; 2 (fully connected)
 *                         100 0.1    &lt;--- with the following weights
 *           
 *          
 *         
 *        
 *       
 *      
 *     
 *    
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

   private static final int INITIAL_SIZE = 10000;

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

   /**
    * Use this to read one row from a matrix. It does not close the reader. (this actually has to read several lines to
    * get the data for one matrix row)
    * 
    * @param stream
    * @return @throws IOException
    */
   public NamedMatrix readOneRow( BufferedReader dis ) throws IOException {
      SparseRaggedDoubleMatrix2DNamed returnVal = new SparseRaggedDoubleMatrix2DNamed();

      String row = dis.readLine(); // line containing the id and the number of edges.
      StringTokenizer tok = new StringTokenizer( row, " \t" );

      int index = Integer.parseInt( tok.nextToken() );
      int amount = Integer.parseInt( tok.nextToken() );
      String rowName = new Integer( index ).toString();
      returnVal.addRow( rowName, readOneRow( dis, amount ) );
      return returnVal;
   }

   /**
    * Read an entire sparse matrix from a stream.
    * 
    * @param stream
    * @return @throws IOException
    */
   public NamedMatrix read( InputStream stream ) throws IOException {
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
            log.warn( new String( "loading  " + index + "th entry" ) );
         }

         //         IntArrayList rowind = readOneIndexRow( dis, amount );
         //         DoubleArrayList values = readOneValueRow( dis, amount );
         returnVal.addRow( new Integer( k ).toString(),
               readOneRow( dis, amount ) );
         //         returnVal.addRow( new Integer( k ).toString(), values, rowind ); // todo - this doesn't make it symmetric.
         k++;
      }

      dis.close();
      return returnVal;
   }

   /**
    * @param dis
    * @param amount
    * @return @throws IOException
    */
   private DoubleArrayList readOneValueRow( BufferedReader dis, int amount )
         throws IOException {

      DoubleArrayList values = new DoubleArrayList( amount );
      String row = dis.readLine(); // row with weights.
      StringTokenizer tokb = new StringTokenizer( row, " \t" );

      while ( tokb.hasMoreTokens() ) {
         double eval = ( new Double( Double.parseDouble( tokb.nextToken() ) ) )
               .doubleValue();
         values.add( eval );
         if ( values.size() > amount ) {
            throw new IllegalStateException( "Too many tokens ("
                  + values.size() + ", expected " + amount + ")" );
         }
      }
      return values;
   }

   /**
    * @param dis
    * @param amount
    * @return @throws IOException
    */
   private IntArrayList readOneIndexRow( BufferedReader dis, int amount )
         throws IOException {

      IntArrayList rowind = new IntArrayList( amount );
      String row = dis.readLine(); // row with indices.
      StringTokenizer tokb = new StringTokenizer( row, " \t" );
      while ( tokb.hasMoreTokens() ) {
         int ind = Integer.parseInt( tokb.nextToken() );

         if ( rowind.size() > 0 && ind > rowind.getQuick( rowind.size() - 1 ) ) {
            throw new IllegalStateException(
                  "Indices must be given in ascending order." );
         }

         rowind.add( ind );
         if ( rowind.size() > amount ) {
            throw new IllegalStateException( "Too many tokens ("
                  + rowind.size() + ", expected " + amount + ")" );
         }
      }
      return rowind;
   }

   private DoubleMatrix1D readOneRow( BufferedReader dis, int amount )
         throws IOException {
      
      
      String rowInd = dis.readLine(); // row with indices.
      String rowWei = dis.readLine(); // row with weights.

      StringTokenizer tokw = new StringTokenizer( rowWei, " \t" );
      StringTokenizer toki = new StringTokenizer( rowInd, " \t" );

      IntArrayList indexes = new IntArrayList( INITIAL_SIZE );
      DoubleArrayList values = new DoubleArrayList( INITIAL_SIZE );

      while ( toki.hasMoreTokens() ) {
         double eval = Double.parseDouble( tokw.nextToken() );
         int ind = Integer.parseInt( toki.nextToken() ) - 1; // this is a JW thing - the indexes start at 1.

         if (ind < 0) {
            continue; // assume this is garbage.
         }
         
         /*
          * index of the search key, if it is contained in the receiver; otherwise, (-(insertion point) - 1). The
          * insertion point is defined as the the point at which the value would be inserted into the receiver: the
          * index of the first element greater than the key, or receiver.size(), if all elements in the receiver are
          * less than the specified key. Note that this guarantees that the return value will be >= 0 if and only if the
          * key is found.
          */
         int k = indexes.binarySearch( ind );
         if ( k >= 0 ) { // found. This shouldn't happen - if it does it means there is a duplicate.
            values.setQuick( k, eval );
         }

         if ( eval != 0.0 ) {
            k = -k - 1;
            
            if (k < -1) {
               throw new IllegalStateException("k was less than zero...");
            }
            
            indexes.beforeInsert( k, ind ); // this is slow.
            values.beforeInsert( k, eval ); // this is slow.
         }

         if ( values.size() > amount ) {
            throw new IllegalStateException( "Too many tokens ("
                  + values.size() + ", expected " + amount + ")" );
         }
      }
      return new RCDoubleMatrix1D( values, indexes );

   }

}