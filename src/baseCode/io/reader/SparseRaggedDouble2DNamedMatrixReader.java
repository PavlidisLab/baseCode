package baseCode.io.reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import baseCode.dataStructure.matrix.NamedMatrix;
import baseCode.dataStructure.matrix.RCDoubleMatrix1D;
import baseCode.dataStructure.matrix.SparseRaggedDoubleMatrix2DNamed;
import baseCode.util.FileTools;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntDoubleHashMap;
import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;
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
 *           
 *            
 *             
 *              
 *               
 *                
 *                                         2          &lt;--- number of items - the first line of the file only. NOTE - this line is often blank or not present.
 *                                         1 2        &lt;--- items 1 has 2 edges
 *                                         1 2        &lt;--- edge indices are to items 1 &amp; 2
 *                                         0.1 100    &lt;--- with the following weights
 *                                         2 2        &lt;--- items 2 also has 2 edges
 *                                         1 2        &lt;--- edge indices are also to items 1 &amp; 2 (fully connected)
 *                                         100 0.1    &lt;--- with the following weights
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

   /**
    * Read a sparse symmetric square matrix that is expressed as an adjacency list in a tab-delimited file:
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
    *           
    *            
    *                           item1 item2 weight
    *                           item1 item5 weight
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
    *   
    *  
    * </pre>
    * 
    * <p>
    * IMPORTANT: By definition the resulting matrix is square and symmetric, even if the symmetric edges are not
    * explicitly listed.
    * 
    * @param name of file
    * @return
    */
   public NamedMatrix readFromAdjList( String fileName ) throws IOException {
      if ( !FileTools.testFile( fileName ) ) {
         throw new IOException( "Could not read from file " + fileName );
      }
      FileInputStream stream = new FileInputStream( fileName );
      return readFromAdjList( stream );
   }

   /**
    * @throws IOException
    * @throws NumberFormatException Read a sparse symmetric square matrix that is expressed as an adjacency list in a
    *         tab-delimited file:
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
    *           
    *            
    *              item1 item2 weight
    *              item1 item5 weight
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
    *   
    *  
    * </pre>
    * 
    * <p>
    *         IMPORTANT: By definition the resulting matrix is square and symmetric, even if the symmetric edges are not
    *         explicitly listed.
    * @param stream
    * @return
    */
   public NamedMatrix readFromAdjList( InputStream stream )
         throws NumberFormatException, IOException {
      Set itemNames = new HashSet();
      Map rows = new HashMap();

      BufferedReader dis = new BufferedReader( new InputStreamReader( stream ) );

      OpenIntObjectHashMap indexNameMap = new OpenIntObjectHashMap(); // eventual row index --> name
      Map nameIndexMap = new HashMap(); // name --> eventual row index

      /*
       * Store the information about the matrix in a temporary set of data structures, the most important of which is a
       * map of nodes to edge information. Each edge information object contains the index and the weight of the edge.
       */
      String row;
      int index = 0;
      while ( ( row = dis.readLine() ) != null ) {
         StringTokenizer st = new StringTokenizer( row, " \t", false );

         String itemA = "";
         if ( st.hasMoreTokens() ) {
            itemA = st.nextToken();
            if ( !itemNames.contains( itemA ) ) {
               rows.put( itemA, new OpenIntDoubleHashMap() );
               itemNames.add( itemA );
               indexNameMap.put( index, itemA );
               nameIndexMap.put( itemA, new Integer( index ) );
               ( ( OpenIntDoubleHashMap ) rows.get( itemA ) ).put( index, 0 ); // to itself. - in case it isn't there.
               index++;
            }
         } else
            continue;

         String itemB = "";
         if ( st.hasMoreTokens() ) {
            itemB = st.nextToken();
            if ( !itemNames.contains( itemB ) ) {
               rows.put( itemB, new OpenIntDoubleHashMap() );
               itemNames.add( itemB );
               indexNameMap.put( index, itemB );
               nameIndexMap.put( itemB, new Integer( index ) );
               ( ( OpenIntDoubleHashMap ) rows.get( itemB ) ).put( index, 0 ); // to itself. - in case it isn't there.
               index++;
            }
         } else
            continue;

         double weight;
         if ( st.hasMoreTokens() ) {
            weight = Double.parseDouble( st.nextToken() );
         } else {
            weight = 1.0; // just make it a binary matrix.
         }

         int aind = ( ( Integer ) nameIndexMap.get( itemA ) ).intValue();
         int bind = ( ( Integer ) nameIndexMap.get( itemB ) ).intValue();

     //    if (itemA.equals("CYP4A11") || itemB.equals("CYP4A11")) 
   //      System.err.println( itemA + " " + itemB + " " + aind + " " + bind );

         ( ( OpenIntDoubleHashMap ) rows.get( itemA ) ).put( bind, weight ); // link a to b.
         ( ( OpenIntDoubleHashMap ) rows.get( itemB ) ).put( aind, weight ); // link b to a.
         
         if ( ( rows.size() % 500 ) == 0 ) {
            log.info( new String( "loading  " + index + "th pair" ) );
         }
      }
      dis.close();

      SparseRaggedDoubleMatrix2DNamed matrix = new SparseRaggedDoubleMatrix2DNamed();

      for ( int i = 0; i < indexNameMap.size(); i++ ) {
         String itemName = ( String ) indexNameMap.get( i );

         OpenIntDoubleHashMap arow = ( OpenIntDoubleHashMap ) rows
               .get( itemName );

         DoubleArrayList finalValues = new DoubleArrayList( arow.size() );

    //     System.err.println( itemName + " has " + arow.size() + " links" );
         IntArrayList inB = arow.keys();
         inB.sort();
         int[] rowMemberIndexes = inB.elements();
       //  System.err.println( itemName + " " + i + " " + inB );
         
         for ( int j = 0; j < rowMemberIndexes.length; j++ ) {
            int itemNumber = rowMemberIndexes[j]; // keys
            double weight = arow.get( itemNumber );
            finalValues.add( weight );
         }

         DoubleMatrix1D rowMatrix = new RCDoubleMatrix1D( inB, finalValues );
         matrix.addRow( itemName, rowMatrix );

         if ( i > 0 && ( i % 500 ) == 0 ) {
            log.info( new String( "Adding  " + i + "th row" ) );
         }
      }
      return matrix;
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.io.reader.AbstractNamedMatrixReader#read(java.lang.String)
    */
   public NamedMatrix read( String fileName ) throws IOException {
      if ( !FileTools.testFile( fileName ) ) {
         throw new IOException( "Could not read from file " + fileName );
      }
      FileInputStream stream = new FileInputStream( fileName );
      return read( stream );
   }

   public NamedMatrix readOneRow( BufferedReader dis ) throws IOException {
      return this.readOneRow( dis, 0 );
   }

   /**
    * Use this to read one row from a matrix (JW format). It does not close the reader. (this actually has to read
    * several lines to get the data for one matrix row)
    * 
    * @param stream
    * @param offset A value indicating the lowest value for the indexes listed. This is here in case the indexes in the
    *        stream are numbered starting from 1 instead of zero.
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
    * Read an entire sparse matrix from a stream (JW format).
    * 
    * @param stream
    * @return @throws IOException
    */
   public NamedMatrix read( InputStream stream ) throws IOException {
      return this.read( stream, 0 );
   }

   /**
    * Read an entire sparse matrix from a stream (JW format).
    * 
    * @param stream
    * @param offset A value indicating the lowest value for the indexes listed. This is here in case the indexes in the
    *        stream are numbered starting from 1 instead of zero.
    * @return @throws IOException
    */
   public NamedMatrix read( InputStream stream, int offset ) throws IOException {
      BufferedReader dis = new BufferedReader( new InputStreamReader( stream ) );
      SparseRaggedDoubleMatrix2DNamed returnVal = new SparseRaggedDoubleMatrix2DNamed();

      String row;
      int k = 1;

      while ( ( row = dis.readLine() ) != null ) {

         if ( row.equals( "" ) ) { // in case there is a blank line at the top.
            continue;
         }

         StringTokenizer tok = new StringTokenizer( row, " \t" );
         if ( tok.countTokens() != 2 ) { // in case the row count is there.
            continue;
         }

         int index = Integer.parseInt( tok.nextToken() ) - offset;
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

      /*
       * we have to be careful to skip any lines that invalid. Each line should have at least two characters. In the
       * files JW provided there are some lines that are just " ".
       */
      String rowInd = "";
      String rowWei = "";

      //     while ( rowInd.length() < 2 ) {
      rowInd = dis.readLine(); // row with indices.
      //    }

      //    while ( rowWei.length() < 2 ) {
      rowWei = dis.readLine(); // row with weights.
      //    }

      StringTokenizer tokw = new StringTokenizer( rowWei, " \t" );
      StringTokenizer toki = new StringTokenizer( rowInd, " \t" );

      OpenIntIntHashMap map = new OpenIntIntHashMap( amount, 0.4, 0.8 );
      DoubleArrayList values = new DoubleArrayList( amount );
      DoubleArrayList finalValues = new DoubleArrayList( amount );

      int i = 0;
      while ( toki.hasMoreTokens() ) {

         double weight = Double.parseDouble( tokw.nextToken() );
         int ind = Integer.parseInt( toki.nextToken() ) - offset;

         if ( ind < 0 ) {
            throw new IllegalStateException(
                  "Can't have negative index - check offset." );
         }

         map.put( ind, i );
         values.add( weight );
         i++;
      }

      IntArrayList indexes = map.keys();
      indexes.sort();
      int[] ix = indexes.elements();
      int size = ix.length;
      for ( int j = 0; j < size; j++ ) {
         finalValues.add( values.get( map.get( ix[j] ) ) );
      }

      return new RCDoubleMatrix1D( indexes, finalValues );
   }

}