package baseCode.gui;

import java.awt.Color;
import java.io.IOException;
import baseCode.dataStructure.reader.DoubleMatrixReader;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.Math.MatrixRowStats;
import cern.colt.list.DoubleArrayList;
import baseCode.Math.DescriptiveWithMissing;

/**
 * <p>Title: ColorMatrix</p>
 * <p>Description: Creates a color matrix from a matrix of doubles</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Will Braynen
 * @version $Id$
 */
public class ColorMatrix
    implements Cloneable {

   // data fields

   /**
    * min and max values to display, which might not be the actual min
    * and max values in the matrix.  For instance, we might want to clip
    * values, or show a bigger color range for equal comparison with other
    * rows or matrices.
    */
   public double m_minDisplayValue, m_maxDisplayValue;
   protected Color[][] m_colors;
   protected Color m_missingColor = Color.lightGray;
   protected Color[] m_colorMap = ColorMap.GREENRED_COLORMAP;

   protected DenseDoubleMatrix2DNamed m_matrix;
   protected DoubleMatrixReader m_matrixReader;

   protected int m_totalRows, m_totalColumns;

   /** to be able to sort the rows by an arbitrary key */
   protected int m_rowKeys[];

   /**
    * @param  filename  either an absolute path, or if providing a relative
    *                   path (e.g. data.txt), then keep in mind that it will
    *                   be relative to the java interpreter, not the class
    *                   (not my fault -- that's how java treats relative paths)
    */
   public ColorMatrix(String filename) throws IOException {
      loadMatrixFromFile(filename);
   }

   public ColorMatrix(DenseDoubleMatrix2DNamed matrix) {
      init(matrix);
   }

   /**
    * @param  filename      data filename
    * @param  colorMap      the simplest color map is one with just two colors: { minColor, maxColor }
    * @param  missingColor  values missing from the matrix or non-numeric entries will be displayed using this color
    */
   public ColorMatrix(String filename, Color[] colorMap, Color missingColor) throws IOException {

      m_missingColor = missingColor;
      m_colorMap = colorMap;
      loadMatrixFromFile(filename);
   }

   /**
    * @param  matrix        the matrix
    * @param  colorMap      the simplest color map is one with just two colors: { minColor, maxColor }
    * @param  missingColor  values missing from the matrix or non-numeric entries will be displayed using this color
    */
   public ColorMatrix(DenseDoubleMatrix2DNamed matrix, Color[] colorMap, Color missingColor) {

      m_missingColor = missingColor;
      m_colorMap = colorMap;
      init(matrix);
   }

   public int getRowCount() {
      return m_totalRows;
   }

   public int getColumnCount() {
      return m_totalColumns;
   }

   public void setRowKeys(int[] rowKeys) {
      m_rowKeys = rowKeys;
   }

   public double getValue(int row, int column) {
      return m_matrix.get(row, column);
   }

   public Color getColor(int row, int column) {
      return getColor(row, column, true);
   }

   /**
    * @see  #setRowKeys
    */
   public Color getColor(int row, int column, boolean isRowKey) {

      if (isRowKey) {
         row = m_rowKeys[row];
      }
      return m_colors[row][column];

   } // end getColor

   public void setColor(int row, int column, Color newColor) {
      setColor(row, column, true, newColor);
   }

   public void setColor(int row, int column, boolean isRowKey, Color newColor) {

      if (isRowKey) {
         row = m_rowKeys[row];
      }
      m_colors[row][column] = newColor;
   }

   public String getRowName(int row) {

      return m_matrix.getRowName(row);
   }

   public String getColumnName(int column) {

      return m_matrix.getColName(column);
   }

   public String[] getRowNames() {

      String[] rowNames = new String[m_totalRows];
      for (int i = 0; i < m_totalRows; i++) {
         rowNames[i] = getRowName(i);
      }
      return rowNames;
   }

   public String[] getColumnNames() {

      String[] columnNames = new String[m_totalColumns];
      for (int i = 0; i < m_totalColumns; i++) {
         columnNames[i] = getColumnName(i);
      }
      return columnNames;
   }

   /**
    * Changes values in a row, clipping if there are more values than columns.
    *
    * @param  row     row whose values we want to change
    * @param  values  new row values
    */
   protected void setRow(int row, double values[]) {

      // clip if we have more values than columns
      int totalValues = Math.min(values.length, m_totalColumns);

      for (int column = 0; column < totalValues; column++) {
         m_matrix.set(row, column, values[column]);

      }
   } // end setRow

   /**
    * To be able to sort the rows by an arbitrary key.
    * Creates <code>m_rowKeys</code> array and initializes it in
    * ascending order from 0 to <code>m_totalRows</code>-1,
    * so that by default it matches the physical order
    * of the columns: [0,1,2,...,m_totalRows-1]
    */
   protected int[] createRowKeys() {

      m_rowKeys = new int[m_totalRows];

      for (int i = 0; i < m_totalRows; i++) {
         m_rowKeys[i] = i;

      }
      return m_rowKeys;
   }

   public void init(DenseDoubleMatrix2DNamed matrix) {

      m_matrix = matrix; // by reference, or should we clone?
      m_totalRows = m_matrix.rows();
      m_totalColumns = m_matrix.columns();
      m_colors = new Color[m_totalRows][m_totalColumns];
      createRowKeys();

      m_minDisplayValue = MatrixRowStats.min(m_matrix);
      m_maxDisplayValue = MatrixRowStats.max(m_matrix);

      // map values to colors
      mapValuesToColors();
   }

   /**
    * A convenience method for loading data files
    * @param  filename  the name of the data file
    */
   public void loadMatrixFromFile(String filename) throws IOException {

      m_matrixReader = new DoubleMatrixReader();
      DenseDoubleMatrix2DNamed matrix = (DenseDoubleMatrix2DNamed) m_matrixReader.read(filename);
      init(matrix);
   }

   /**
    * Normalizes the elements of an array to variance one and mean zero,
    * ignoring missing values
    */
   public void standardize() {

      // normalize the data in each row
      for (int r = 0; r < m_totalRows; r++) {
         double[] rowValues = m_matrix.getRow(r);
         DoubleArrayList doubleArrayList = new cern.colt.list.DoubleArrayList(rowValues);
         DescriptiveWithMissing.standardize(doubleArrayList);
         rowValues = doubleArrayList.elements();
         setRow(r, rowValues);
      }

      // we normalized to variance one and mean zero,
      // so no value should fall outside the range [-2,2]
      m_minDisplayValue = -2;
      m_maxDisplayValue = 2;

      mapValuesToColors();

   } // end standardize

   /**
    * @return  a DenseDoubleMatrix2DNamed object
    */
   public DenseDoubleMatrix2DNamed getMatrix() {
      return m_matrix;
   }

   public void mapValuesToColors() {
      ColorMap colorMap = new ColorMap(m_colorMap);
      double range = m_maxDisplayValue - m_minDisplayValue;

      if (0.0 == range) {
         // This is not an exception, just a warning, so no exception to throw
         System.err.println("Warning: range of values in data is zero.");
         range = 1.0; // This avoids getting a step size of zero
         // in case all values in the matrix are equal.
      }

      // zoom factor for stretching or shrinking the range
      double zoomFactor = (colorMap.getPaletteSize() - 1) / range;

      // map values to colors
      for (int row = 0; row < m_totalRows; row++) {
         for (int column = 0; column < m_totalColumns; column++) {
            double value = m_matrix.get(row, column);

            if (Double.isNaN(value)) {
               // the value is missing
               m_colors[row][column] = m_missingColor;
            } else {
               // clip extreme values (this makes sense for normalized
               // values because then for a standard deviation of one and
               // mean zero, we set m_minValue = -2 and m_maxValue = 2,
               // even though there could be a few extreme values
               // outside this range (right?)
               if (value > m_maxDisplayValue) {
                  // clip extremely large values
                  value = m_maxDisplayValue;
               } else if (value < m_minDisplayValue) {
                  // clip extremely small values
                  value = 0;
               } else {
                  // shift the minimum value to zero
                  // to the range [0, maxValue + minValue]
                  value -= m_minDisplayValue;
               }

               // stretch or shrink the range to [0, totalColors]
               double valueNew = value * zoomFactor;
               int i = (int) valueNew;
               m_colors[row][column] = colorMap.getColor(i);
            }
         }
      }
   } // end mapValuesToColors

   public Object clone() {

      //      try {
      //
      //         cloneColorMatrix = (ColorMatrix) super.clone();
      //      }
      //      catch ()

      // create another double matrix
      Color[][] colors = new Color[m_totalRows][m_totalColumns];
      DenseDoubleMatrix2DNamed m = new DenseDoubleMatrix2DNamed(m_totalRows, m_totalColumns);
      // copy the row and column names
      for (int i = 0; i < m_totalRows; i++) {
         m.addRowName(m_matrix.getRowName(i));
      }
      for (int i = 0; i < m_totalColumns; i++) {
         m.addColumnName(m_matrix.getColName(i));
         // copy the data
      }
      for (int r = 0; r < m_totalRows; r++) {
         for (int c = 0; c < m_totalColumns; c++) {
            m.set(r, c, m_matrix.get(r, c));
            //colors[r][c] = new Color( m_colors[r][c].getRGB() );
         }
      }

      // create another copy of a color matrix (this class)
      ColorMatrix clonedColorMatrix = new ColorMatrix(m);
      return clonedColorMatrix;

   } // end clone

} // end class ColorMatrix
