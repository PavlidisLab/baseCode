package baseCode.io.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import baseCode.dataStructure.matrix.NamedMatrix;
import baseCode.dataStructure.matrix.SparseRaggedDoubleMatrix2DNamed;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;

/**
 * Best data structure for reading really big, really sparse matrices when a matrix represetation is needed.
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

   /**
    * Use this to read one row from a matrix. It does not close the reader. (this actually has to read several lines to
    * get the data for one matrix row)
    * 
    * @param stream
    * @return @throws IOException
    */
   public NamedMatrix readOneRow( BufferedReader dis ) throws IOException {
      SparseRaggedDoubleMatrix2DNamed returnVal = new SparseRaggedDoubleMatrix2DNamed();

      String row = dis.readLine();

  //    if ( row == null ) return null;

      StringTokenizer tok = new StringTokenizer( row, " \t" );

   //   if ( !tok.hasMoreTokens() ) return null;

      int index = ( new Integer( Integer.parseInt( tok.nextToken() ) ) )
            .intValue();

      int amount = ( new Integer( Integer.parseInt( tok.nextToken() ) ) )
            .intValue();

      IntArrayList rowind = readOneIndexRow( dis, amount );
      DoubleArrayList values = readOneValueRow( dis, amount );

      returnVal.addRow( new Integer( index ).toString(), values, rowind ); // todo - this doesn't make it symmetric.s
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

      dis.readLine(); // discard the first line.
      
      while ( ( row = dis.readLine() ) != null ) {

         StringTokenizer tok = new StringTokenizer( row, " \t" );

         int index = ( new Integer( Integer.parseInt( tok.nextToken() ) ) )
               .intValue();

         int amount = ( new Integer( Integer.parseInt( tok.nextToken() ) ) )
               .intValue();

         if ( ( index % 500 ) == 0 ) {
            log.warn( new String( "loading  " + index + "th entry" ) );
         }

         IntArrayList rowind = readOneIndexRow( dis, amount );
         DoubleArrayList values = readOneValueRow( dis, amount );

         returnVal.addRow( new Integer( k ).toString(), values, rowind ); // todo - this doesn't make it symmetric.
         k++;
      }
      //   ff.close();
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
            throw new IllegalStateException( "Too many tokens (" + values.size() + ", expected " + amount + ")" );
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
         int ind = ( new Integer( Integer.parseInt( tokb.nextToken() ) ) )
               .intValue();
         rowind.add( ind );
         if ( rowind.size() > amount ) {
            throw new IllegalStateException( "Too many tokens (" + rowind.size() + ", expected " + amount + ")" );
         }
      }
      return rowind;
   }

}