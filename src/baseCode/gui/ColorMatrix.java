package baseCode.gui;

import java.awt.Color;
import java.io.IOException;

import baseCode.dataStructure.matrix.AbstractNamedDoubleMatrix;
import baseCode.dataStructure.matrix.DenseDoubleMatrix2DNamed;
import baseCode.io.reader.DoubleMatrixReader;
import baseCode.math.DescriptiveWithMissing;
import baseCode.math.MatrixStats;
import cern.colt.list.DoubleArrayList;

/**
 * <p>
 * Title: ColorMatrix
 * </p>
 * <p>
 * Description: Creates a color matrix from a matrix of doubles
 * </p>
 * <p>
 * Copyright (c) 2004
 * </p>
 * <p>
 * Institution:: Columbia University
 * </p>
 * 
 * @author Will Braynen
 * @version $Id$
 */
public class ColorMatrix implements Cloneable {

   // data fields

   /**
    * Min and max values to display, which might not be the actual min and max values in the matrix. For instance, we
    * might want to clip values, or show a bigger color range for equal comparison with other rows or matrices.
    */
   protected double m_displayMin;
   protected double m_displayMax;

   protected double m_min;
   protected double m_max;

   protected Color[][] m_colors;
   protected Color m_missingColor = Color.lightGray;
   protected Color[] m_colorMap = ColorMap.BLACKBODY_COLORMAP;

   protected AbstractNamedDoubleMatrix m_matrix;
   protected DoubleMatrixReader m_matrixReader;

   protected int m_totalRows, m_totalColumns;

   /** to be able to sort the rows by an arbitrary key */
   protected int m_rowKeys[];

   /**
    * @param filename either an absolute path, or if providing a relative path (e.g. data.txt), then keep in mind that
    *        it will be relative to the java interpreter, not the class (not my fault -- that's how java treats relative
    *        paths)
    * @throws IOException
    */
   public ColorMatrix( String filename ) throws IOException {
      loadMatrixFromFile( filename );
   }

   public ColorMatrix( DenseDoubleMatrix2DNamed matrix ) {
      init( matrix );
   }

   /**
    * @param filename data filename
    * @param colorMap the simplest color map is one with just two colors: { minColor, maxColor }
    * @param missingColor values missing from the matrix or non-numeric entries will be displayed using this color
    * @throws IOException
    */
   public ColorMatrix( String filename, Color[] colorMap, Color missingColor )
         throws IOException {

      m_missingColor = missingColor;
      m_colorMap = colorMap;
      loadMatrixFromFile( filename );
   }

   /**
    * @param matrix the matrix
    * @param colorMap the simplest color map is one with just two colors: { minColor, maxColor }
    * @param missingColor values missing from the matrix or non-numeric entries will be displayed using this color
    */
   public ColorMatrix( AbstractNamedDoubleMatrix matrix, Color[] colorMap,
         Color missingColor ) {

      m_missingColor = missingColor;
      m_colorMap = colorMap;
      init( matrix );
   }

   public int getRowCount() {
      return m_totalRows;
   }

   public int getColumnCount() {
      return m_totalColumns;
   }

   protected int getTrueRowIndex( int row ) {
      return m_rowKeys[row];
   }

   public double getValue( int row, int column )
         throws ArrayIndexOutOfBoundsException {

      if ( row >= m_totalRows )
            throw new ArrayIndexOutOfBoundsException( "The matrix has only "
                  + m_totalRows
                  + " rows, so the maximum possible row index is "
                  + ( m_totalRows - 1 ) + ". Row index " + row
                  + " is too high." );
      if ( column >= m_totalColumns )
            throw new ArrayIndexOutOfBoundsException( "The matrix has only "
                  + m_totalColumns
                  + " columns, so the maximum possible column index is "
                  + ( m_totalColumns - 1 ) + ". Column index " + column
                  + " is too high." );

      row = getTrueRowIndex( row );
      return m_matrix.get( row, column );
   }

   public double[] getRow( int row ) throws ArrayIndexOutOfBoundsException {

      if ( row >= m_totalRows )
            throw new ArrayIndexOutOfBoundsException( "The matrix has only "
                  + m_totalRows
                  + " rows, so the maximum possible row index is "
                  + ( m_totalRows - 1 ) + ". Row index " + row
                  + " is too high." );

      row = getTrueRowIndex( row );
      return m_matrix.getRow( row );
   }

   public double[] getRowByName( String rowName ) {
      return m_matrix.getRowByName( rowName );
   }

   public Color getColor( int row, int column )
         throws ArrayIndexOutOfBoundsException {

      if ( row >= m_totalRows )
            throw new ArrayIndexOutOfBoundsException( "The matrix has only "
                  + m_totalRows
                  + " rows, so the maximum possible row index is "
                  + ( m_totalRows - 1 ) + ". Row index " + row
                  + " is too high." );
      if ( column >= m_totalColumns )
            throw new ArrayIndexOutOfBoundsException( "The matrix has only "
                  + m_totalColumns
                  + " columns, so the maximum possible column index is "
                  + ( m_totalColumns - 1 ) + ". Column index " + column
                  + " is too high." );

      row = getTrueRowIndex( row );
      return m_colors[row][column];
   }

   public void setColor( int row, int column, Color newColor )
         throws ArrayIndexOutOfBoundsException {

      if ( row >= m_totalRows )
            throw new ArrayIndexOutOfBoundsException( "The matrix has only "
                  + m_totalRows
                  + " rows, so the maximum possible row index is "
                  + ( m_totalRows - 1 ) + ". Row index " + row
                  + " is too high." );
      if ( column >= m_totalColumns )
            throw new ArrayIndexOutOfBoundsException( "The matrix has only "
                  + m_totalColumns
                  + " columns, so the maximum possible column index is "
                  + ( m_totalColumns - 1 ) + ". Column index " + column
                  + " is too high." );

      row = getTrueRowIndex( row );
      m_colors[row][column] = newColor;
   }

   public String getRowName( int row ) throws ArrayIndexOutOfBoundsException {

      if ( row >= m_totalRows )
            throw new ArrayIndexOutOfBoundsException( "The matrix has only "
                  + m_totalRows
                  + " rows, so the maximum possible row index is "
                  + ( m_totalRows - 1 ) + ". Row index " + row
                  + " is too high." );

      row = getTrueRowIndex( row );
      return m_matrix.getRowName( row );
   }

   public String getColumnName( int column )
         throws ArrayIndexOutOfBoundsException {

      if ( column >= m_totalColumns )
            throw new ArrayIndexOutOfBoundsException( "The matrix has only "
                  + m_totalColumns
                  + " columns, so the maximum possible column index is "
                  + ( m_totalColumns - 1 ) + ". Column index " + column
                  + " is too high." );

      return m_matrix.getColName( column );
   }

   public String[] getRowNames() {
      String[] rowNames = new String[m_totalRows];
      for ( int i = 0; i < m_totalRows; i++ ) {
         int row = getTrueRowIndex( i );
         rowNames[i] = getRowName( row );
      }
      return rowNames;
   }

   public String[] getColumnNames() {
      String[] columnNames = new String[m_totalColumns];
      for ( int i = 0; i < m_totalColumns; i++ ) {
         columnNames[i] = getColumnName( i );
      }
      return columnNames;
   }

   public int getRowIndexByName( String rowName ) {
      return m_matrix.getRowIndexByName( rowName );
   }

   /**
    * Changes values in a row, clipping if there are more values than columns.
    * 
    * @param row row whose values we want to change
    * @param values new row values
    */
   protected void setRow( int row, double values[] ) {

      row = getTrueRowIndex( row );

      // clip if we have more values than columns
      int totalValues = Math.min( values.length, m_totalColumns );

      for ( int column = 0; column < totalValues; column++ ) {
         m_matrix.set( row, column, values[column] );

      }
   } // end setRow

   /**
    * To be able to sort the rows by an arbitrary key. Creates <code>m_rowKeys</code> array and initializes it in
    * ascending order from 0 to <code>m_totalRows</code> -1, so that by default it matches the physical order of the
    * columns: [0,1,2,...,m_totalRows-1]
    * 
    * @return int[]
    */
   protected int[] createRowKeys() {
      m_rowKeys = new int[m_totalRows];
      for ( int i = 0; i < m_totalRows; i++ ) {
         m_rowKeys[i] = i;
      }
      return m_rowKeys;
   }

   public void setRowKeys( int[] rowKeys ) {
      m_rowKeys = rowKeys;
   }

   public void resetRowKeys() {
      for ( int i = 0; i < m_totalRows; i++ ) {
         m_rowKeys[i] = i;
      }
   }

   public void init( AbstractNamedDoubleMatrix matrix ) {

      m_matrix = matrix; // by reference, or should we clone?
      m_totalRows = m_matrix.rows();
      m_totalColumns = m_matrix.columns();
      m_colors = new Color[m_totalRows][m_totalColumns];
      createRowKeys();

      m_displayMin = m_min = MatrixStats.min( m_matrix );
      m_displayMax = m_max = MatrixStats.max( m_matrix );

      // map values to colors
      mapValuesToColors();
   }

   /**
    * A convenience method for loading data files
    * 
    * @param filename the name of the data file
    * @throws IOException
    */
   public void loadMatrixFromFile( String filename ) throws IOException {

      m_matrixReader = new DoubleMatrixReader();
      DenseDoubleMatrix2DNamed matrix = ( DenseDoubleMatrix2DNamed ) m_matrixReader
            .read( filename );
      init( matrix );
   }

   /**
    * Normalizes the elements of a matrix to variance one and mean zero, ignoring missing values todo move this to
    * matrixstats or something.
    */
   public void standardize() {

      // normalize the data in each row
      for ( int r = 0; r < m_totalRows; r++ ) {
         double[] rowValues = getRow( r );
         DoubleArrayList doubleArrayList = new cern.colt.list.DoubleArrayList(
               rowValues );
         DescriptiveWithMissing.standardize( doubleArrayList );
         rowValues = doubleArrayList.elements();
         setRow( r, rowValues );
      }

      m_displayMin = -2;
      m_displayMax = +2;

      mapValuesToColors();

   } // end standardize

   /**
    * @return a DenseDoubleMatrix2DNamed object
    */
   public AbstractNamedDoubleMatrix getMatrix() {
      return m_matrix;
   }

   //
   // Standardized display range
   //
   public void setDisplayRange( double min, double max ) {

      m_displayMin = min;
      m_displayMax = max;

      mapValuesToColors();
   }

   public double getDisplayMin() {
      return m_displayMin;
   }

   public double getDisplayMax() {
      return m_displayMax;
   }

   /**
    * @param colorMap an array of colors which define the midpoints in the color map; this can be one of the constants
    *        defined in the ColorMap class, like ColorMap.REDGREEN_COLORMAP and ColorMap.BLACKBODY_COLORMAP
    * @throws IllegalArgumentException if the colorMap array argument contains less than two colors.
    */
   public void setColorMap( Color[] colorMap ) throws IllegalArgumentException {

      if ( colorMap.length < 2 ) {
         throw new IllegalArgumentException();
      }

      m_colorMap = colorMap;
      mapValuesToColors();
   }

   public void mapValuesToColors() {
      ColorMap colorMap = new ColorMap( m_colorMap );
      double range = m_displayMax - m_displayMin;

      if ( 0.0 == range ) {
         // This is not an exception, just a warning, so no exception to throw
         System.err.println( "Warning: range of values in data is zero." );
         range = 1.0; // This avoids getting a step size of zero
         // in case all values in the matrix are equal.
      }

      // zoom factor for stretching or shrinking the range
      double zoomFactor = ( colorMap.getPaletteSize() - 1 ) / range;

      // map values to colors
      for ( int row = 0; row < m_totalRows; row++ ) {
         for ( int column = 0; column < m_totalColumns; column++ ) {
            double value = getValue( row, column );

            if ( Double.isNaN( value ) ) {
               // the value is missing
               m_colors[row][column] = m_missingColor;
            } else {
               // clip extreme values (this makes sense for normalized
               // values because then for a standard deviation of one and
               // mean zero, we set m_minValue = -2 and m_maxValue = 2,
               // even though there could be a few extreme values
               // outside this range (right?)
               if ( value > m_displayMax ) {
                  // clip extremely large values
                  value = m_displayMax;
               } else if ( value < m_displayMin ) {
                  // clip extremely small values
                  value = m_displayMin;
               }

               // shift the minimum value to zero
               // to the range [0, maxValue + minValue]
               value -= m_displayMin;

               // stretch or shrink the range to [0, totalColors]
               double valueNew = value * zoomFactor;
               int i = ( int ) Math.round( valueNew );
               m_colors[row][column] = colorMap.getColor( i );
            }
         }
      }
   } // end mapValuesToColors

   public Object clone() {
      // create another double matrix
      DenseDoubleMatrix2DNamed matrix = new DenseDoubleMatrix2DNamed(
            m_totalRows, m_totalColumns );
      // copy the row and column names
      for ( int i = 0; i < m_totalRows; i++ ) {
         matrix.addRowName( m_matrix.getRowName( i ), i );
      }
      for ( int i = 0; i < m_totalColumns; i++ ) {
         matrix.addColumnName( m_matrix.getColName( i ), i );
         // copy the data
      }
      for ( int r = 0; r < m_totalRows; r++ ) {
         for ( int c = 0; c < m_totalColumns; c++ ) {
            matrix.set( r, c, m_matrix.get( r, c ) );
         }
      }

      // create another copy of a color matrix (this class)
      ColorMatrix clonedColorMatrix = new ColorMatrix( matrix, m_colorMap,
            m_missingColor );

      int[] rowKeys = ( int[] ) m_rowKeys.clone();
      clonedColorMatrix.setRowKeys( rowKeys );

      return clonedColorMatrix;

   } // end clone

} // end class ColorMatrix
