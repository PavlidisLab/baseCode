/*
 * Util.java
 *
 * Created on June 5, 2004, 10:21 AM
 */

package baseCode.graphics.text;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;



/**
 *
 * @author  Will Braynen
 */
public class Util {
   
   
  /**
   * @param   text  the string whose pixel width is to be measured
   * @param   font  the pixels width of a string varies from font to font
   * @param   c     the parent component; usually <code>this</code>
   * @return  the pixel width of the string for the specified font.
   */
   public static int stringPixelWidth( String text, Font font, Component c ) {

      FontMetrics fontMetrics = c.getFontMetrics( font );
      return fontMetrics.charsWidth( text.toCharArray(), 0, text.length() );

   } // end stringPixelWidth
  
   /**
   * @param   strings  an array of strings whose pixels widths to compare
   * @param   font     the pixels width of a string varies from font to font
   * @param   c        the parent component; usually <code>this</code>
   * @return  the largest pixel width of a string in the <code>strings</code>
   *          array.
   */
   public static int maxStringPixelWidth( String[] strings, Font font, Component c ) {

   // the number of chars in the longest string
   int maxWidth = 0;
   int width;
   String s;
   for (int i = 0;  i < strings.length;  i++)
   {
      s = strings[i];
      width = stringPixelWidth( s, font, c );
      if (maxWidth < width)
      maxWidth = width;
   }

   return maxWidth;
   } // end getMaxPixelWidth
     
   /**
    * Draws a string vertically, turned 90 degrees counter-clockwise
    * @param  g     the graphics context on which to draw
    * @param  text  the string to draw
    * @param  font  the font to use
    * @param  x     the <i>x</i> coordinate
    * @param  y     the <i>y</i> coordinate
    */
   public static void drawVerticalString( Graphics g, String text, Font font, int x, int y )
   {
      Graphics2D g2 = (Graphics2D)g;
      AffineTransform fontAT = new AffineTransform();
      //fontAT.shear(0.2, 0.0);  // slant text backwards
      fontAT.setToRotation( Math.PI * 3.0f / 2.0f ); // counter-clockwise 90 degrees
      FontRenderContext frc = g2.getFontRenderContext();
      Font theDerivedFont = font.deriveFont( fontAT );
      TextLayout tstring = new TextLayout( text, theDerivedFont, frc );
      tstring.draw( g2, x, y );
      
   } // end drawVerticalString     
}
