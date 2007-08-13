/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;
import ubic.basecode.gui.graphics.text.Util;

/**
 * A visual component for displaying a color matrix
 * 
 * @author Will Braynen
 * @version $Id$
 */
public class JMatrixDisplay extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = -8078532270193813539L;

    private Log log = LogFactory.getLog( JMatrixDisplay.class );

    // data fields
    ColorMatrix m_matrix; // reference to standardized or unstandardized matrix
    ColorMatrix m_unstandardizedMatrix;
    ColorMatrix m_standardizedMatrix;
    boolean m_isShowingStandardizedMatrix = false;

    protected boolean m_isShowLabels = false;
    protected BufferedImage m_image = null;

    protected int m_ratioWidth = 0;
    protected int m_rowLabelWidth; // max
    protected int m_columnLabelHeight; // max
    protected int m_labelGutter = 5;
    protected int m_fontGutter;
    protected Font m_labelFont = null;
    protected int m_fontSize = 10;
    protected final int m_maxFontSize = 10;
    protected final int m_defaultResolution = 120;
    protected int m_resolution = m_defaultResolution;
    protected int m_textSize = 0;
    protected int m_maxColumnLength = 0;

    protected Dimension m_cellSize = new Dimension( 10, 10 ); // in pixels

    public JMatrixDisplay( String filename ) throws IOException {

        ColorMatrix matrix = new ColorMatrix( filename );
        init( matrix );
    }

    public JMatrixDisplay( DoubleMatrixNamed matrix ) {
        this( new ColorMatrix( matrix ) );
    }

    public JMatrixDisplay( ColorMatrix matrix ) {
        init( matrix );
    }

    public void init( ColorMatrix matrix ) {

        m_unstandardizedMatrix = m_matrix = matrix;
        initSize();

        // create a standardized copy of the matrix
        m_standardizedMatrix = ( ColorMatrix ) matrix.clone();
        m_standardizedMatrix.standardize();
    }

    /**
     * Sets the display size
     */
    protected void initSize() {

        Dimension d = getSize( m_isShowLabels );
        setMinimumSize( d );
        setPreferredSize( d );
        setSize( d );
        this.revalidate();
    }

    protected Dimension getSize( boolean withLabels ) {

        if ( m_matrix == null ) {
            return null;
        }

        // row label width and height (font-dependent)
        setFont();
        m_rowLabelWidth = m_labelGutter + Util.maxStringPixelWidth( m_matrix.getRowNames(), m_labelFont, this );
        m_rowLabelWidth += m_labelGutter; // this is optional (leaves some  space on the right)
        if (m_maxColumnLength > 0) {
            String [] cols = m_matrix.getColumnNames();
            for (int i = 0; i < cols.length; i++ ) {
                cols[i] = padColumnString( cols[i] );
                
            }
            // fix column height to ~5 pixels per character. This prevents a slightly different column height
            // for different letters.
            m_columnLabelHeight = 5 * m_maxColumnLength;
        }
        else {
            m_columnLabelHeight = Util.maxStringPixelWidth( m_matrix.getColumnNames(), m_labelFont, this );         
        }

        // m_columnLabelHeight += m_labelGutter; // this is optional (leaves some
        // space on top)

        // height and width of this display component
        int height = m_cellSize.height * m_matrix.getRowCount();
        int width = m_cellSize.width * m_matrix.getColumnCount();

        // adjust for row and column labels
        if ( withLabels ) {
            width += m_rowLabelWidth;
            height += ( m_columnLabelHeight + m_labelGutter );
        }

        // set the sizes
        return new Dimension( width, height );

    } // end getSize

    /**
     * <code>JComponent</code> method used to render this component
     * 
     * @param g Graphics used for painting
     */
    protected void paintComponent( Graphics g ) {

        super.paintComponent( g );
        drawMatrix( g, m_isShowLabels );

        if ( m_isShowLabels ) {
            drawRowNames( g );
            drawColumnNames( g );
        }
    } // end paintComponent

    public void setStandardizedEnabled( boolean showStandardizedMatrix ) {
        m_isShowingStandardizedMatrix = showStandardizedMatrix;
        if ( showStandardizedMatrix ) {
            m_matrix = m_standardizedMatrix;
        } else {
            m_matrix = m_unstandardizedMatrix;
        }
    } // end setStandardizedEnabled

    public boolean getStandardizedEnabled() {

        return m_isShowingStandardizedMatrix;
    }

    /**
     * Gets called from #paintComponent and #saveImage
     * 
     * @param g Graphics
     * @param leaveRoomForLabels boolean
     */
    protected void drawMatrix( Graphics g, boolean leaveRoomForLabels ) {

        g.setColor( Color.white );
        g.fillRect( 0, 0, this.getWidth(), this.getHeight() );

        int rowCount = m_matrix.getRowCount();
        int columnCount = m_matrix.getColumnCount();

        // loop through the matrix, one row at a time
        for ( int i = 0; i < rowCount; i++ ) {
            int y = ( i * m_cellSize.height );
            if ( leaveRoomForLabels ) {
                y += ( m_columnLabelHeight + m_labelGutter );
            }

            // draw an entire row, one cell at a time
            for ( int j = 0; j < columnCount; j++ ) {
                int x = ( j * m_cellSize.width );
                int width = ( ( j + 1 ) * m_cellSize.width ) - x;

                Color color = m_matrix.getColor( i, j );
                g.setColor( color );
                g.fillRect( x, y, width, m_cellSize.height );
            }

        } // end looping through the matrix, one row at a time
    } // end drawMatrix

    /**
     * Draws row names (horizontally)
     * 
     * @param g Graphics
     */
    protected void drawRowNames( Graphics g ) {

        if ( m_matrix == null ) return;

        int rowCount = m_matrix.getRowCount();

        // draw row names
        for ( int i = 0; i < rowCount; i++ ) {
            g.setColor( Color.black );
            g.setFont( m_labelFont );
            int y = ( i * m_cellSize.height ) + m_columnLabelHeight + m_labelGutter;
            int xRatio = ( m_matrix.getColumnCount() * m_cellSize.width ) + m_labelGutter;
            int yRatio = y + m_cellSize.height - m_fontGutter;
            Object rowName = m_matrix.getRowName( i );
            if ( null == rowName ) {
                rowName = "Undefined";
            }

            g.drawString( rowName.toString(), xRatio, yRatio );

        } // end drawing row names
    } // end rawRowName

    /**
     * Draws column names vertically (turned 90 degrees counter-clockwise)
     * 
     * @param g Graphics
     */
    protected void drawColumnNames( Graphics g ) {

        if ( m_matrix == null ) return;

        int columnCount = m_matrix.getColumnCount();
        for ( int j = 0; j < columnCount; j++ ) {
            // compute the coordinates
            int x = m_cellSize.width + ( j * m_cellSize.width ) - m_fontGutter;
            int y = m_columnLabelHeight;

            // get column name
            Object columnName = m_matrix.getColumnName( j );
            if ( null == columnName ) {
                columnName = "Undefined";
            }
            
            // fix the name length as 20 characters
            // add ellipses if > 20
            // spacepad to 20 if < 20
            String columnNameString = columnName.toString();
            if (m_maxColumnLength > 0) {
                columnNameString = padColumnString( columnNameString );
            }
            // set font and color
            g.setColor( Color.black );
            g.setFont( m_labelFont );

            // print the text vertically
            Util.drawVerticalString( g, columnNameString, m_labelFont, x, y );

        } // end for column
    } // end drawColumnNames

    /**
     * Pads a string to the maxColumnLength. If it is over the maxColumnLength, it abbreviates it to the maxColumnLength
     * @param str
     * @return
     */
    private String padColumnString( String str ) {
        str = StringUtils.abbreviate( str, m_maxColumnLength );
        str = StringUtils.rightPad( str, m_maxColumnLength, " " );
        return str;
    }

    /**
     * Sets the font used for drawing text
     */
    private void setFont() {
        int fontSize = Math.min( getFontSize(),
                ( int ) ( ( double ) m_maxFontSize / ( double ) m_defaultResolution * m_resolution ) );
        if ( ( fontSize != m_fontSize ) || ( m_labelFont == null ) ) {
            m_fontSize = fontSize;
            m_labelFont = new Font( "Ariel", Font.PLAIN, m_fontSize );
            m_fontGutter = ( int ) ( m_cellSize.height * .22 );
        }
    }

    /**
     * @return the height of the font
     */
    private int getFontSize() {
        return Math.max( m_cellSize.height, 5 );
    }

    /**
     * Saves the image to a png file.
     * 
     * @param outPngFilename String
     * @throws IOException
     */
    public void saveImage( String outPngFilename ) throws java.io.IOException {
        saveImage( outPngFilename, m_isShowLabels, m_isShowingStandardizedMatrix );
    }

    public void saveImage( String outPngFilename, boolean showLabels ) throws java.io.IOException {
        saveImage( outPngFilename, showLabels, m_isShowingStandardizedMatrix );
    }

    /**
     * @param outPngFilename String
     * @param showLabels boolean
     * @param standardize normalize to deviation 1, mean 0.
     * @todo never read
     * @throws IOException
     */
    public void saveImage( String outPngFilename, boolean showLabels, boolean standardize ) throws java.io.IOException {

        Graphics2D g = null;

        // Include row and column labels?
        boolean wereLabelsShown = m_isShowLabels;
        if ( !wereLabelsShown ) {
            // Labels aren't visible, so make them visible
            setLabelsVisible( true );
            initSize();
        }

        // Draw the image to a buffer
        Dimension d = getSize( showLabels ); // how big is the image with row and
        // column labels
        m_image = new BufferedImage( d.width, d.height, BufferedImage.TYPE_INT_RGB );
        g = m_image.createGraphics();
        drawMatrix( g, showLabels );
        if ( showLabels ) {
            drawRowNames( g );
            drawColumnNames( g );
        }

        // Write the image to a png file
        ImageIO.write( m_image, "png", new File( outPngFilename ) );

        // Restore state: make the image as it was before
        if ( !wereLabelsShown ) {
            // Labels weren't visible to begin with, so hide them
            setLabelsVisible( false );
            initSize();
        }
    } // end saveImage

    /**
     * @param stream
     * @param showLabels
     * @param standardize
     */
    public void writeOutAsPNG( OutputStream stream, boolean showLabels, boolean standardize ) {
        // TODO move me
        Graphics2D g = null;

        // Include row and column labels?
        boolean wereLabelsShown = m_isShowLabels;
        if ( !wereLabelsShown ) {
            // Labels aren't visible, so make them visible
            setLabelsVisible( true );
            initSize();
        }

        // Draw the image to a buffer
        Dimension d = getSize( showLabels ); // how big is the image with row and
        // column labels
        m_image = new BufferedImage( d.width, d.height, BufferedImage.TYPE_INT_RGB );
        g = m_image.createGraphics();
        drawMatrix( g, showLabels );
        if ( showLabels ) {
            // drawRowNames( g );
            drawColumnNames( g );
        }

        // Write the buffered image to the output steam.
        try {
            ImageIO.write( m_image, "png", stream );
        } catch ( IOException e ) {
            log.error( "Error writing image to output stream.  Stacktrace is: " + e.toString() );
        }

        // Restore state: make the image as it was before
        if ( !wereLabelsShown ) {
            // Labels weren't visible to begin with, so hide them
            setLabelsVisible( false );
            initSize();
        }
    } // end saveImage

    /**
     * If this display component has already been added to the GUI, it will be resized to fit or exclude the row names
     * 
     * @param isShowLabels boolean
     */
    public void setLabelsVisible( boolean isShowLabels ) {
        m_isShowLabels = isShowLabels;
        initSize();
    }

    public ColorMatrix getColorMatrix() {
        return m_matrix;
    }

    public DoubleMatrixNamed getMatrix() {
        return m_matrix.m_matrix;
    }

    /**
     * @param matrix the new matrix to use; will resize this display component as necessary
     */
    public void setMatrix( ColorMatrix matrix ) {
        m_matrix = matrix;
        initSize();
    }

    public void setCellSize( Dimension d ) {

        m_cellSize = d;
        initSize();
    }

    public void setRowHeight( int height ) {

        m_cellSize.height = height;
        initSize();
    }

    public int getRowHeight() {
        return m_cellSize.height;
    }

    public Color getColor( int row, int column ) {
        return m_matrix.getColor( row, column );
    } // end getColor

    public double getValue( int row, int column ) {
        return m_matrix.getValue( row, column );
    } // end getValue

    public double[] getRow( int row ) {
        return m_matrix.getRow( row );
    }

    public double[] getRowByName( String rowName ) {
        return m_matrix.getRowByName( rowName );
    }

    public int getRowCount() {
        return m_matrix.getRowCount();
    }

    public int getColumnCount() {
        return m_matrix.getColumnCount();
    }

    public Object getColumnName( int column ) {
        return m_matrix.getColumnName( column );
    }

    public Object getRowName( int row ) {
        return m_matrix.getRowName( row );
    }

    public String[] getColumnNames() {
        return m_matrix.getColumnNames();
    }

    public String[] getRowNames() {
        return m_matrix.getRowNames();
    }

    public int getRowIndexByName( String rowName ) {
        return m_matrix.getRowIndexByName( rowName );
    }

    public void setRowKeys( int[] rowKeys ) {
        m_matrix.setRowKeys( rowKeys );
    }

    public void resetRowKeys() {
        m_matrix.resetRowKeys();
    }

    /**
     * @param colorMap an array of colors which define the midpoints in the color map; this can be one of the constants
     *        defined in the ColorMap class, like ColorMap.REDGREEN_COLORMAP and ColorMap.BLACKBODY_COLORMAP
     */
    public void setColorMap( Color[] colorMap ) {

        m_standardizedMatrix.setColorMap( colorMap );
        m_unstandardizedMatrix.setColorMap( colorMap );
    }

    /**
     * @return the current color map
     */
    public Color[] getColorMap() {
        return m_matrix.m_colorMap;
    }

    /**
     * @return the smallest value in the matrix
     */
    public double getMin() {
        return m_matrix.m_min;
    }

    /**
     * @return the largest value in the matrix
     */
    public double getMax() {
        return m_matrix.m_max;
    }

    public double getDisplayMin() {
        return m_matrix.m_displayMin;
    }

    public double getDisplayMax() {
        return m_matrix.m_displayMax;
    }

    public double getDisplayRange() {
        return m_matrix.m_displayMax - m_matrix.m_displayMin;
    }

    public void setDisplayRange( double min, double max ) {
        m_matrix.setDisplayRange( min, max );
    }

    /**
     * @return the color used for missing values
     */
    public Color getMissingColor() {
        return m_matrix.m_missingColor;
    }
    
    /**
     * @return the m_maxColumnLength
     */
    public int getMaxColumnLength() {
        return m_maxColumnLength;
    }

    /**
     * @param columnLength the m_maxColumnLength to set
     */
    public void setMaxColumnLength( int columnLength ) {
        m_maxColumnLength = columnLength;
    }

} // end class JMatrixDisplay

