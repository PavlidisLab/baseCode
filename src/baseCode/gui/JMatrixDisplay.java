package baseCode.Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class JMatrixDisplay extends JPanel {

  // data fields
  ColorMatrix m_colorMatrix;

  boolean m_isPrintLabels = true;
  boolean m_isImageSnapshot = false;
  BufferedImage m_image = null;

  private int m_ratioWidth = 0, m_labelWidth = 70;
  private int m_labelGutter = 5;
  private Font m_labelFont = null;
  private int m_fontSize = 10;
  private final int m_maxFontSize = 10;
  private final int m_defaultResolution  = 120;
  private int m_resolution = m_defaultResolution;
  private int m_textSize = 0;
  protected static int m_rowHeight = 10; // in pixels
  protected static int m_rowWidth = 5; // in pixels


  public JMatrixDisplay() {

     this( new ColorMatrix() );
  }


  public JMatrixDisplay( ColorMatrix colorMatrix ) {

     m_colorMatrix = colorMatrix;
     initSize();
  }

  /**
   * Sets the display sizes based on the microarraySetView
   */
  protected void initSize() {

    if (m_colorMatrix != null)
    {
      int height = m_rowHeight * m_colorMatrix.getRowCount();
      int width  = m_rowWidth  * m_colorMatrix.getColumnCount();

      if (m_isPrintLabels)
      {
          width += m_labelWidth;
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

    if (m_colorMatrix != null)
    {
      Graphics2D ig = null;
      if (m_isImageSnapshot) {
        m_image = new BufferedImage(this.getWidth(),
                                    this.getHeight(),
                                    BufferedImage.TYPE_INT_RGB);
        ig = m_image.createGraphics();
        ig.setColor(Color.white);
        ig.fillRect(0, 0, this.getWidth(), this.getHeight());
      }
      int fontGutter = (int) ( (double) m_rowHeight * .22);
      int rowCount = m_colorMatrix.getRowCount();
      int columnCount = m_colorMatrix.getColumnCount();

      // loop through the microarray
      for (int i = 0;  i < rowCount;  i++)
      {
        int y = i * m_rowHeight;

        for (int j = 0;  j < columnCount;  j++)
        {
          int x = (j * m_rowWidth);
          int width = ( (j + 1) * m_rowWidth) - x;

          Color color = m_colorMatrix.getColor( i, j );
          g.setColor(color);

          if (m_isImageSnapshot)
            ig.setColor(color);

          g.fillRect(x, y, width, m_rowHeight);

          if (m_isImageSnapshot)
            ig.fillRect(x, y, width, m_rowHeight);
        }

        if (m_isPrintLabels && columnCount > 0)
        {

           g.setColor(Color.black);
           if (m_isImageSnapshot)
               ig.setColor(Color.black);

           setFont();

           g.setFont(m_labelFont);
           if (m_isImageSnapshot)
               ig.setFont(m_labelFont);

           int xRatio = (columnCount * m_rowWidth) + m_labelGutter;
           int yRatio = y + m_rowHeight - fontGutter;
           String rowName = m_colorMatrix.getRowName( i );
           if (null == rowName)
           {
              rowName = "Undefined";
           }
           g.drawString( rowName, xRatio, yRatio );
           if (m_isImageSnapshot)
              ig.drawString( rowName, xRatio, yRatio );

        } // end if print row (row) labels

      } // end for rows
    } // end if (microarray != null)
  } // end paintComponent

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
    return Math.max( m_rowHeight, 5 );
  }


}
