package baseCode.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

// vertical text
import baseCode.graphics.text.Util;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;

/**
 * <p>Title: JMatrixDisplay</p>
 * <p>Description: a visual component for displaying a color matrix</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Will Braynen
 * @version $Id$
 */
public class JMatrixDisplay extends JPanel {

    // data fields
    ColorMatrix m_matrix;
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
    protected final int m_defaultResolution  = 120;
    protected int m_resolution = m_defaultResolution;
    protected int m_textSize = 0;

    protected Dimension m_cellSize = new Dimension( 10, 10 ); // in pixels

    public JMatrixDisplay( String filename ) throws IOException {

	ColorMatrix matrix = new ColorMatrix( filename );
	init( matrix );
    }

    public JMatrixDisplay( DenseDoubleMatrix2DNamed matrix ) {

	this( new ColorMatrix( matrix ));
    }

    public JMatrixDisplay( ColorMatrix matrix ) {

	init( matrix );
    }

    public void init( ColorMatrix matrix ) {

	m_matrix = matrix;
	initSize();

	// create a standardized copy of the matrix
	m_standardizedMatrix = (ColorMatrix) matrix.clone();
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

	if (m_matrix == null)
	    {
		return null;
	    }

	// row label width and height (font-dependent)
	setFont();
	m_rowLabelWidth = m_labelGutter + Util.maxStringPixelWidth( m_matrix.getRowNames(), m_labelFont, this );
	//m_rowLabelWidth += m_labelGutter; // this is optional (leaves some space on the right)
	m_columnLabelHeight = Util.maxStringPixelWidth( m_matrix.getColumnNames(), m_labelFont, this );
	//m_columnLabelHeight += m_labelGutter; // this is optional (leaves some space on top)

	// height and width of this display component
	int height = m_cellSize.height * m_matrix.getRowCount();
	int width  = m_cellSize.width  * m_matrix.getColumnCount();

	// adjust for row and column labels
	if (withLabels)
	    {
		width  += m_rowLabelWidth;
		height += (m_columnLabelHeight + m_labelGutter);
	    }

	// set the sizes
	return new Dimension( width, height );

    } // end getSize

    /**
     * <code>JComponent</code> method used to render this component
     * @param g Graphics used for painting
     */
    protected void paintComponent( Graphics g ) {

	super.paintComponent(g);
	drawMatrix( g, m_isShowLabels );

	if (m_isShowLabels)
	    {
		drawRowNames( g );
		drawColumnNames( g );
	    }
    } // end paintComponent

    public void setStandardizedEnabled( boolean showStandardizedMatrix )
    {
	m_isShowingStandardizedMatrix = showStandardizedMatrix;
    }

    public boolean getStandardizedEnabled() {

	return m_isShowingStandardizedMatrix;
    }

    /**
     * Gets called from #paintComponent and #saveToFile
     */
    protected void drawMatrix( Graphics g, boolean leaveRoomForLabels ) {

	// which matrix do we draw?  standardized or not?
	ColorMatrix matrix = m_isShowingStandardizedMatrix ? m_standardizedMatrix : m_matrix;

	g.setColor(Color.white);
	g.fillRect(0, 0, this.getWidth(), this.getHeight());

	int rowCount = matrix.getRowCount();
	int columnCount = matrix.getColumnCount();

	// loop through the matrix, one row at a time
	for (int i = 0;  i < rowCount;  i++)
	    {
		int y = (i * m_cellSize.height);
		if (leaveRoomForLabels)
		    {
			y += (m_columnLabelHeight + m_labelGutter);
		    }

		// draw an entire row, one cell at a time
		for (int j = 0; j < columnCount; j++)
		    {
			int x = (j * m_cellSize.width);
			int width = ( (j + 1) * m_cellSize.width) - x;

			Color color = matrix.getColor(i, j); // BLAH!!! change to matrix
			g.setColor(color);
			g.fillRect(x, y, width, m_cellSize.height);
		    }

	    } // end looping through the matrix, one row at a time
    } // end drawMatrix

    /**
     * Draws row names (horizontally)
     */
    protected void drawRowNames( Graphics g ) {

	if (m_matrix == null) return;

	int rowCount = m_matrix.getRowCount();

	// draw row names
	for (int i = 0;  i < rowCount;  i++)
	    {
		g.setColor( Color.black );
		g.setFont( m_labelFont );
		int y = (i * m_cellSize.height) + m_columnLabelHeight + m_labelGutter;
		int xRatio = (m_matrix.getColumnCount() * m_cellSize.width) + m_labelGutter;
		int yRatio = y + m_cellSize.height - m_fontGutter;
		String rowName = m_matrix.getRowName( i );
		if (null == rowName) {
		    rowName = "Undefined";
		}
		g.drawString(rowName, xRatio, yRatio);
	    } // end drawing row names
    } // end rawRowName

    /**
     * Draws column names vertically (turned 90 degrees counter-clockwise)
     */
    protected void drawColumnNames( Graphics g ) {

	if (m_matrix == null) return;

	int columnCount = m_matrix.getColumnCount();
	for (int j = 0;  j < columnCount;  j++)
	    {
		// compute the coordinates
		int x = m_cellSize.width + (j * m_cellSize.width) - m_fontGutter;
		int y = m_columnLabelHeight;

		// get column name
		String columnName = m_matrix.getColumnName( j );
		if (null == columnName) {
		    columnName = "Undefined";
		}

		// set font and color
		g.setColor( Color.black );
		g.setFont( m_labelFont );

		// print the text vertically
		Util.drawVerticalString( g, columnName, m_labelFont, x, y );

	    } // end for column
    } // end drawColumnNames




    /**
     * Sets the font used for drawing text
     */
    private void setFont() {
     int fontSize =
         Math.min(getFontSize(),
		  (int)( (double) m_maxFontSize /
			 (double) m_defaultResolution * (double) m_resolution) );
     if((fontSize != m_fontSize) || (m_labelFont == null))
	 {
	     m_fontSize  = fontSize;
	     m_labelFont = new Font("Ariel", Font.PLAIN, m_fontSize);
	     m_fontGutter = (int) ( (double) m_cellSize.height * .22);
	 }
    }

    /**
     * @return  the height of the font
     */
    private int getFontSize() {
	return Math.max( m_cellSize.height, 5 );
    }



    /**
     * Saves the image to a png file. The image file will <i>always</i> include
     * row and column names!
     */
    public void saveToFile( String outPngFilename ) throws java.io.IOException {

	saveToFile( outPngFilename, true );
    }

    /**
     * Saves the image to a png file.
     */
    public void saveToFile( String outPngFilename, boolean showLabels ) throws java.io.IOException {

	Graphics2D g = null;

	boolean wereLabelsShown = m_isShowLabels;
	if ( ! wereLabelsShown )
	    {
		// labels aren't visible, so make them visible
		setLabelsVisible( true );
		initSize();
	    }

	// draw the image to a buffer
	Dimension d = getSize( showLabels ); // how big is the image with row and column labels
	m_image = new BufferedImage( d.width, d.height, BufferedImage.TYPE_INT_RGB );
	g = m_image.createGraphics();
	drawMatrix( g, showLabels );
	if (showLabels)
	    {
		drawRowNames( g );
		drawColumnNames( g );
	    }

	// write the image to a png file
	ImageIO.write( m_image, "png", new File( outPngFilename ));

	if ( ! wereLabelsShown )
	    {
		// labels weren't visible to begin with, so hide them
		setLabelsVisible( false );
		initSize();
	    }
    } // end saveToFile

    /**
     * If this display component has already been added to the GUI,
     * it will be resized to fit or exclude the row names
     */
    public void setLabelsVisible( boolean isShowLabels ) {
	m_isShowLabels = isShowLabels;
	initSize();
    }

    public ColorMatrix getMatrix() {
	return m_matrix;
    }

    /**
     * @param  matrix  the new matrix to use;  will resize
     *                 this display component as necessary
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

	if (m_isShowingStandardizedMatrix)
	    return m_standardizedMatrix.getColor( row, column );
	else
	    return m_matrix.getColor( row, column );
    } // end getColor

    public double getValue( int row, int column ) {

	if (m_isShowingStandardizedMatrix)
	    return m_standardizedMatrix.getValue( row, column );
	else
	    return m_matrix.getValue( row, column );
    } // end getValue

    public int getRowCount() {
	return m_matrix.getRowCount();
    }

    public int getColumnCount() {
	return m_matrix.getColumnCount();
    }

    public String getColumnName( int column ) {
	return m_matrix.getColumnName( column );
    }

    public String getRowName( int row ) {
	return m_matrix.getRowName( row );
    }

    /**
     * @param  colorMap  an array of colors which define the midpoints in the
     *                   color map; this can be one of the constants defined
     *                   in the ColorMap class, like ColorMap.REDGREEN_COLORMAP
     *                   and ColorMap.BLACKBODY_COLORMAP
     *
     * @throws an exception if the colorMap array argument contains less than
     *                      two colors.
     */
    public void setColorMap( Color[] colorMap ) throws Exception {

       m_standardizedMatrix.setColorMap( colorMap );
       m_matrix.setColorMap( colorMap );
    }

} // end class JMatrixDisplay
