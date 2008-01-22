package ubic.basecode.dataStructure.matrix;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cern.colt.matrix.DoubleMatrix2D;

public abstract class DoubleMatrixNamed3D<R, C, S> extends AbstractNamedMatrix3D<R, C, S> {
    protected static final int MAX_ROWS_TO_PRINT = 20;
    protected static final int MAX_SLICES_TO_PRINT = 10;
    protected static Log log = LogFactory.getLog( DoubleMatrixNamed.class.getName() );

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.NamedMatrix#rows()
     */
    public abstract int rows();

    public abstract int columns();

    public abstract int slices();

    public abstract Object[][] getRowObj( int row );

    public abstract Object[][] getColObj( int col );

    public abstract Object[][] getSliceObj( int slice );

    public abstract boolean isMissing( int slice, int row, int col );

    public abstract double[][] getRow( int row );

    public abstract double[][] getColumn( int col );

    public abstract double[][] getSlice( int slice );

    public abstract double get( int x, int y, int z );

    /**
     * @param i
     * @param j
     * @return
     */
    public abstract double getQuick( int i, int j, int k );

    public abstract void set( int x, int y, int z, double value );

    /**
     * @param j
     * @return
     */
    public abstract DoubleMatrix2D viewRow( int slice );

    public abstract DoubleMatrix2D viewColumn( int column );

    public abstract DoubleMatrix2D viewSlice( int slice );

    /**
     * @param s String
     * @return double[]
     */
    public double[][] getRowByName( R row ) {
        return getRow( getRowIndexByName( row ) );
    }

    /**
     * @param s String
     * @return double[]
     */
    public double[][] getColumnByName( C col ) {
        return getColumn( getColIndexByName( col ) );
    }

    public double[][] getSliceByName( S slice ) {
        return getSlice( getSliceIndexByName( slice ) );
    }

    /**
     * @param j
     * @param i
     * @param c
     */
    public abstract void setQuick( int slice, int row, int column, double c );

    public String toString() {
        int slices = this.slices();
        int rows = this.rows();
        int columns = this.columns();
        StringBuffer buf = new StringBuffer();
        for ( int i = 0; i < slices; i++ ) {
            buf.append( "Slice\t" + getSliceName( i ) );
            buf.append( "Row\\Col" );
            for ( int j = 0; j < columns; j++ ) {
                buf.append( "\t" + this.getColName( j ) );
            }
            buf.append( "\n" );
            for ( int k = 0; k < rows; k++ ) {

                buf.append( this.getRowName( k ) );
                for ( int j = 0; j < columns; j++ ) {
                    buf.append( "\t" + this.get( i, k, j ) );
                }
                buf.append( "\n" );
                if ( k > MAX_ROWS_TO_PRINT ) {
                    buf.append( "...\n" );
                    break;
                }
            }
            if ( i > MAX_SLICES_TO_PRINT ) {
                buf.append( "...\n" );
                break;
            }
        }
        return buf.toString();
    }
}