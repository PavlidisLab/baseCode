package baseCode.dataStructure.reader;

import java.io.*;
import java.util.*;
import baseCode.dataStructure.*;

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
    * Requires filename as input
    * @param filename
    * @return
    */
   public NamedMatrix read(String filename) {
      DenseDoubleMatrix2DNamed matrix = null;
      Vector MTemp = new Vector();
      Vector colNames;
      Vector rowNames = new Vector();

      try {
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
                  }
                  else if (!st.hasMoreTokens()) { // at end of line.
                     missing = true;
                  }
                  else {
                     previousToken = s;
                     continue;
                  }
               }
               else if (s.compareTo(" ") == 0) {
                  if (previousToken.compareTo("\t") == 0) {
                     missing = true;
                  }
                  else {
                     System.err.println("Spaces not allowed after values");
                     // bad, not allowed.
                  }
               }
               else if (s.compareToIgnoreCase("NaN") == 0) {
                  if (previousToken.compareTo("\t") == 0) {
                     missing = true;
                  }
                  else {
                     System.err.println("NaN found where it isn't supposed to be");
                     // bad, not allowed - missing a tab?
                  }
               }

               if (columnNumber > 0) {
                  if (missing) {
                     rowTemp.add(Double.toString(Double.NaN));
                  }
                  else {
                     rowTemp.add(s);
                  }
               }
               else {
                  if (missing) {
                     System.err.println("Missing values not allowed for row labels");
                     // bad, no missing values allowed for row labels.
                  }
                  else {
                     rowNames.add(s);
                  }
               }

               columnNumber++;
               previousToken = s;
            }
            MTemp.add(rowTemp);
            if (rowTemp.size() > numHeadings) {
               System.err.println("Warning: too many values (" + rowTemp.size() + ") in row " +
                                  rowNumber + " (based on headings count of " + numHeadings +
                                  "); values will be ignored");
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
               }
               else {
                  matrix.set(i, j, Double.parseDouble( (String) ( (Vector) MTemp.get(i)).get(j)));
               }
            }
         }

      }
      catch (IOException e) {
         // catch possible io errors from readLine()
         System.out.println(" IOException error!");
         e.printStackTrace();
      }
      return matrix;
   }

   public static void main(String[] args) {
      MatrixReader m = new DoubleMatrixReader();
      NamedMatrix mm = m.read(args[0]);
      System.err.println(mm.toString());
   }
}