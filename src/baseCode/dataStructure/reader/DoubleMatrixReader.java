package baseCode.dataStructure.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.NamedMatrix;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */
public class DoubleMatrixReader
    extends MatrixReader {

   /**
    * @param   filename     data file to read from
    * @return  NamedMatrix  object constructed from the data file
    */
   public NamedMatrix read(String filename) throws IOException {
      DenseDoubleMatrix2DNamed matrix = null;
      Vector MTemp = new Vector();
      Vector colNames;
      Vector rowNames = new Vector();

      BufferedReader dis = new BufferedReader(new FileReader(filename));
      int columnNumber = 0;
      int rowNumber = 0;
      String row;

      colNames = readHeader(dis);
      int numHeadings = colNames.size();

      while ( (row = dis.readLine()) != null) {
         StringTokenizer st = new StringTokenizer(row, "\t", true);
         Vector rowTemp = new Vector();
         columnNumber = 0;
         String previousToken = "";

         while (st.hasMoreTokens()) {
            String s = st.nextToken();

            boolean missing = false;

            if (s.compareTo("\t") == 0) {
               /* two tabs in a row */
               if (previousToken.compareTo("\t") == 0) {
                  missing = true;
               } else if (!st.hasMoreTokens()) { // at end of line.
                  missing = true;
               } else {
                  previousToken = s;
                  continue;
               }
            } else if (s.compareTo(" ") == 0) {
               if (previousToken.compareTo("\t") == 0) {
                  missing = true;
               } else {
                  throw new IOException("Spaces not allowed after values");
                  // bad, not allowed.
               }
            } else if (s.compareToIgnoreCase("NaN") == 0) {
               if (previousToken.compareTo("\t") == 0) {
                  missing = true;
               } else {
                  throw new IOException(
                      "NaN found where it isn't supposed to be");
                  // bad, not allowed - missing a tab?
               }
            }

            if (columnNumber > 0) {
               if (missing) {
                  rowTemp.add(Double.toString(Double.NaN));
               } else {
                  rowTemp.add(s);
               }
            } else {
               if (missing) {
                  throw new IOException(
                      "Missing values not allowed for row labels");
               } else {
                  rowNames.add(s);
               }
            }

            columnNumber++;
            previousToken = s;
         }
         MTemp.add(rowTemp);
         if (rowTemp.size() > numHeadings) {
            throw new IOException("Too many values (" + rowTemp.size() +
                                  ") in row " +
                                  rowNumber + " (based on headings count of " +
                                  numHeadings +
                                  ")");
         }
         rowNumber++;
      }

      matrix = new DenseDoubleMatrix2DNamed(rowNumber, numHeadings);
      matrix.setRowNames(rowNames);
      matrix.setColumnNames(colNames);

      for (int i = 0; i < matrix.rows(); i++) {
         for (int j = 0; j < matrix.columns(); j++) {
            if ( ( (Vector) MTemp.get(i)).size() < j + 1) {
               matrix.set(i, j, Double.NaN); // this allows the input file to have ragged ends.
            } else {
               matrix.set(i, j,
                          Double.parseDouble( (String) ( (Vector) MTemp.
                   get(i)).get(j)));
            }
         }
      }
      return matrix;
   } // end read

   /**
    * I realize that having two full implementations of read() is reduntant.
    * But I am hesitant to merge them into one because I am not sure my
    * modified read() -- this method -- is not without bugs.
    *
    * When sufficiently tested, the old read() implementation should be
    * replaced by the following one-liner:
    *
    *    public NamedMatrix read( String filename ) throws IOException {
    *       read( filename, null );
    *    }
    *
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    * @param   filename        data file to read from
    * @param   filterRowNames  contains names or rows we want to get
    * @return  NamedMatrix     object constructed from the data file
    */
   public NamedMatrix read(String filename, String[] filterRowNames) throws IOException {
      DenseDoubleMatrix2DNamed matrix = null;
      Vector MTemp = new Vector();
      Vector colNames;
      Vector rowNames = new Vector();

      BufferedReader dis = new BufferedReader(new FileReader(filename));
      int columnNumber = 0;
      int rowNumber = 0;
      String row;

      colNames = readHeader(dis);
      int numHeadings = colNames.size();

      while ( (row = dis.readLine()) != null) {
         StringTokenizer st = new StringTokenizer(row, "\t", true);

         //
         // Skip rows in which we are not interested
         //
         String s = null;
         if (st.hasMoreTokens()) {
            s = st.nextToken();
         } else {
            continue;
         }
         if (filterRowNames != null && skipThisRow(s, filterRowNames)) {
            continue;
         }

         //
         // If we're here, we don't want to skip this row, so parse it
         //
         Vector rowTemp = new Vector();
         columnNumber = 0;
         String previousToken = "";

         while (st.hasMoreTokens()) { // Iterate through the row, parsing it into row name and values
            if (columnNumber > 0) {
               // if columnNumber == 0, then we just entered this loop and
               // have already called st.nextToken() above to get the first
               // string, so we don't want to call it twice, or we'll skip
               // the row name
               s = st.nextToken();
            }
            boolean missing = false;

            if (s.compareTo("\t") == 0) {
               /* two tabs in a row */
               if (previousToken.compareTo("\t") == 0) {
                  missing = true;
               } else if (!st.hasMoreTokens()) { // at end of line.
                  missing = true;
               } else {
                  previousToken = s;
                  continue;
               }
            } else if (s.compareTo(" ") == 0) {
               if (previousToken.compareTo("\t") == 0) {
                  missing = true;
               } else {
                  throw new IOException("Spaces not allowed after values");
                  // bad, not allowed.
               }
            } else if (s.compareToIgnoreCase("NaN") == 0) {
               if (previousToken.compareTo("\t") == 0) {
                  missing = true;
               } else {
                  throw new IOException(
                      "NaN found where it isn't supposed to be");
                  // bad, not allowed - missing a tab?
               }
            }

            if (columnNumber > 0) {
               if (missing) {
                  rowTemp.add(Double.toString(Double.NaN));
               } else {
                  rowTemp.add(s);
               }
            } else {
               if (missing) {
                  throw new IOException(
                      "Missing values not allowed for row labels");
               } else {
                  rowNames.add(s);
               }
            }

            columnNumber++;
            previousToken = s;
         } // end while (st.hasMoreTokens())
         // done parsing one row -- no more tokens

         MTemp.add(rowTemp);
         if (rowTemp.size() > numHeadings) {
            throw new IOException("Too many values (" + rowTemp.size() +
                                  ") in row " +
                                  rowNumber + " (based on headings count of " +
                                  numHeadings +
                                  ")");
         }
         rowNumber++;
      }

      matrix = new DenseDoubleMatrix2DNamed(rowNumber, numHeadings);
      matrix.setRowNames(rowNames);
      matrix.setColumnNames(colNames);

      for (int i = 0; i < matrix.rows(); i++) {
         for (int j = 0; j < matrix.columns(); j++) {
            if ( ( (Vector) MTemp.get(i)).size() < j + 1) {
               matrix.set(i, j, Double.NaN); // this allows the input file to have ragged ends.
            } else {
               matrix.set(i, j,
                          Double.parseDouble( (String) ( (Vector) MTemp.
                   get(i)).get(j)));
            }
         }
      }
      return matrix;
   } // end read

   /**
    * Returns true if and only if thisRowName is found in filterRowNames array
    */
   public boolean skipThisRow(String thisRowName, String[] filterRowNames) {

      boolean found = false;
      for (int i = 0; i < filterRowNames.length; i++) {
         if (thisRowName.equals(filterRowNames[i])) {
            return false; // found it!  so don't skip this row!
         }
      }
      return true; // didn't find it :(  skip this row.

   } // end skipThisRow

} // end class DoubleMatrixReader
