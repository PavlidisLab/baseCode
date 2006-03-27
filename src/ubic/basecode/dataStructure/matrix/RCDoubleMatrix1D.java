package ubic.basecode.dataStructure.matrix;

import cern.colt.function.DoubleFunction;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * A row-compressed 1D matrix. The only deviation from the contract of DoubleMatrix1D is in apply(), which only operates
 * on the non-empty elements. This implementation has a highly optimized dot product computer. If you need to compute
 * the dot product of a RCDoubleMatrix1D with another DoubleMatrix1D, call zDotProduct on this, not on the other. This
 * is because getQuick() and setQuick() are not very fast for this.
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class RCDoubleMatrix1D extends DoubleMatrix1D {

    protected IntArrayList indexes;
    protected DoubleArrayList values;

    /**
     * @param values
     */
    public RCDoubleMatrix1D( double[] values ) {
        this( values.length );
        assign( values );
    }

    /**
     * @param length
     */
    public RCDoubleMatrix1D( int length ) {
        setUp( length );
        this.indexes = new IntArrayList( length );
        this.values = new DoubleArrayList( length );
    }

    /**
     * @param indexes These MUST be in sorted order.
     * @param values These MuST be in the same order as the indexes, meaning that indexes[0] is the column for
     *        values[0].
     */
    public RCDoubleMatrix1D( IntArrayList indexes, DoubleArrayList values ) {
        int k = indexes.size();
        int s = 0;
        if ( k > 0 ) {
            s = indexes.get( k - 1 ) + 1;
        }

        setUp( s );
        this.indexes = indexes;
        this.values = values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleMatrix1D#getQuick(int)
     */
    public double getQuick( int index ) {

        int location = indexes.binarySearch( index );

        if ( location >= 0 ) {
            return values.get( location );
        }
        return 0.0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleMatrix1D#like(int)
     */
    public DoubleMatrix1D like( int s ) {
        return new RCDoubleMatrix1D( s );
    }

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleMatrix1D#like2D(int, int)
     */
    public DoubleMatrix2D like2D( int rows, int columns ) {
        return new DenseDoubleMatrix2D( rows, columns );
    }

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleMatrix1D#setQuick(int, double)
     */
    public void setQuick( int column, double value ) {
        int location = indexes.binarySearch( column );

        if ( location >= 0 ) {
            values.set( location, value ); // just change the value
        } else {
            location = -location - 1; // e.g., -1 means to insert just before 0.
            indexes.beforeInsert( location, column ); // add this column, so the order is still right.
            values.beforeInsert( location, value ); // keep this in the same order.
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleMatrix1D#viewSelectionLike(int[])
     */
    protected DoubleMatrix1D viewSelectionLike( int[] offsets ) {
        throw new UnsupportedOperationException(); // should never be called
    }

    /**
     * Apply the given function to each element non-zero element in the matrix.
     * 
     * @param function
     * @return
     */
    public DoubleMatrix1D forEachNonZero( final cern.colt.function.DoubleFunction function ) {

        double[] elements = values.elements();
        for ( int i = elements.length; --i >= 0; ) {
            elements[i] = function.apply( elements[i] );
        }
        return this;

    }

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleMatrix1D#zDotProduct(cern.colt.matrix.DoubleMatrix1D)
     */
    public double zDotProduct( DoubleMatrix1D y ) {

        int[] idx = indexes.elements();
        double[] el = values.elements();
        double[] other = y.toArray();
        double returnVal = 0.0;
        int otherSize = y.size();
        for ( int i = idx.length; --i >= 0; ) {
            int index = idx[i];
            if ( index >= otherSize ) continue; // in case our arrays are ragged.
            returnVal += el[i] * other[index];
        }
        return returnVal;
    }

    /**
     * WARNING this only even assigns to the non-empty values, for performance reasons. If you need to assign to any
     * index, you have to use another way.
     * 
     * @see cern.colt.matrix.DoubleMatrix1D#assign(cern.colt.function.DoubleFunction)
     */
    public DoubleMatrix1D assign( DoubleFunction function ) {

        double[] elements = values.elements();
        for ( int i = elements.length; --i >= 0; ) {
            elements[i] = function.apply( elements[i] );
        }
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleMatrix1D#zSum()
     */
    public double zSum() {
        double sum = 0.0;
        double[] elements = values.elements();
        for ( int i = elements.length; --i >= 0; ) {
            sum += elements[i];
        }
        return sum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return new cern.colt.matrix.doublealgo.Formatter().toString( this );
    }
}