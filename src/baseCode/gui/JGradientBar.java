package baseCode.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Dimension;

/**
 * A GUI legend component that displays a color map as a color gradient 
 * from min to max, traversing all the colors in the color map.
 *
 * @author  Will Braynen
 * @version $Id$
 */
public class JGradientBar extends JPanel {
   
   JLabel m_min;
   JLabel m_max;
   JGradientLabel m_gradient;
      
   /** Creates a new instance of JGradientBar */
   public JGradientBar( String min, String max, Color[] colorMap ) {
      
      m_min = new JLabel( min );
      m_max = new JLabel( max );
      m_gradient = new JGradientLabel( colorMap );
      
      setLayout( new FlowLayout() );
      
      Font font = getFont().deriveFont( Font.BOLD, 13.0f );
      m_min.setFont( font );
      m_max.setFont( font );
      
      add( m_min );
      add( m_gradient );
      add( m_max );
   } // end constructor
   
   public void setLabels( String min, String max ) {
      
      m_min.setText( min );
      m_max.setText( max );
   } // end setLabels
   
} // end class JGradientBar



class JGradientLabel extends JLabel {
   
   // fields
   Color[] m_colorMap;   
   
   /** Creates a new instance of JGradientLabel */
   public JGradientLabel( Color[] colorMap ) {

      // colorMap should contain at least two colors
      if (0 == colorMap.length) {
         
         // if there are no colors, default to grey for both colors
         Color color = colorMap[0];
         colorMap = new Color[2];
         colorMap[0] = colorMap[1] = Color.LIGHT_GRAY;
      }
      else if (1 == colorMap.length) {
         
         // if there is only one color, make the second color the same
         Color color = colorMap[0];
         colorMap = new Color[2];
         colorMap[0] = colorMap[1] = color;
      }
      
      m_colorMap = colorMap;
      
      int width  = 100;
      int height = 20;
      Dimension d = new Dimension( width, height );
      setSize( d );
      setPreferredSize( d );
   } // end constructor
   
   protected void paintComponent( Graphics g ) {
      
      Graphics2D g2 = (Graphics2D) g;
            
      Color color1 = Color.BLUE;
      Color color2 = Color.YELLOW;
      
      final int width = getWidth();
      final int height = getHeight();
      
      int x = 0;
      int y = 0;

      // Go from one color to another, creating a gradient in-between,
      // painting from left to right on this component
      int intervalCount = m_colorMap.length - 1;
      int intervalWidth = width / intervalCount;
      
      for (int i = 0;  i < intervalCount;  i++) {
         
         color1 = m_colorMap[i];
         color2 = m_colorMap[i+1];
         
         GradientPaint oneColorToAnother = new GradientPaint( x, y, color1, x + intervalWidth, y, color2 );
         g2.setPaint( oneColorToAnother );
         g2.fillRect( x, y, width, height );
         
         // Move to paint the next vertical screen slice of this component
         x += ( width / intervalCount );
      }
   } // end paintComponent
} // end JGradientLabel
