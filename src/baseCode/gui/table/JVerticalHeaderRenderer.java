package baseCode.gui.table;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import baseCode.graphics.text.Util;

/**
 * @author Will Braynen
 * @version $Id$
 */
public class JVerticalHeaderRenderer extends JTableHeader implements
      TableCellRenderer {

   String m_columnName;
   final int PREFERRED_HEIGHT = 80;
   final int MAX_TEXT_LENGTH = 12;

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
      setToolTipText( m_columnName );

      // Since the renderer is a component, return itself
      return this;
   }

   protected void paintComponent( Graphics g ) {

      super.paintComponent( g );
      Font font = getFont();

      if ( m_columnName.length() > MAX_TEXT_LENGTH ) {
         m_columnName = m_columnName.substring( 0, MAX_TEXT_LENGTH );

      }
      int x = getSize().width - 4;
      int y = getSize().height - 4;
      Util.drawVerticalString( g, m_columnName, font, x, y );
   }

   public Dimension getPreferredSize() {

      return new Dimension( super.getPreferredSize().width, PREFERRED_HEIGHT );
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