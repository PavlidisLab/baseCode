/*
 * Util.java
 *
 * Created on June 5, 2004, 10:21 AM
 */

package baseCode.graphics.text;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

import java.io.File;

/**
 *
 * @author  Will Braynen
 * @todo    Move all the filename methods (getExtension, etc) out of this class
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
      for ( int i = 0; i < strings.length; i++ ) {
         s = strings[i];
         width = stringPixelWidth( s, font, c );
         if ( maxWidth < width ) {
            maxWidth = width;
         }
      }

      return maxWidth;
   } // end getMaxPixelWidth

   /**
    * Draws a string vertically, turned 90 degrees counter-clockwise.
    * Read carefully what the <i>x</i> and <i>y</i> coordinates means;
    * chances are that if you draw to (x,y) = (0,0), you won't see anything.
    *
    * @param  g     the graphics context on which to draw
    * @param  text  the string to draw
    * @param  font  the font to use
    * @param  x     the <i>x</i> coordinate where you want to place the baseline of the text.
    * @param  y     the <i>y</i> coordinate where you want to place the first letter of the text.
    */
   public static void drawVerticalString( Graphics g, String text, Font font, int x, int y ) {
      Graphics2D g2 = ( Graphics2D ) g;
      AffineTransform fontAT = new AffineTransform();
      //fontAT.shear(0.2, 0.0);  // slant text backwards
      fontAT.setToRotation( Math.PI * 3.0f / 2.0f ); // counter-clockwise 90 degrees
      FontRenderContext frc = g2.getFontRenderContext();
      Font theDerivedFont = font.deriveFont( fontAT );
      TextLayout tstring = new TextLayout( text, theDerivedFont, frc );
      tstring.draw( g2, x, y );

   } // end drawVerticalString



   ///////////////////////////////////////////////////////////////////////
   //
   // Does the following really belong here?
   //
   protected final static String PNG_EXTENSION = "png";
   protected final static String GIF_EXTENSION = "gif";
   protected final static String TXT_EXTENSION = "txt";
   protected final static String[] IMAGE_EXTENSIONS = { PNG_EXTENSION, GIF_EXTENSION };
   protected final static String[] DATA_EXTENSIONS  = { TXT_EXTENSION };

   // default values
   public final static String DEFAULT_DATA_EXTENSION  = TXT_EXTENSION;
   public final static String DEFAULT_IMAGE_EXTENSION = PNG_EXTENSION;


   /**
    * Returns the extension of a file.
    */
   public static String getExtension( String filename ) {

      String extension = null;
      int i = filename.lastIndexOf( '.' );

      if (i > 0 &&  i < filename.length() - 1) {
         extension = filename.substring( i + 1 ).toLowerCase();
      }
      return extension;
   } // end getExtension


   public static String getWithoutExtension( String filename ) {

      String[] s = filename.split( "." );
      String extension = s[s.length - 1];
      String filenameWithoutExtension = filename.substring(
         filename.length() -
         extension.length() -
         1, filename.length() - 1 );

      return filenameWithoutExtension;
   } // end getFilenameWithoutExtension

   /**
    * @return  the new filename with the changed extension, but does not
    *          modify the <code>filename</code> parameter.
    */
   public static String changeExtension( String filename, String newExtension ) {

      String filenameWithoutExtension = getWithoutExtension( filename );
      return (filenameWithoutExtension + newExtension);
   } // end getWithChangedExtension


   public static boolean hasImageExtension( String filename ) {

      String extension = getExtension( filename );
      if (extension != null) {
         for (int i = 0;  i < IMAGE_EXTENSIONS.length;  i++) {
            if ( IMAGE_EXTENSIONS[i].equals( extension ) ) {
               return true;
            }
         }
      }
      return false;
   } // end hasImageExtension


   public static boolean hasDataExtension( String filename ) {

      String extension = getExtension( filename );
      if (extension != null) {
         for (int i = 0;  i < DATA_EXTENSIONS.length;  i++) {
            if ( DATA_EXTENSIONS[i].equals( extension ) ) {
               return true;
            }
         }
      }
      return false;
   } // end hasImageExtension


   /**
    * @return  the new filename with the added extension, but does not
    *          modify the <code>filename</code> parameter.
    */
   public static String addImageExtension( String filename ) {
      return ( filename + "." + DEFAULT_IMAGE_EXTENSION );
   }

   /**
    * @return  the new filename with the added extension, but does not
    *          modify the <code>filename</code> parameter.
    */
   public static String addDataExtension( String filename ) {
      return ( filename + "." + DEFAULT_DATA_EXTENSION );
   }
}
