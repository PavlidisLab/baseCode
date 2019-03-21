/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.dataStructure.matrix;

import cern.colt.function.DoubleFunction;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * A row-compressed 1D matrix. The only deviation from the contract of DoubleMatrix1D is in apply(), which only operates
 * on the non-empty (0) elements. This implementation has a highly optimized dot product computer. If you need to
 * compute the dot product of a RCDoubleMatrix1D with another DoubleMatrix1D, call zDotProduct on this, not on the
 * other. This is because getQuick() and setQuick() are not very fast for this.
 * 
 * @author pavlidis
 * 
 */
public class RCDoubleMatrix1D extends DoubleMatrix1D {

    private static final long serialVersionUID = -5883440011745758929L;
    protected IntArrayList indexes;
    protected DoubleArrayList values;

    /**
     * @param values
     */
    public RCDoubleMatrix1D( double[] values ) {
        assign( values );
    }

    /**
     * @param length
     */
    public RCDoubleMatrix1D( int length ) {
        this.indexes = new IntArrayList();
        this.values = new DoubleArrayList();
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

    @Override
    public DoubleMatrix1D assign( double[] v ) {
        this.indexes = new IntArrayList();
        this.values = new DoubleArrayList();
        for ( int i = 0; i < v.length; i++ ) {
            if ( v[i] == 0 || Double.isNaN( v[i] ) ) {
                continue;
            }
            this.indexes.add( i );
            this.values.add( v[i] );
        }
        this.size = v.length;
        return this;
    }

    /**
     * WARNING this only assigns to the non-empty values, for performance reasons. If you need to assign to any index,
     * you have to use another way.
     * 
     * @see cern.colt.matrix.DoubleMatrix1D#assign(cern.colt.function.DoubleFunction)
     */
    @Override
    public DoubleMatrix1D assign( DoubleFunction function ) {
        for ( int i = values.size(); --i >= 0; ) {
            values.set( i, function.apply( values.get( i ) ) );
        }
        return this;
    }

    @Override
    public double get( int index ) {
        return this.getQuick( index );
    }

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleMatrix1D#getQuick(int)
     */
    @Override
    public double getQuick( int index ) {

        int location = indexes.binarySearch( index );

        if ( location >= 0 ) {
            return values.get( location );
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleMatrix1D#like(int)
     */
    @Override
    public DoubleMatrix1D like( int s ) {
        return new RCDoubleMatrix1D( s );
    }

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleMatrix1D#like2D(int, int)
     */
    @Override
    public DoubleMatrix2D like2D( int rows, int columns ) {
        return new DenseDoubleMatrix2D( rows, columns );
    }

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleMatrix1D#setQuick(int, double)
     */
    @Override
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        int used = 0;
        StringBuilder buf = new StringBuilder();
        for ( int i = 0; i < indexes.size(); i++ ) {
            int index = indexes.get( i );
            while ( index > used ) {
                buf.append( " 0" );
                used++;
            }
            buf.append( " " + values.get( i ) );
        }
        return buf.toString();

    }

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleMatrix1D#zDotProduct(cern.colt.matrix.DoubleMatrix1D)
     */
    @Override
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

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleMatrix1D#zSum()
     */
    @Override
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
     * @see cern.colt.matrix.DoubleMatrix1D#viewSelectionLike(int[])
     */
    @Override
    protected DoubleMatrix1D viewSelectionLike( int[] offsets ) {
        throw new UnsupportedOperationException(); // should never be called
    }
}