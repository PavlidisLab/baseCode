/*
 * MicroarraySet.java
 *
 * Created on May 27, 2004, 9:59 PM
 */

package baseCode.Gui;

import java.awt.Color;
import baseCode.dataStructure.reader.DoubleMatrixReader;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import java.net.URL;

/**
 *
 * @author  Will
 */
public class ColorMatrix {

    // data fields
    Color[][] m_colors;
    Color[] m_colorMap;
    final int m_totalColors = 64;
    
    DenseDoubleMatrix2DNamed m_matrix;
    DoubleMatrixReader m_matrixReader;
       
    double m_minValue, m_maxValue;
    int m_totalRows, m_totalColumns;

    String[] blackbody = {"black", "darkred", "orange", "yellow", "white"};
    String[] spectrum  = {"darkred", "red", "orange", "yellow", "green", "blue", "darkblue", "violet"};
    String[] redgreen  = {"red", "darkred", "black", "darkgreen", "green"};
    String[] greenred  = {"green", "darkgreen", "black", "darkred", "red"};

    
    public ColorMatrix() {
        
        this( "C:\\1_normalized.txt" );
    }
    
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
     */
    double getStepSize( int minColor, int maxColor, int totalColors ) {
        
        int colorRange = maxColor - minColor;
        return ((double)( colorRange ) / ( 1 == totalColors ? 1 : totalColors - 1 ));
        // could be negative
    } 
    
    
    Color[] initColorMap() {
        
        final Color minColor = Color.green;
        final Color maxColor = Color.red;
        Color[] colorMap = new Color[m_totalColors];

        int r = minColor.getRed();
        int g = minColor.getGreen();
        int b = minColor.getBlue();
        
        double redStepSize   = getStepSize( r, maxColor.getRed(),   m_totalColors );
        double greenStepSize = getStepSize( g, maxColor.getGreen(), m_totalColors );
        double blueStepSize  = getStepSize( b, maxColor.getBlue(),  m_totalColors );        
        
        
        // allocate colors across a range
        for (int i = 0;  i < m_totalColors;  i++)
        {
            // clip
            r = Math.min( r, 255 );
            g = Math.min( g, 255 );
            b = Math.min( b, 255 );
            
            // but also make sure it's not less than zero
            r = Math.max( r, 0 );
            g = Math.max( g, 0 );
            b = Math.max( b, 0 );            

            colorMap[i] = new Color( r, g, b );
            
            r += redStepSize;
            g += greenStepSize;
            b += blueStepSize;            
        }
        
        return colorMap;
        
    } // end initColorMap
    
    
    public void initColors() 
    {        
        m_colorMap = initColorMap();
        double range = m_maxValue - m_minValue;
        if (0.0 == range) 
        {
            System.err.println( "Warning: range of values in data is zero." );
            range = 1.0; // This avoids getting a step size of zero
                         // in case all values in the matrix are equal.
        }
        
        // map values to colors
        for (int row = 0;  row < m_totalRows;  row++)
        {
            for (int column = 0;  column < m_totalColumns;  column++)
            {
                double value = m_matrix.get( row, column );
                
                // if values can be less than zero, shift them up
                // to the range [0, maxValue + minValue]
                if (m_minValue < 0)
                {
                    value += Math.abs( m_minValue );
                }
                // normalize the values to the range [0, totalColors]
                int i = (int)(( m_totalColors / range ) * value );
                m_colors[row][column] = m_colorMap[i];
            }
        }        
    } // end initColors
    

    public int getRowCount() {

        return m_totalRows;
    }

    public int getColumnCount() {

        return m_totalColumns;
    }

    public Color getColor( int row, int column ) {

        return m_colors[row][column];
    }

    public String getRowName( int i ) {

        return m_matrix.getRowName( i );
    }
    
    public void loadMatrix( DenseDoubleMatrix2DNamed matrix ) { 

        m_matrix = matrix;
        m_totalRows = m_matrix.rows();
        m_totalColumns = m_matrix.columns();
        m_colors = new Color[m_totalRows][m_totalColumns];

        // compute min and max values in the matrix
        m_minValue = m_maxValue = m_matrix.get( 0, 0 );
        for (int row = 0;  row < m_totalRows;  row++)
        {
            for (int column = 0;  column < m_totalColumns;  column++)
            {
                double value = m_matrix.get( row, column );
                m_minValue = (m_minValue > value ? value : m_minValue);
                m_maxValue = (m_maxValue < value ? value : m_maxValue);
            }
        }

        initColors();        
    }
    
    public DenseDoubleMatrix2DNamed loadFile( String filename ) {
    
        m_matrixReader = new DoubleMatrixReader();
        DenseDoubleMatrix2DNamed matrix = (DenseDoubleMatrix2DNamed) m_matrixReader.read( filename );
        return matrix;
    }    
}
