package baseCode.gui.table;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import baseCode.graphics.text.Util;

/**
 * @author Will Braynen
 * @version $Id: JHorizontalTableHeaderRenderer.java,v 1.1 2004/06/22 23:38:58
 *          wbraynen Exp $
 */
public class JHorizontalTableHeaderRenderer extends JTableHeader implements
      TableCellRenderer {

   String m_columnName;

   // This method is called each time a column header
   // using this renderer needs to be rendered.
   public Component getTableCellRendererComponent( JTable table, Object value,
         boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex ) {
      // 'value' is column header value of column 'vColIndex'
      // rowIndex is always -1
      // isSelected is always false
      // hasFocus is always false

      // Configure the component with the specified value
      m_columnName = value.toString();

      // Set tool tip if desired
      //setToolTipText( columnName );

      // Since the renderer is a component, return itself
      return this;
   }

   protected void paintComponent( Graphics g ) {

      super.paintComponent( g );

      int x = getSize().width / 2
            - Util.stringPixelWidth( m_columnName, getFont(), this ) / 2;
      int y = getSize().height - 10;
      g.drawString( m_columnName, x, y );

   }

   // The following methods override the defaults for performance reasons
   public void validate() {
   }

   public void revalidate() {
   }

   protected void firePropertyChange( String propertyName, Object oldValue,
         Object newValue ) {
   }

   public void firePropertyChange( String propertyName, boolean oldValue,
         boolean newValue ) {
   }

}