package baseCode.gui.table;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;


/**
 * 
 * @author  Will Braynen
 * @version $Id$
 */
public class JBarGraphCellRenderer
    extends JLabel
    implements TableCellRenderer {
   
   double[] m_values = null;
   final static int LINE_WIDTH = 2;
   final static Color[] COLORS = { 
      Color.RED, 
      Color.BLUE, 
      Color.DARK_GRAY, 
      Color.GREEN,
      Color.CYAN, 
      Color.MAGENTA, 
      Color.ORANGE 
   };

   // This method is called each time a cell in a column
   // using this renderer needs to be rendered.
   public Component getTableCellRendererComponent(
       JTable table,
       Object value,
       boolean isSelected,
       boolean hasFocus,
       int displayedRow,
       int displayedColumn ) {
      // 'value' is value contained in the cell located at
      // (rowIndex, vColIndex)

      m_values = (double[]) value;

      // Since the renderer is a component, return itself
      return this;
   }

   protected void paintComponent( Graphics g ) {
      
      super.paintComponent( g );
      
      final int width  = getWidth();
      final int height = getHeight();
      final int y = 0;
      
      for (int i = 0;  i < m_values.length;  i++) {
         
         // map from [0,1] range to [0,width] range
         int x = ( int )( m_values[i] * width );

         // what color to use?
         if ( i < COLORS.length) {
            g.setColor( COLORS[i] );
         } else {
            // ran out of colors!
            g.setColor( Color.LIGHT_GRAY );
         }
         
         // draw the vertical bar line
         g.drawRect( x, y, LINE_WIDTH, height );
      }
   } // end paintComponent
} // end class
