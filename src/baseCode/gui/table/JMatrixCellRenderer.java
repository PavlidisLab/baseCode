package baseCode.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import baseCode.gui.JMatrixDisplay;

/**
 * @author Will Braynen
 * @version $Id: JMatrixTableCellRenderer.java,v 1.3 2004/06/23 21:41:56
 *          wbraynen Exp $
 */
public class JMatrixCellRenderer extends JLabel implements TableCellRenderer {

   JMatrixDisplay m_matrixDisplay;

   // to format tooltips
   DecimalFormat m_scientificNotation = new DecimalFormat( "0.##E0" );
   DecimalFormat m_regular = new DecimalFormat();

   public JMatrixCellRenderer(JMatrixDisplay matrixDisplay) {

      m_matrixDisplay = matrixDisplay;
      setOpaque( true );

      // for tooltips
      m_regular.setMaximumFractionDigits( 3 );
   }

   // This method is called each time a cell in a column
   // using this renderer needs to be rendered.
   public Component getTableCellRendererComponent( JTable table,
         Object tableCellValue, boolean isSelected, boolean hasFocus,
         int displayedRow, int displayedColumn ) {
      // 'value' is value contained in the cell located at
      // (rowIndex, vColIndex)

      if ( isSelected ) {
         // cell (and perhaps other cells) are selected
      }

      if ( hasFocus ) {
         // this cell is the anchor and the table has the focus
      }

      Point coords = ( Point ) tableCellValue;
      int row = coords.x;
      int column = coords.y;

      // Set the color
      Color matrixColor;
      try {
         matrixColor = m_matrixDisplay.getColor( row, column );
      }
      catch( ArrayIndexOutOfBoundsException e ) {
         matrixColor = m_matrixDisplay.getMissingColor();
      }
      setBackground( matrixColor );
      
      // The tooltip should always show the actual (non-normalized) value
      double matrixValue;
      boolean isStandardized = m_matrixDisplay.getStandardizedEnabled();
      m_matrixDisplay.setStandardizedEnabled( false );
      {
         try {
            matrixValue = m_matrixDisplay.getValue( row, column );
         }
         catch( ArrayIndexOutOfBoundsException e ) {
            matrixValue = Double.NaN;
         }
      }
      m_matrixDisplay.setStandardizedEnabled( isStandardized ); // return to
                                                                // previous
                                                                // state

      // Only very small and very large numbers should be displayed in
      // scientific notation
      String value;
      if ( Math.abs( matrixValue ) < 0.01 || Math.abs( matrixValue ) > 100000 ) {
         value = m_scientificNotation.format( matrixValue );
      } else {
         value = m_regular.format( matrixValue );
      }
      setToolTipText( value );

      // Since the renderer is a component, return itself
      return this;
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

} // end class MatrixDisplayCellRenderer
