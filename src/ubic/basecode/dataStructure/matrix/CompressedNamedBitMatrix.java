/*
 * The baseCode project
 * 
 * Copyright (c) 2007 University of British Columbia
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;

/**
 * Named compressed sparse bit matrix. Elements of the matrix are stored in the <code>long</code> data type.
 * 
 * @author xwan
 */
public class CompressedNamedBitMatrix extends AbstractNamedMatrix {

    private static final long serialVersionUID = 1775002416710933373L;

    private FlexCompRowMatrix[] matrix;

    public static int BITS_PER_ELEMENT = Double.SIZE - 1;
    private int totalBitsPerItem;
    private int rows = 0, cols = 0;
    public static long BIT1 = 0x1L;

    /**
     * Constructs a matrix with specified rows, columns, and total bits per element
     * 
     * @param rows - number of rows in the matrix
     * @param cols - number of columns in the matrix
     * @param totalBitsPerItem - the number of bits for each element
     */
    public CompressedNamedBitMatrix( int rows, int cols, int totalBitsPerItem ) {
        super();
        // calculate number of matrices required
        int num = ( int ) Math.floor( ( double ) totalBitsPerItem / BITS_PER_ELEMENT ) + 1;
        matrix = new FlexCompRowMatrix[num];
        for ( int i = 0; i < num; i++ ) {
            matrix[i] = new FlexCompRowMatrix( rows, cols );
        }
        this.totalBitsPerItem = totalBitsPerItem;
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * Returns the total number of bits in a matrix element
     * 
     * @return the number of bits per element
     */
    public int getBitNum() {
        return this.totalBitsPerItem;
    }

    /**
     * Returns all of the bits for an element
     * 
     * @param row - the element row
     * @param col - the element column
     * @return all the bits encoded as an array of <code>longs</code>
     */
    public long[] getAllBits( int row, int col ) {
        long[] allBits = new long[this.matrix.length];
        for ( int i = 0; i < this.matrix.length; i++ )
            allBits[i] = Double.doubleToRawLongBits( this.matrix[i].get( row, col ) );
        return allBits;
    }

    /**
     * Sets the bit of the specified element at the specified index to 1.
     * 
     * @param row - matrix row
     * @param col - matrix column
     * @param index - bit vector index
     */
    public void set( int row, int col, int index ) {
        if ( index >= this.totalBitsPerItem || row > this.rows || col > this.cols ) {
            throw new ArrayIndexOutOfBoundsException( "Attempt to access row=" + row + " col=" + col + " index="
                    + index );
        }

        if ( get( row, col, index ) ) {
            // System.err.println( "Bit row=" + row + " col=" + col + " index=" + index + " is already set to 1" );
            return;
        }

        /*
         * We can only fit BITS_PER_ELEMENT values in each matrix cell. If num > 0, we have multiple 'stacked' matrices.
         */
        int num = ( int ) Math.floor( ( double ) index / BITS_PER_ELEMENT );
        assert num >= 0 && num < matrix.length;
        int whichBitToSet = index % BITS_PER_ELEMENT;
        long currentValue = Double.doubleToRawLongBits( matrix[num].get( row, col ) );
        double res = Double.longBitsToDouble( currentValue | CompressedNamedBitMatrix.BIT1 << whichBitToSet );
        matrix[num].set( row, col, res );
    }

    public void reset( int rows, int cols ) {
        for ( int i = 0; i < this.matrix.length; i++ ) {
            this.matrix[i].set( rows, cols, 0 );
        }
    }

    /**
     * Checks the bit of the specified element at the specified index.
     * 
     * @param row - matrix row
     * @param col - matrix column
     * @param index - bit vector index
     * @return true if bit is 1, false if 0.
     */
    public boolean get( int row, int col, int index ) {
        if ( index >= this.totalBitsPerItem || row > this.rows || col > this.cols )
            throw new ArrayIndexOutOfBoundsException( "Attempt to access row=" + row + " col=" + col + " index="
                    + index );
        int num = ( int ) Math.floor( ( double ) index / CompressedNamedBitMatrix.BITS_PER_ELEMENT );
        int bit_index = index % CompressedNamedBitMatrix.BITS_PER_ELEMENT;
        long binVal = Double.doubleToRawLongBits( matrix[num].get( row, col ) );
        long res = binVal & CompressedNamedBitMatrix.BIT1 << bit_index;
        if ( res == 0 ) return false;
        return true;
    }

    /**
     * Count the number of one-bits of the passed-in <code>double</code> val.
     * 
     * @param val
     * @return number of one-bits in val
     */
    static public int countBits( double val ) {
        if ( val == 0.0 ) return 0;
        long binVal = Double.doubleToRawLongBits( val );
        return Long.bitCount( binVal );
    }

    /**
     * @param row
     * @return - array of counts of one-bits for each element in the row.
     */
    public int[] getRowBitCount( int row ) {
        int[] bits = new int[columns()];
        for ( int i = 0, k = this.matrix.length; i < k; i++ ) {
            SparseVector vector = this.matrix[i].getRow( row );

            /*
             * Sparse vector: has indices (>0 except for first position) saying where values are; data are the values.
             */
            double[] data = vector.getData();
            int[] indices = vector.getIndex();
            for ( int j = 0; j < data.length; j++ ) {
                if ( indices[j] == 0 && j > 0 ) break;
                if ( data[j] != 0.0 ) bits[indices[j]] += countBits( data[j] );
            }
        }
        return bits;
    }

    /**
     * Count the number of one-bits at the specified element position
     * 
     * @param rows
     * @param cols
     * @return
     */
    public int bitCount( int rows, int cols ) {
        int bits = 0;
        if ( rows > this.rows || cols > this.cols ) return bits;
        for ( int i = 0; i < this.matrix.length; i++ ) {
            double val = this.matrix[i].get( rows, cols );
            if ( val != 0 ) bits = bits + countBits( val );
        }
        return bits;
    }

    /**
     * Number of ones in the entire matrix.
     * 
     * @return
     */
    public long totalBitCount() {
        long result = 0L;
        for ( int i = 0, k = this.matrix.length; i < k; i++ ) {
            for ( int j = 0; j < cols; j++ ) {

                SparseVector vector = this.matrix[i].getRow( j );

                /*
                 * Sparse vector: has indices (>0 except for first position) saying where values are; data are the
                 * values.
                 */
                double[] data = vector.getData();
                for ( int m = 0; m < data.length; m++ ) {
                    result += countBits( data[m] );
                }
            }
        }
        return result;
    }

    /**
     * Counts the number of one-bits that are in common between the two specified elements; i.e. performs an AND
     * operation on the two bit vectors and counts the remaining 1 bits.
     * 
     * @param row1 - element 1 row
     * @param col1 - element 1 column
     * @param row2 - element 2 row
     * @param col2 - element 2 column
     * @return number of bits in common
     */
    public int overlap( int row1, int col1, int row2, int col2 ) {
        int bits = 0;
        for ( int i = 0; i < this.matrix.length; i++ ) {
            double val1 = this.matrix[i].get( row1, col1 );
            double val2 = this.matrix[i].get( row2, col2 );
            if ( val1 == 0 || val2 == 0 ) continue;
            long binVal1 = Double.doubleToRawLongBits( val1 );
            long binVal2 = Double.doubleToRawLongBits( val2 );
            bits = bits + countBits( binVal1 & binVal2 );
        }
        return bits;
    }

    /**
     * Return the number of columns in the matrix
     * 
     * @return number of columns
     */
    public int columns() {
        return this.cols;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.AbstractNamedMatrix#getColObj(int)
     */
    public Object[] getColObj( int i ) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.AbstractNamedMatrix#getRowObj(int)
     */
    public Object[] getRowObj( int i ) {
        throw new UnsupportedOperationException();
    }

    public Object getObj( int row, int col ) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.AbstractNamedMatrix#isMissing(int, int)
     */
    public boolean isMissing( int i, int j ) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.AbstractNamedMatrix#rows()
     */
    public int rows() {
        return this.rows;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.AbstractNamedMatrix#set(int, int, java.lang.Object)
     */
    public void set( int i, int j, Object val ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the matrix element to the specified bit vector
     * 
     * @param row
     * @param col
     * @param val
     * @return true if set successfully
     */
    public boolean set( int row, int col, double[] val ) {
        if ( val.length != this.matrix.length || row >= this.rows || col >= this.cols ) return false;
        for ( int mi = 0; mi < val.length; mi++ )
            this.matrix[mi].set( row, col, val[mi] );
        return true;
    }

    /**
     * Save the matrix to the specified file
     * 
     * @param fileName - save file
     */
    public void toFile( String fileName ) throws IOException {
        FileWriter out = new FileWriter( new File( fileName ) );
        out.write( this.rows + "\t" + this.cols + "\t" + this.totalBitsPerItem + "\n" );
        Object[] rowNames = this.getRowNames().toArray();
        for ( int i = 0; i < rowNames.length; i++ ) {
            out.write( rowNames[i].toString() );
            if ( i != rowNames.length - 1 ) out.write( "\t" );
        }
        out.write( "\n" );
        Object[] colNames = this.getColNames().toArray();
        for ( int i = 0; i < colNames.length; i++ ) {
            out.write( colNames[i].toString() );
            if ( i != colNames.length - 1 ) out.write( "\t" );
        }
        out.write( "\n" );
        for ( int i = 0; i < this.rows; i++ )
            for ( int j = 0; j < this.cols; j++ ) {
                if ( this.bitCount( i, j ) != 0 ) {
                    out.write( i + "\t" + j );
                    for ( int k = 0; k < this.matrix.length; k++ ) {
                        long binVal = Double.doubleToRawLongBits( this.matrix[k].get( i, j ) );
                        /* Long.parseLong( hexString, 16) to get it back; */
                        String hexString = Long.toHexString( binVal );
                        out.write( "\t" + hexString );
                    }
                    out.write( "\n" );
                }
            }
        out.close();
    }
}
