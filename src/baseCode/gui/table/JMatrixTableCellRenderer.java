package baseCode.gui.table;

import java.text.DecimalFormat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import baseCode.gui.JMatrixDisplay;

/**
 * @author  Will Braynen
 * @version $Id$
 */
public class JMatrixTableCellRenderer
    extends JLabel
    implements TableCellRenderer {

   JMatrixDisplay m_matrixDisplay;
   DecimalFormat m_nf;

   public JMatrixTableCellRenderer( JMatrixDisplay matrixDisplay, DecimalFormat nf ) {

      m_matrixDisplay = matrixDisplay;
      setOpaque( true );

      // to format tooltips
      m_nf = nf;
   }

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

      Point coords = ( Point ) value;
      int row = coords.x;
      int column = coords.y;

      // Set the color
      Color matrixColor = m_matrixDisplay.getColor( row, column );
      setBackground( matrixColor );

      // The tooltip should always show the actual (non-normalized) value
      boolean isStandardized = m_matrixDisplay.getStandardizedEnabled();
      m_matrixDisplay.setStandardizedEnabled( false );
      double matrixValue = m_matrixDisplay.getValue( row, column );
      m_matrixDisplay.setStandardizedEnabled( isStandardized ); // return to previous state
      setToolTipText( "" + m_nf.format( matrixValue ) );

      // Since the renderer is a component, return itself
      return this;
   }

   // The following methods override the defaults for performance reasons
   public void validate() {}

   public void revalidate() {}

   protected void firePropertyChange( String propertyName, Object oldValue,
                                      Object newValue ) {}

   public void firePropertyChange( String propertyName, boolean oldValue,
                                   boolean newValue ) {}

} // end class MatrixDisplayCellRenderer
