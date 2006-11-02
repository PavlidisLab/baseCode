package ubic.basecode.dataStructure.matrix;

import java.text.NumberFormat;

import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

public class CompressedSparseDoubleMatrix2DNamed extends DoubleMatrixNamed implements NamedMatrix {
    private FlexCompRowMatrix matrix;

    /**
     * @param rows int
     * @param cols int
     */
    public CompressedSparseDoubleMatrix2DNamed( int rows, int cols ) {
        super();
        matrix = new FlexCompRowMatrix( rows, cols );
    }
    
    public void set( int row, int col, Object value ) {
        set( row, col, ( ( Double ) value ).doubleValue() );
    }

    /**
     * Return a reference to a specific row.
     * 
     * @param row int
     * @return double[]
     */
    public double[] getRow( int row ) {
        return viewRow( row ).toArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.DoubleMatrixNamed#getColumn(int)
     */
    public double[] getColumn( int col ) {
        double[] result = new double[rows()];
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = get( i, col );
        }
        return result;
    }

    /**
     * Return a copy of a given column.
     * 
     * @param col int
     * @return double[]
     */
    public double[] getCol( int col ) {
        double[] result = new double[rows()];
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = get( i, col );
        }
        return result;
    }

    public Object[] getRowObj( int row ) {
        Double[] result = new Double[columns()];
        for ( int i = 0; i < columns(); i++ ) {
            result[i] = new Double( get( row, i ) );
        }
        return result;
    }

    public Object[] getColObj( int col ) {
        Double[] result = new Double[rows()];
        for ( int i = 0; i < rows(); i++ ) {
            result[i] = new Double( get( i, col ) );
        }
        return result;
    }

    /**
     * @return java.lang.String
     */
    public String toString() {
        NumberFormat nf = NumberFormat.getInstance();
        String result = "";
        if ( this.hasColNames() || this.hasRowNames() ) {
            result = "label";
        }

        if ( this.hasColNames() ) {
            for ( int i = 0; i < columns(); i++ ) {
                result = result + "\t" + getColName( i );
            }
            result += "\n";
        }

        for ( int i = 0; i < rows(); i++ ) {
            if ( this.hasRowNames() ) {
                result += getRowName( i );
            }
            for ( int j = 0; j < columns(); j++ ) {

                double value = get( i, j );

                if ( value == 0.0 ) {
                    result = result + "\t";
                } else {

                    result = result + "\t" + nf.format( value );
                }
            }
            result += "\n";
        }
        return result;
    }

    /**
     * @param s String
     * @return double[]
     */
    public double[] getRowByName( String s ) {
        return getRow( getRowIndexByName( s ) );
    }

    public boolean isMissing( int i, int j ) {
        return Double.isNaN( get( i, j ) );
    }

    /**
     * @return
     */
    public int columns() {
        return matrix.numColumns();
    }

    /**
     * @param row
     * @param column
     * @return
     */
    public double get( int row, int column ) {
        return matrix.get( row, column );
    }

    /**
     * @param row
     * @param column
     * @return
     */
    public double getQuick( int row, int column ) {
        return matrix.get( row, column );
    }

    /**
     * @return
     */
    public int rows() {
        return matrix.numRows();
    }

    /**
     * @param row
     * @param column
     * @param value
     */
    public void set( int row, int column, double value ) {
        matrix.set( row, column, value );
    }

    /**
     * @param row
     * @param column
     * @param value
     */
    public void setQuick( int row, int column, double value ) {
        matrix.set( row, column, value );
    }

    /**
     * @param column
     * @return
     */
    public DoubleMatrix1D viewColumn( int column ) {
    	double[] oneColumn = new double[this.rows()];
    	for(int i = 0; i < matrix.numRows(); i++)
    		oneColumn[i] = this.get(i,column);
    	return new DenseDoubleMatrix1D(oneColumn);
    }

    /**
     * @param row
     * @return
     */
    public DoubleMatrix1D viewRow( int row ) {
    	return new DenseDoubleMatrix1D(matrix.getRow(row).getData());
    }

    /**
     * @return
     */
    public int cardinality() {
    	int total = 0;
    	for(int i = 0; i < matrix.numRows(); i++)
    	{
    		total = total + matrix.getRow(i).getUsed();
    		//System.err.println(matrix.getRow(i).getUsed());
    	}

        return total;
    }

    /**
     * @param minNonZeros
     */
    public void ensureCapacity( int minNonZeros ) {
        
    }

    /**
     * @return
     */
    public int size() {
        return matrix.numColumns()*matrix.numRows();
    }

    /**
     * 
     */
    public void trimToSize() {
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see basecode.dataStructure.matrix.AbstractNamedDoubleMatrix#getRowArrayList(int)
     */
    public DoubleArrayList getRowArrayList( int i ) {
        return new DoubleArrayList( getRow( i ) );
    }


}
