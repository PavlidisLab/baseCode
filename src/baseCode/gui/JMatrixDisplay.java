package baseCode.Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

// vertical text
import java.awt.geom.AffineTransform;
import java.awt.font.*;


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

  /** Cell height in pixels; if printing row names, minimum recommended height is 10 pixels */
  protected static int m_cellHeight = 10; // in pixels
  /** Cell width in pixels; if printing column names, minimum recommended width is 10 pixels */
  protected static int m_cellWidth = 10; // in pixels


  public JMatrixDisplay( ColorMatrix matrix ) {

     m_matrix = matrix;     
     initSize();
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
    m_rowLabelWidth = m_labelGutter + maxStringPixelWidth( m_matrix.getRowNames(), m_labelFont, this );
    //m_rowLabelWidth += m_labelGutter; // this is optional (leaves some space on the right)
    m_columnLabelHeight = maxStringPixelWidth( m_matrix.getColumnNames(), m_labelFont, this );
    //m_columnLabelHeight += m_labelGutter; // this is optional (leaves some space on top)

    // height and width of this display component
    int height = m_cellHeight * m_matrix.getRowCount();
    int width  = m_cellWidth  * m_matrix.getColumnCount();

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

  /**
   * Gets called from #paintComponent and #saveToFile
   */
  protected void drawMatrix( Graphics g, boolean leaveRoomForLabels ) {

     if (m_matrix == null) return;

     g.setColor(Color.white);
     g.fillRect(0, 0, this.getWidth(), this.getHeight());

     int rowCount = m_matrix.getRowCount();
     int columnCount = m_matrix.getColumnCount();

     // loop through the matrix, one row at a time
     for (int i = 0;  i < rowCount;  i++)
     {
        int y = (i * m_cellHeight);
        if (leaveRoomForLabels)
        {
           y += (m_columnLabelHeight + m_labelGutter);
        }

        // draw an entire row, one cell at a time
        for (int j = 0; j < columnCount; j++)
        {
           int x = (j * m_cellWidth);
           int width = ( (j + 1) * m_cellWidth) - x;

           Color color = m_matrix.getColor(i, j);
           g.setColor(color);
           g.fillRect(x, y, width, m_cellHeight);
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
         int y = (i * m_cellHeight) + m_columnLabelHeight + m_labelGutter;
         int xRatio = (m_matrix.getColumnCount() * m_cellWidth) + m_labelGutter;
         int yRatio = y + m_cellHeight - m_fontGutter;
         String rowName = m_matrix.getRowName( i );
         rowName = rowName.trim();  // remove leading and trailing whitespace
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
        int x = m_cellWidth + (j * m_cellWidth) - m_fontGutter;
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
        Graphics2D g2 = (Graphics2D)g;
        AffineTransform fontAT = new AffineTransform();
        //fontAT.shear(0.2, 0.0);  // slant text backwards
        fontAT.setToRotation( Math.PI * 3.0f / 2.0f ); // counter-clockwise 90 degrees
        FontRenderContext frc = g2.getFontRenderContext();
        Font theDerivedFont = m_labelFont.deriveFont( fontAT );
        TextLayout tstring = new TextLayout( columnName, theDerivedFont, frc );
        tstring.draw( g2, x, y );
     } // end for column
  } // end drawColumnNames
  
  /**
   * ----------- SHOULD PROBABLY NOT BE IN THIS CLASS -----------
   *
   * @return  the pixel width of the string for the specified font.
   */
  public static int stringPixelWidth( String s, Font font, Component c ) {
     
     FontMetrics fontMetrics = c.getFontMetrics( font );
     return fontMetrics.charsWidth( s.toCharArray(), 0, s.length() );
     
  } // end stringPixelWidth
  
  /**
   * ----------- SHOULD PROBABLY NOT BE IN THIS CLASS -----------
   */
  public static int maxStringPixelWidth( String[] strings, Font font, Component c ) {
     
     // the number of chars in the longest string
     int maxWidth = 0;
     int width;
     String s;
     for (int i = 0;  i < strings.length;  i++)
     {
        s = strings[i];
        s.trim();  // remove leading and trailing whitespace
        width = stringPixelWidth( s, font, c );
        if (maxWidth < width)
           maxWidth = width;
     }
     
     return maxWidth;
     
  } // end getMaxPixelWidth
  
  
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
       m_fontGutter = (int) ( (double) m_cellHeight * .22);
    }
  }

  /**
   * @return  the height of the font
   */
  private int getFontSize() {
     return Math.max( m_cellHeight, 5 );
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
}
