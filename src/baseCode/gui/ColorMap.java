package baseCode.gui;

import java.awt.Color;

/**
 * <p>Title: ColorMap</p>
 * <p>Description: contains predefined color maps for visualization and color palette methods</p>
 * <p> Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Will Braynen
 * @version $Id$
 */

public class ColorMap {

   /** first color in the current color map */
   protected Color m_minColor;
   /** last color in the current color map */
   protected Color m_maxColor;
   protected Color[] m_customColorMap;

   public static final int m_defaultSuggestedNumberOfColors = 64;
   public static final Color DARK_RED = new Color( 128, 0, 0 );
   public static final Color[] GREENRED_COLORMAP = {
       Color.green, Color.black, Color.red};
   public static final Color[] REDGREEN_COLORMAP = {
       Color.red, Color.black, Color.green};
   public static final Color[] BLACKBODY_COLORMAP = {
       Color.black, DARK_RED, Color.orange, Color.yellow, Color.white};

   protected Color[] m_currentColorMap = GREENRED_COLORMAP; // reference to a color map
   protected Color[] m_colorPalette;

   public ColorMap() {

      this( m_defaultSuggestedNumberOfColors );
   }

   public ColorMap( int suggestedNumberOfColors ) {

      this( suggestedNumberOfColors, GREENRED_COLORMAP );
   }

   public ColorMap( Color[] colorMap ) {

      this( m_defaultSuggestedNumberOfColors, colorMap );
   }

   /** Pre-condition: suggestedNumberOfColors > colorMap.length */
   public ColorMap( int suggestedNumberOfColors, Color[] colorMap ) {

      m_currentColorMap = colorMap;
      m_colorPalette = createColorPalette( suggestedNumberOfColors, colorMap );
   }

   /**
    * Calculate how fast we have to change color components.
    * Assume min and max colors are different!
    *
    * @param  minColor  red, green, or blue component of the RGB color
    * @param  maxColor  red, green, or blue component of the RGB color
    * @return  positive or negative step size
    */
   protected int getStepSize( int minColor, int maxColor, int totalColors ) {

      int colorRange = maxColor - minColor;
      double stepSize = colorRange / ( 1 == totalColors ? 1 : totalColors - 1 );
      return ( int ) Math.round( stepSize );
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
   protected Color[] createColorPalette( int suggestedNumberOfColors, Color[] colorMap ) {

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

      for ( int segment = 0; segment < totalSegments; segment++ ) {
         // the minimum color for each segment as defined by the current color map
         minColor = colorMap[segment];
         int r = minColor.getRed();
         int g = minColor.getGreen();
         int b = minColor.getBlue();

         // the maximum color for each segment and the step sizes
         maxColor = colorMap[segment + 1];
         int redStepSize = getStepSize( r, maxColor.getRed(), colorsPerSegment );
         int greenStepSize = getStepSize( g, maxColor.getGreen(), colorsPerSegment );
         int blueStepSize = getStepSize( b, maxColor.getBlue(), colorsPerSegment );

         for ( int k, i = 0; i < colorsPerSegment; i++ ) {
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

   public Color getColor( int i ) {

      return m_colorPalette[i];
   }

   public Color[] getPalette() {

      return m_colorPalette;
   }

   /**
    * @return  the number of colors in the palette
    */
   public int getPaletteSize() {

      return m_colorPalette.length;
   }
}
