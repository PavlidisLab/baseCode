package baseCode.dataStructure.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import cern.colt.list.DoubleArrayList;

import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.NamedMatrix;

/**
 * 
 * Reader for {@link baseCode.dataStructure.DenseDoubleMatrix2DNamed}.
 * <p>
 * Copyright (c) 2004
 * </p>
 * <p>
 * Institution: Columbia University
 * </p>
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class DoubleMatrixReader extends AbstractNamedMatrixReader {

   /**
    * @param filename data file to read from
    * @return NamedMatrix object constructed from the data file
    */
   public NamedMatrix read( String filename ) throws IOException {
      return read( filename, null );
   }

   /**
    * @param stream InputStream stream to read from
    * @return NamedMatrix object constructed from the data file
    */
   public NamedMatrix read( InputStream stream ) throws IOException {
      return read( stream, null );
   }

   /**
    * @return <code>read( stream, wantedRowNames, createEmptyRows )</code> with
    *         <code>createEmptyRows</code> set to true.
    */
   public NamedMatrix read( InputStream stream, Set wantedRowNames )
         throws IOException {
            return read( stream, wantedRowNames, true );
   }
   
   /**
    * @param stream
    * @param wantedRowNames
    * @param createEmptyRows if a row contained in <code>wantedRowNames</code>
    *                        is not found in the file, create an empty row
    *                        filled with Double.NaN iff this param is true. 
    * @return @throws IOException
    */
   public NamedMatrix read( InputStream stream, Set wantedRowNames, boolean createEmptyRows )
         throws IOException {

      BufferedReader dis = new BufferedReader( new InputStreamReader( stream ) );

      Vector MTemp = new Vector();
      Vector colNames;
      Vector rowNames = new Vector();

      //BufferedReader dis = new BufferedReader( new FileReader( filename ) );
      int columnNumber = 0;
      int rowNumber = 0;
      String row;

      //
      // We need to keep track of which row names we actually found in the file
      // because will want to add empty rows for each row name we didn't find
      // (if createEmptyRows == true).
      //
      Set wantedRowsFound = null;
      if ( wantedRowNames != null && createEmptyRows ) {
         wantedRowsFound = new HashSet();
      }
      
      colNames = readHeader( dis );
      int numHeadings = colNames.size();

      while ( ( row = dis.readLine() ) != null ) {
         StringTokenizer st = new StringTokenizer( row, "\t", true );

         //
         // Skip rows in which we are not interested
         //
         String s = null;
         if ( st.hasMoreTokens() ) {
            s = st.nextToken();
         } else {
            continue;
         }
         if ( wantedRowNames != null ) {

            // if we already have all the rows we want, then bail out
            if ( rowNumber >= wantedRowNames.size() ) {
               return createMatrix( MTemp, rowNumber, numHeadings, rowNames,
                     colNames );
            }
            // skip this row if it's not in wantedRowNames
            else if ( !wantedRowNames.contains( s ) ) {
               // maybe the next line in the file has the row we want
               continue;
            }
            else if ( createEmptyRows ) {
               // we found the row we want in the file
               wantedRowsFound.add( s );
            }
         }
         
         //
         // If we're here, that means this row in the file contains the row
         // we want -- we don't want to skip this file row, so parse it
         //
         DoubleArrayList rowTemp = new DoubleArrayList();
         columnNumber = 0;
         String previousToken = "";

         while ( st.hasMoreTokens() ) {
            // Iterate through the row, parsing it into row name and values
            if ( columnNumber > 0 ) {
               // if columnNumber == 0, then we just entered this loop and
               // have already called st.nextToken() above to get the first
               // string, so we don't want to call it twice, or we'll skip
               // the row name
               s = st.nextToken();
            }
            boolean missing = false;

            if ( s.compareTo( "\t" ) == 0 ) {
               /* two tabs in a row */
               if ( previousToken.compareTo( "\t" ) == 0 ) {
                  missing = true;
               } else if ( !st.hasMoreTokens() ) { // at end of line.
                  missing = true;
               } else {
                  previousToken = s;
                  continue;
               }
            } else if ( s.compareTo( " " ) == 0 ) {
               if ( previousToken.compareTo( "\t" ) == 0 ) {
                  missing = true;
               } else {
                  throw new IOException( "Spaces not allowed after values" );
                  // bad, not allowed.
               }
            } else if ( s.compareToIgnoreCase( "NaN" ) == 0 ) {
               if ( previousToken.compareTo( "\t" ) == 0 ) {
                  missing = true;
               } else {
                  throw new IOException(
                        "NaN found where it isn't supposed to be" );
                  // bad, not allowed - missing a tab?
               }
            }

            if ( columnNumber > 0 ) {
               if ( missing ) {
                  rowTemp.add( Double.NaN   );
               } else {
                  rowTemp.add( Double.parseDouble(s) );
               }
            } else {
               if ( missing ) {
                  throw new IOException(
                        "Missing values not allowed for row labels" );
               }
               rowNames.add( s );
            }

            columnNumber++;
            previousToken = s;
         } // end while (st.hasMoreTokens())
         // done parsing one row -- no more tokens

         MTemp.add( rowTemp );
         if ( rowTemp.size() > numHeadings ) {
            throw new IOException( "Too many values (" + rowTemp.size()
                  + ") in row " + rowNumber + " (based on headings count of "
                  + numHeadings + ")" );
         }
         rowNumber++;
      }
      stream.close();
      
      //
      // Add empty rows for each row name we didn't find in the file
      //
      if ( wantedRowNames != null && createEmptyRows ) {
         Iterator iterator = wantedRowNames.iterator();
         while ( iterator.hasNext() ) {
            String s = ( String ) iterator.next();
            if ( ! wantedRowsFound.contains( s ) ) {
               // add an empty row
               DoubleArrayList emptyRow = createEmptyRow( numHeadings );
               rowNames.add( s );
               MTemp.add( emptyRow );
               rowNumber++;
            }
         }
      }
      
      return createMatrix( MTemp, rowNumber, numHeadings, 
      rowNames, colNames );

   }

   /**
    * Read a matrix from a file, subject to filtering criteria.
    * 
    * @param filename data file to read from
    * @param wantedRowNames contains names of rows we want to get
    * @return NamedMatrix object constructed from the data file
    */
   public NamedMatrix read( String filename, Set wantedRowNames )
         throws IOException {
      File infile = new File( filename );
      if ( !infile.exists() || !infile.canRead() ) {
         throw new IllegalArgumentException( "Could not read from " + filename );
      }
      FileInputStream stream = new FileInputStream( infile );
      return read( stream, wantedRowNames );
   } // end read

   protected DenseDoubleMatrix2DNamed createMatrix( Vector MTemp, int rowCount,
         int colCount, Vector rowNames, Vector colNames ) {

      DenseDoubleMatrix2DNamed matrix = new DenseDoubleMatrix2DNamed( rowCount,
            colCount );
     
      for ( int i = 0; i < matrix.rows(); i++ ) {
         for ( int j = 0; j < matrix.columns(); j++ ) {
            if ( (( DoubleArrayList ) MTemp.get( i )).size() < j + 1 ) {
               matrix.set( i, j, Double.NaN );
               // this allows the input file to have ragged ends.
               // todo I'm not sure this is a good idea -PP
            } else {
               matrix.set( i, j, (( DoubleArrayList ) MTemp.get( i )).elements()[j]);
            }
         }
      }
      matrix.setRowNames( rowNames );
      matrix.setColumnNames( colNames );
      return matrix;

   } // end createMatrix
   
   protected DoubleArrayList createEmptyRow( int numColumns ) {
    
      DoubleArrayList row = new DoubleArrayList();
      for ( int i = 0;  i < numColumns;  i++ ) {
         row.add( Double.NaN );
      }
      return row;
   }

} // end class DoubleMatrixReader
