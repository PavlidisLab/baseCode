/*
 * MicroarraySet.java
 *
 * Created on May 27, 2004, 9:59 PM
 */

package baseCode.Gui;

import java.awt.Color;
import baseCode.dataStructure.reader.DoubleMatrixReader;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.Math.MatrixStats;
import cern.colt.list.DoubleArrayList;

/**
 *
 * @author  Will
 */
public class ColorMatrix {

    // data fields
    Color[][] m_colors;
    Color[] m_colorPalette;
    final int m_suggestedNumberOfColors = 64;

    DenseDoubleMatrix2DNamed m_matrix;
    DoubleMatrixReader m_matrixReader;

    double m_minValue, m_maxValue;
    int m_totalRows, m_totalColumns;

    /** to be able to sort the rows by an arbitrary key */
    int m_rowKeys[];

    // colors and color maps
    Color m_missingColor = Color.lightGray;
    final Color DARK_RED = new Color( 128, 0, 0 );
    Color m_minColor = Color.green; // default
    Color m_maxColor = Color.red;   // default
    Color[] m_customColorMap    = { m_minColor, m_maxColor };
    final Color[] GREENRED_COLORMAP  = { Color.green, Color.black, Color.red   };
    final Color[] REDGREEN_COLORMAP  = { Color.red,   Color.black, Color.green };
    final Color[] BLACKBODY_COLORMAP = { Color.black, DARK_RED, Color.orange, Color.yellow, Color.white };
    Color[] m_currentColorMap = GREENRED_COLORMAP; // reference to a color map

    /**
     * @param  filename  either an absolute path, or if providing a relative
     *                   path (e.g. data.txt), then keep in mind that it will
     *                   be relative to the java interpreter, not the class
     *                   (not my fault -- that's how java treats relative paths)
     */
    public ColorMatrix( String filename ) {

        DenseDoubleMatrix2DNamed matrix = loadFile( filename );
        loadMatrix( matrix );
    }

    public ColorMatrix( DenseDoubleMatrix2DNamed matrix ) {

        loadMatrix( matrix );
    }

    /**
     * Calculate how fast we have to change color components.
     * Assume min and max colors are different!
     *
     * @param  minColor  red, green, or blue component of the RGB color
     * @param  maxColor  red, green, or blue component of the RGB color
     * @return  positive or negative step size
     */
    int getStepSize( int minColor, int maxColor, int totalColors ) {

        int colorRange = maxColor - minColor;
        double stepSize = colorRange / ( 1 == totalColors ? 1 : totalColors - 1 );
        return (int)Math.round( stepSize );
    }

    /**
     * Allocates colors across a range.
     *
     * @param suggestedNumberOfColors  palette resolution; if colorPalette.length
     *        does not evenly divide into this number, the actual number of
     *        colors in the palette will be rounded down.
     * @param colorMap  the simplest color map is { minColor, maxColor };
     *                  you might, however, want to go through intermediate
     *                  colors instead of following a straight-line route
     *                  through the color space.
     * @return Color[]  the color palette
     */
    Color[] createColorPalette( int suggestedNumberOfColors, Color[] colorMap ) {

        Color[] colorPalette;
        Color minColor;
        Color maxColor;

        // number of segments is one less than the number of points
        // dividing the line into segments;  the color map contains points,
        // not segments, so for example, if the color map is trivially
        // { minColor, maxColor }, then there is only one segment
        int totalSegments = m_currentColorMap.length - 1;

        // allocate colors across a range; distribute evenly
        // between intermediate points, if there are any
        int colorsPerSegment = suggestedNumberOfColors / totalSegments;

        // make sure all segments are equal by rounding down
        // the total number of colors if necessary
        int totalColors = totalSegments * colorsPerSegment;

        // create color map to return
        colorPalette = new Color[totalColors];

        for (int segment = 0;  segment < totalSegments;  segment++)
        {
           // the minimum color for each segment as defined by the current color map
           minColor = colorMap[segment];
           int r = minColor.getRed();
           int g = minColor.getGreen();
           int b = minColor.getBlue();

           // the maximum color for each segment and the step sizes
           maxColor = colorMap[segment + 1];
           int redStepSize   = getStepSize( r, maxColor.getRed(),   colorsPerSegment );
           int greenStepSize = getStepSize( g, maxColor.getGreen(), colorsPerSegment );
           int blueStepSize  = getStepSize( b, maxColor.getBlue(),  colorsPerSegment );

           for (int k, i=0;  i < colorsPerSegment;  i++)
           {
               // clip
               r = Math.min( r, 255 );
               g = Math.min( g, 255 );
               b = Math.min( b, 255 );

               // but also make sure it's not less than zero
               r = Math.max( r, 0 );
               g = Math.max( g, 0 );
               b = Math.max( b, 0 );

               k = segment * colorsPerSegment + i;
               colorPalette[k] = new Color( r, g, b );

               r += redStepSize;
               g += greenStepSize;
               b += blueStepSize;
           }
        }

        return colorPalette;

    } // end createColorPalette


    public void mapValuesToColors()
    {
        m_colorPalette = createColorPalette( m_suggestedNumberOfColors, m_currentColorMap );
        double range = m_maxValue - m_minValue;
        if (0.0 == range)
        {
            System.err.println( "Warning: range of values in data is zero." );
            range = 1.0; // This avoids getting a step size of zero
                         // in case all values in the matrix are equal.
        }

        // zoom factor for stretching or shrinking the range
        double zoomFactor = (m_colorPalette.length - 1) / range;

        // map values to colors
        for (int row = 0;  row < m_totalRows;  row++)
        {
            for (int column = 0;  column < m_totalColumns;  column++)
            {
                double value = m_matrix.get( row, column );

                if (Double.isNaN (value))
                {
                    // the value is missing
                    m_colors[row][column] = m_missingColor;
                }
                else
                {
                    // clip extreme values (this makes sense for normalized
                    // values because then for a standard deviation of one and
                    // mean zero, we set m_minValue = -2 and m_maxValue = 2,
                    // even though there could be a few extreme values
                    // outside this range (right?)
                    if (value > m_maxValue)
                    {
                        // clip extremely large values
                        value = m_maxValue;
                    }
                    else if (value < m_minValue)
                    {
                        // clip extremely small values
                        value = 0;
                    }
                    else
                    {
                        // shift the minimum value to zero
                        // to the range [0, maxValue + minValue]
                        value -= m_minValue;
                    }

                    // stretch or shrink the range to [0, totalColors]
                    double valueNew = value * zoomFactor;
                    int i = (int)valueNew;
                    m_colors[row][column] = m_colorPalette[i];
                }
            }
        }
    } // end mapValuesToColors


    public int getRowCount() {

        return m_totalRows;
    }

    public int getColumnCount() {

        return m_totalColumns;
    }

    public void setRowKeys( int[] rowKeys ) {

        m_rowKeys = rowKeys;
    }

    public Color getColor( int row, int column ) {

        return getColor( row, column, true );
    }

    /**
     * @see  #setRowKeys
     */
    public Color getColor( int row, int column, boolean isRowKey ) {

        if (isRowKey)
        {
            row = m_rowKeys[row];
            return m_colors[row][column];
        }
        else
        {
            return m_colors[row][column];
        }
    } // end getColor

    public String getRowName( int i ) {

        return m_matrix.getRowName( i );
    }

    /**
     * Changes values in a row, clipping if there are more values than columns.
     *
     * @param  row     row whose values we want to change
     * @param  values  new row values
     */
    protected void setRow( int row, double values[] ) {

        // clip if we have more values than columns
        int totalValues = Math.min( values.length, m_totalColumns );

        for (int column = 0;  column < totalValues;  column++)
            m_matrix.set( row, column, values[column] );

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

        for (int i = 0;  i < m_totalRows;  i++)
            m_rowKeys[i] = i;

        return m_rowKeys;
    }

    public void loadMatrix( DenseDoubleMatrix2DNamed matrix ) {

        m_matrix = matrix; // by reference, or should we clone?
        m_totalRows = m_matrix.rows();
        m_totalColumns = m_matrix.columns();
        m_colors = new Color[m_totalRows][m_totalColumns];
        createRowKeys();

        m_minValue = MatrixStats.min(m_matrix);
        m_maxValue = MatrixStats.max(m_matrix);

        // map values to colors
        mapValuesToColors();
    }

    public DenseDoubleMatrix2DNamed loadFile( String filename ) {

        m_matrixReader = new DoubleMatrixReader();
        DenseDoubleMatrix2DNamed matrix = (DenseDoubleMatrix2DNamed) m_matrixReader.read( filename );
        return matrix;
    }

    /**
     * Normalizes the elements of an array to variance one and mean zero,
     * ignoring missing values
     */
    public void standardize() {

       // normalize the data in each row
       for (int r = 0; r < m_totalRows; r++)
       {
          double[] rowValues = m_matrix.getRow(r);
          DoubleArrayList doubleArrayList = new cern.colt.list.DoubleArrayList( rowValues );
          doubleArrayList = baseCode.Math.Stats.standardize(doubleArrayList);
          rowValues = doubleArrayList.elements();
          setRow(r, rowValues);
       }

       // we normalized to variance one and mean zero,
       // so no value should fall outside the range [-2,2]
       m_minValue = -2;
       m_maxValue = 2;

       mapValuesToColors();

    } // end standardize

}
