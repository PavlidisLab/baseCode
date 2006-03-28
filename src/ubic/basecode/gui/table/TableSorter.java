package ubic.basecode.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import ubic.basecode.gui.JMatrixDisplay;

/**
 * TableSorter is a decorator for TableModels; adding sorting functionality to a supplied TableModel. TableSorter does
 * not store or copy the data in its TableModel; instead it maintains a map from the row indexes of the view to the row
 * indexes of the model. As requests are made of the sorter (like getValueAt(row, col)) they are passed to the
 * underlying model after the row numbers have been translated via the internal mapping array. This way, the TableSorter
 * appears to hold another copy of the table with the rows in a different order.
 * </p>
 * <p>
 * TableSorter registers itself as a listener to the underlying model, just as the JTable itself would. Events recieved
 * from the model are examined, sometimes manipulated (typically widened), and then passed on to the TableSorter's
 * listeners (typically the JTable). If a change to the model has invalidated the order of TableSorter's rows, a note of
 * this is made and the sorter will resort the rows the next time a value is requested.
 * </p>
 * <p>
 * When the tableHeader property is set, either by using the setTableHeader() method or the two argument constructor,
 * the table header may be used as a complete UI for TableSorter. The default renderer of the tableHeader is decorated
 * with a renderer that indicates the sorting status of each column. In addition, a mouse listener is installed with the
 * following behavior:</p>
 * <ul>
 * <li>Mouse-click: Clears the sorting status of all other columns and advances the sorting status of that column
 * through three values: {NOT_SORTED, ASCENDING, DESCENDING} (then back to NOT_SORTED again).
 * <li>SHIFT-mouse-click: Clears the sorting status of all other columns and cycles the sorting status of the column
 * through the same three values, in the opposite order: {NOT_SORTED, DESCENDING, ASCENDING}.
 * <li>CONTROL-mouse-click and CONTROL-SHIFT-mouse-click: as above except that the changes to the column do not cancel
 * the statuses of columns that are already sorting - giving a way to initiate a compound sort.
 * </ul>
 * <p>
 * This is a long overdue rewrite of a class of the same name that first appeared in the swing table demos in 1997.
 * </p>
 * 
 * @author Philip Milne
 * @author Brendon McLean
 * @author Dan van Enckevort
 * @author Parwinder Sekhon
 * @author Paul Pavlidis (minor adjustments)
 * @version 2.0 02/27/04
 * @version $Id$
 */

public class TableSorter extends AbstractTableModel {
    protected TableModel tableModel;
    JMatrixDisplay m_matrixDisplay; // get rid of this!

    public static final int DESCENDING = -1;
    public static final int NOT_SORTED = 0;
    public static final int ASCENDING = 1;

    private static Directive EMPTY_DIRECTIVE = new Directive( -1, NOT_SORTED );

    public static final Comparator COMPARABLE_COMAPRATOR = new Comparator() {
        public int compare( Object o1, Object o2 ) {
            return ( ( Comparable ) o1 ).compareTo( o2 );
        }
    };
    public static final Comparator LEXICAL_COMPARATOR = new Comparator() {
        public int compare( Object o1, Object o2 ) {
            return o1.toString().compareTo( o2.toString() );
        }
    };

    private Row[] viewToModel;
    int[] modelToView;

    private JTableHeader tableHeader;
    private MouseListener mouseListener;
    private TableModelListener tableModelListener;
    private Map columnComparators = new HashMap();
    List sortingColumns = new ArrayList();

    public TableSorter() {
        this.mouseListener = new MouseHandler();
        this.tableModelListener = new TableModelHandler();
    }

    public TableSorter( TableModel tableModel ) {
        this();
        setTableModel( tableModel );
    }

    public TableSorter( TableModel tableModel, JTableHeader tableHeader ) {
        this();
        setTableHeader( tableHeader );
        setTableModel( tableModel );
    }

    public TableSorter( TableModel tableModel, JMatrixDisplay matrixDisplay ) {
        this( tableModel );
        m_matrixDisplay = matrixDisplay;
    }

    void clearSortingState() {
        viewToModel = null;
        modelToView = null;
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel( TableModel tableModel ) {
        if ( this.tableModel != null ) {
            this.tableModel.removeTableModelListener( tableModelListener );
        }

        this.tableModel = tableModel;
        if ( this.tableModel != null ) {
            this.tableModel.addTableModelListener( tableModelListener );
        }

        clearSortingState();
        fireTableStructureChanged();
    }

    public JTableHeader getTableHeader() {
        return tableHeader;
    }

    public void setTableHeader( JTableHeader tableHeader ) {
        if ( this.tableHeader != null ) {
            this.tableHeader.removeMouseListener( mouseListener );
            TableCellRenderer defaultRenderer = this.tableHeader.getDefaultRenderer();
            if ( defaultRenderer instanceof SortableHeaderRenderer ) {
                this.tableHeader.setDefaultRenderer( ( ( SortableHeaderRenderer ) defaultRenderer ).tableCellRenderer );
            }
        }
        this.tableHeader = tableHeader;
        if ( this.tableHeader != null ) {
            this.tableHeader.addMouseListener( mouseListener );
            this.tableHeader.setDefaultRenderer( new SortableHeaderRenderer( this.tableHeader.getDefaultRenderer() ) );
        }
    }

    public boolean isSorting() {
        return sortingColumns.size() != 0;
    }

    private Directive getDirective( int column ) {
        for ( int i = 0; i < sortingColumns.size(); i++ ) {
            Directive directive = ( Directive ) sortingColumns.get( i );
            if ( directive.column == column ) {
                return directive;
            }
        }
        return EMPTY_DIRECTIVE;
    }

    public int getSortingStatus( int column ) {
        return getDirective( column ).direction;
    }

    private void sortingStatusChanged() {
        clearSortingState();
        fireTableDataChanged();
        if ( tableHeader != null ) {
            tableHeader.repaint();
        }
    }

    public void setSortingStatus( int column, int status ) {
        Directive directive = getDirective( column );
        if ( directive != EMPTY_DIRECTIVE ) {
            sortingColumns.remove( directive );
        }
        if ( status != NOT_SORTED ) {
            sortingColumns.add( new Directive( column, status ) );
        }
        sortingStatusChanged();
    }

    protected Icon getHeaderRendererIcon( int column, int size ) {
        Directive directive = getDirective( column );
        if ( directive == EMPTY_DIRECTIVE ) {
            return null;
        }
        return new Arrow( directive.direction == DESCENDING, size, sortingColumns.indexOf( directive ) );
    }

    public void cancelSorting() {
        sortingColumns.clear();
        sortingStatusChanged();
    }

    public void setColumnComparator( Class type, Comparator comparator ) {
        if ( comparator == null ) {
            columnComparators.remove( type );
        } else {
            columnComparators.put( type, comparator );
        }
    }

    protected Comparator getComparator( int column ) {
        Class columnType = tableModel.getColumnClass( column );
        Comparator comparator = ( Comparator ) columnComparators.get( columnType );
        if ( comparator != null ) {
            return comparator;
        }
        if ( Comparable.class.isAssignableFrom( columnType ) ) {
            return COMPARABLE_COMAPRATOR;
        }
        return LEXICAL_COMPARATOR;
    }

    protected Comparator getComparator( Class columnClass ) {
        Class columnType = columnClass;
        Comparator comparator = ( Comparator ) columnComparators.get( columnType );
        if ( comparator != null ) {
            return comparator;
        }
        if ( Comparable.class.isAssignableFrom( columnType ) ) {
            return COMPARABLE_COMAPRATOR;
        }
        return LEXICAL_COMPARATOR;
    }

    private Row[] getViewToModel() {
        if ( viewToModel == null ) {
            int tableModelRowCount = tableModel.getRowCount();
            viewToModel = new Row[tableModelRowCount];
            for ( int row = 0; row < tableModelRowCount; row++ ) {
                viewToModel[row] = new Row( row );
            }

            if ( isSorting() ) {
                Arrays.sort( viewToModel );
            }
        }
        return viewToModel;
    }

    public int modelIndex( int viewIndex ) {
        return getViewToModel()[viewIndex].modelIndex;
    }

    int[] getModelToView() {
        if ( modelToView == null ) {
            int n = getViewToModel().length;
            modelToView = new int[n];
            for ( int i = 0; i < n; i++ ) {
                modelToView[modelIndex( i )] = i;
            }
        }
        return modelToView;
    }

    // TableModel interface methods

    public int getRowCount() {
        return ( tableModel == null ) ? 0 : tableModel.getRowCount();
    }

    public int getColumnCount() {
        return ( tableModel == null ) ? 0 : tableModel.getColumnCount();
    }

    public String getColumnName( int column ) {
        return tableModel.getColumnName( column );
    }

    public Class getColumnClass( int column ) {
        return tableModel.getColumnClass( column );
    }

    public boolean isCellEditable( int row, int column ) {
        return tableModel.isCellEditable( modelIndex( row ), column );
    }

    public Object getValueAt( int row, int column ) {
        return tableModel.getValueAt( modelIndex( row ), column );
    }

    public void setValueAt( Object aValue, int row, int column ) {
        tableModel.setValueAt( aValue, modelIndex( row ), column );
    }

    // Helper classes

    private class Row implements Comparable {
        int modelIndex;

        public Row( int index ) {
            this.modelIndex = index;
        }

        public int compareTo( Object o ) {
            int row1 = modelIndex;
            int row2 = ( ( Row ) o ).modelIndex;

            for ( Iterator it = sortingColumns.iterator(); it.hasNext(); ) {
                Directive directive = ( Directive ) it.next();
                int column = directive.column;
                Object o1 = tableModel.getValueAt( row1, column );
                Object o2 = tableModel.getValueAt( row2, column );
                Comparator comparator = null;
                int comparison = 0;
                boolean favor = false;

                if ( o1 == null && o2 == null ) {
                    comparison = 0;
                } else if ( o1 == null ) {
                    if ( directive.direction == DESCENDING ) // Define null less than everything, except null.
                        comparison = -1;
                    else
                        // Define null greater than everything, except null.
                        comparison = 1;
                } else if ( o2 == null ) {
                    if ( directive.direction == DESCENDING )
                        comparison = 1;
                    else
                        comparison = -1;
                } else if ( o1 != null && o2 != null ) {
                    if ( o1.getClass().equals( Double.class ) ) {
                        comparator = getComparator( Double.class );
                    } else if ( o1.getClass().equals( Integer.class ) ) {
                        comparator = getComparator( Integer.class );
                    } else if ( o1.getClass().equals( Point.class ) ) {
                        comparator = getComparator( Double.class );

                        // If sortColumn is in the matrix display, then model.getValueAt()
                        // returns a Point object that represents a coordinate into the
                        // display matrix. This is done so that the display matrix object
                        // can be asked for both the color and the value. We are here only
                        // interested in the value.

                        /**
                         * @todo we shouldn't include a dependency on JMatrixDisplay here. Find a workaround.
                         */
                        if ( m_matrixDisplay != null ) {

                            Point p1 = ( Point ) o1;
                            Point p2 = ( Point ) o2;

                            o1 = new Double( m_matrixDisplay.getValue( p1.x, p1.y ) );
                            o2 = new Double( m_matrixDisplay.getValue( p2.x, p2.y ) );
                        }
                    } else if ( o1.getClass().equals( ArrayList.class ) ) {
                        if ( ( ( ArrayList ) o1 ).get( 0 ).getClass().equals( Double.class ) ) {
                            comparator = getComparator( Double.class );
                            // only comparing the first member of the list
                            Double a = ( Double ) ( ( ArrayList ) o1 ).get( 0 );
                            Double b = ( Double ) ( ( ArrayList ) o2 ).get( 0 );

                            // yes, we did get an array list, but we want to
                            // compare the Double values the array lists contain,
                            // not the array lists themselves.
                            o1 = a;
                            o2 = b;
                        }
                    } else if ( o1.getClass().equals( Vector.class ) ) {
                        if ( ( ( Vector ) o1 ).get( 0 ).getClass().equals( String.class ) ) {
                            if ( ( ( Vector ) o1 ).size() == 2 ) {
                                comparison = -1;
                                favor = true;
                            } else if ( ( ( Vector ) o2 ).size() == 2 ) {
                                comparison = 1;
                                favor = true;
                            } else // compare normally
                            {
                                comparator = getComparator( String.class );
                                String a = ( String ) ( ( Vector ) o1 ).get( 0 );
                                String b = ( String ) ( ( Vector ) o2 ).get( 0 );
                                o1 = a;
                                o2 = b;
                            }
                        } else if ( ( ( Vector ) o1 ).get( 0 ).getClass().equals( Double.class ) ) {

                            // only comparing the first member of the list
                            Double a = ( Double ) ( ( ArrayList ) o1 ).get( 0 );
                            Double b = ( Double ) ( ( ArrayList ) o2 ).get( 0 );
                            comparator = getComparator( Double.class );

                            // yes, we did get a Vector, but we want to
                            // compare the Double values the Vectors contain,
                            // not the Vectors themselves
                            o1 = a;
                            o2 = b;
                        }
                    } else {
                        comparator = getComparator( column );
                    }
                    if ( favor != true ) { // we're not favoring anyone
                        comparison = comparator.compare( o1, o2 );
                    }
                }

                if ( comparison != 0 ) {
                    return directive.direction == DESCENDING ? -comparison : comparison;
                }
            }
            return 0;
        }
    }

    private class TableModelHandler implements TableModelListener {
        public void tableChanged( TableModelEvent e ) {
            // If we're not sorting by anything, just pass the event along.
            if ( !isSorting() ) {
                clearSortingState();
                fireTableChanged( e );
                return;
            }

            // If the table structure has changed, cancel the sorting; the
            // sorting columns may have been either moved or deleted from
            // the model.
            if ( e.getFirstRow() == TableModelEvent.HEADER_ROW ) {
                cancelSorting();
                fireTableChanged( e );
                return;
            }

            // We can map a cell event through to the view without widening
            // when the following conditions apply:
            //
            // a) all the changes are on one row (e.getFirstRow() == e.getLastRow()) and,
            // b) all the changes are in one column (column != TableModelEvent.ALL_COLUMNS) and,
            // c) we are not sorting on that column (getSortingStatus(column) == NOT_SORTED) and,
            // d) a reverse lookup will not trigger a sort (modelToView != null)
            //
            // Note: INSERT and DELETE events fail this test as they have column == ALL_COLUMNS.
            //
            // The last check, for (modelToView != null) is to see if modelToView
            // is already allocated. If we don't do this check; sorting can become
            // a performance bottleneck for applications where cells
            // change rapidly in different parts of the table. If cells
            // change alternately in the sorting column and then outside of
            // it this class can end up re-sorting on alternate cell updates -
            // which can be a performance problem for large tables. The last
            // clause avoids this problem.
            int column = e.getColumn();
            if ( e.getFirstRow() == e.getLastRow() && column != TableModelEvent.ALL_COLUMNS
                    && getSortingStatus( column ) == NOT_SORTED && modelToView != null ) {
                int viewIndex = getModelToView()[e.getFirstRow()];
                fireTableChanged( new TableModelEvent( TableSorter.this, viewIndex, viewIndex, column, e.getType() ) );
                return;
            }

            // Something has happened to the data that may have invalidated the row order.
            clearSortingState();
            fireTableDataChanged();
            return;
        }
    }

    private class MouseHandler extends MouseAdapter {
        public void mouseClicked( MouseEvent e ) {
            if ( e.getButton() == MouseEvent.BUTTON1 ) {
                JTableHeader h = ( JTableHeader ) e.getSource();
                TableColumnModel columnModel = h.getColumnModel();
                int viewColumn = columnModel.getColumnIndexAtX( e.getX() );
                if ( viewColumn < 0 ) return;
                int column = columnModel.getColumn( viewColumn ).getModelIndex();
                if ( column != -1 ) {
                    int status = getSortingStatus( column );
                    if ( !e.isControlDown() ) {
                        cancelSorting();
                    }
                    // Cycle the sorting states through {ASCENDING, DESCENDING} ignoring NOT_SORTED
                    status = ( status == ASCENDING ) ? DESCENDING : ASCENDING;
                    setSortingStatus( column, status );
                }
            }
        }
    }

    private static class Arrow implements Icon {
        private boolean descending;
        private int size;
        private int priority;

        public Arrow( boolean descending, int size, int priority ) {
            this.descending = descending;
            this.size = size;
            this.priority = priority;
        }

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            Color color = c == null ? Color.GRAY : c.getBackground();
            // In a compound sort, make each succesive triangle 20%
            // smaller than the previous one.
            int dx = ( int ) ( size / 2 * Math.pow( 0.8, priority ) );
            int dy = descending ? dx : -dx;
            // Align icon (roughly) with font baseline.
            y = y + 5 * size / 6 + ( descending ? -dy : 0 );
            int shift = descending ? 1 : -1;
            g.translate( x, y );

            // Right diagonal.
            g.setColor( color.darker() );
            g.drawLine( dx / 2, dy, 0, 0 );
            g.drawLine( dx / 2, dy + shift, 0, shift );

            // Left diagonal.
            g.setColor( color.brighter() );
            g.drawLine( dx / 2, dy, dx, 0 );
            g.drawLine( dx / 2, dy + shift, dx, shift );

            // Horizontal line.
            if ( descending ) {
                g.setColor( color.darker().darker() );
            } else {
                g.setColor( color.brighter().brighter() );
            }
            g.drawLine( dx, 0, 0, 0 );

            g.setColor( color );
            g.translate( -x, -y );
        }

        public int getIconWidth() {
            return size;
        }

        public int getIconHeight() {
            return size;
        }
    }

    private class SortableHeaderRenderer implements TableCellRenderer {
        TableCellRenderer tableCellRenderer;

        public SortableHeaderRenderer( TableCellRenderer tableCellRenderer ) {
            this.tableCellRenderer = tableCellRenderer;
        }

        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column ) {
            Component c = tableCellRenderer.getTableCellRendererComponent( table, value, isSelected, hasFocus, row,
                    column );
            if ( c instanceof JLabel ) {
                JLabel l = ( JLabel ) c;
                l.setHorizontalTextPosition( SwingConstants.LEFT );
                l.setVerticalAlignment( SwingConstants.BOTTOM );
                int modelColumn = table.convertColumnIndexToModel( column );
                l.setIcon( getHeaderRendererIcon( modelColumn, l.getFont().getSize() ) );
            }
            return c;
        }
    }

    private static class Directive {
        int column;
        int direction;

        public Directive( int column, int direction ) {
            this.column = column;
            this.direction = direction;
        }
    }
}