package baseCode.Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class JMatrixDisplay extends JPanel {

  // data fields
  ColorMatrix m_matrix;

  protected boolean m_isPrintRowNames = false;
  protected boolean m_isPrintColumnNames = true; // TO DO: implement
  protected BufferedImage m_image = null;

  protected int m_ratioWidth = 0;
  protected int m_rowNameWidth = 70; // TO DO: set this dynamically to the width needed for the longest row name
  protected int m_labelGutter = 5;
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

  private boolean picOnceSaved = false;  // TO DO: delete this



  public JMatrixDisplay( ColorMatrix matrix ) {

     m_matrix = matrix;
     initSize();
  }

  /**
   * Sets the display sizes based on the microarraySetView
   */
  protected void initSize() {

    if (m_matrix != null)
    {
      int height = m_cellHeight * m_matrix.getRowCount();
      int width  = m_cellWidth  * m_matrix.getColumnCount();

      if (m_isPrintRowNames)
      {
          width += m_rowNameWidth;
      }

      Dimension d = new Dimension( width, height );
      setPreferredSize( d );
      setSize( d );
      this.revalidate();
    }
  }

  /**
   * <code>JComponent</code> method used to render this component
   * @param g Graphics used for painting
   */
  protected void paintComponent( Graphics g ) {

    super.paintComponent(g);
    drawDisplay( g, m_matrix );

    if (!picOnceSaved) {
       picOnceSaved = true;
       // save screenshot to file
       String filename = "C:\\matrix.png";
       try {
          saveToFile(filename);
       }
       catch (java.io.IOException e) {
          System.err.println("IOException: unable to save screenshot to " + filename);
       }
    }
  } // end paintComponent

  /**
   * Gets called from #paintComponent and #saveToFile
   */
  protected void drawDisplay( Graphics g, ColorMatrix matrix ) {

     if (matrix != null)
     {
        g.setColor(Color.white);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        int fontGutter = (int) ( (double) m_cellHeight * .22);
        int rowCount = matrix.getRowCount();
        int columnCount = matrix.getColumnCount();

        // loop through the matrix, one row at a time
        for (int i = 0;  i < rowCount;  i++)
        {
           int y = i * m_cellHeight;

           // draw an entire row, one cell at a time
           for (int j = 0; j < columnCount; j++) 
           {
              int x = (j * m_cellWidth);
              int width = ( (j + 1) * m_cellWidth) - x;

              Color color = matrix.getColor(i, j);
              g.setColor(color);
              g.fillRect(x, y, width, m_cellHeight);
           }

           // print row names
           if (m_isPrintRowNames && columnCount > 0) 
           {
              g.setColor(Color.black);
              setFont();
              g.setFont(m_labelFont);
              int xRatio = (columnCount * m_cellWidth) + m_labelGutter;
              int yRatio = y + m_cellHeight - fontGutter;
              String rowName = matrix.getRowName(i);
              if (null == rowName) {
                 rowName = "Undefined";
              }
              g.drawString(rowName, xRatio, yRatio);
           } // end printing row names
        } // end looping through the matrix, one row at a time
     } // end if (matrix != null)
  } // end drawDisplay

  /**
   * Sets the <code>Font</code> used for drawing text
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
    }
  }

  /**
   * Gets the <code>Font</code> size
   * @return <code>Font</code> size
   */
  private int getFontSize() {
    return Math.max( m_cellHeight, 5 );
  }

  /**
   * Saves to file
   */
  public void saveToFile( String outFileName ) throws java.io.IOException {

     Graphics2D g = null;
     m_image = new BufferedImage(this.getWidth(),
                                 this.getHeight(),
                                 BufferedImage.TYPE_INT_RGB);
     g = m_image.createGraphics();

     drawDisplay( g, m_matrix );

      ImageIO.write( m_image, "png", new File( outFileName ));
  }
}
