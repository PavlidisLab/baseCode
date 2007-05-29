/**
 * 
 */
package ubic.basecode.dataStructure.matrix;

import java.io.File;
import java.io.FileWriter;

import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;

/**
 * @author xwan
 */
public class CompressedNamedBitMatrix extends AbstractNamedMatrix {

    /**
     * 
     */
    private static final long serialVersionUID = 1775002416710933373L;
    private FlexCompRowMatrix[] matrix;
    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.AbstractNamedMatrix#columns()
     */
    public static int DOUBLE_LENGTH = 63; //java doesn't support unsigned long.
    private int totalBitsPerItem;
    private int rows = 0, cols = 0;
    public static long BIT1 = 0x0000000000000001L;

    /**
     * @param rows
     * @param cols
     * @param totalBitsPerItem
     */
    public CompressedNamedBitMatrix( int rows, int cols, int totalBitsPerItem ) {
        super();
        int num = ( int ) ( totalBitsPerItem / CompressedNamedBitMatrix.DOUBLE_LENGTH ) + 1;
        matrix = new FlexCompRowMatrix[num];
        for ( int i = 0; i < num; i++ )
            matrix[i] = new FlexCompRowMatrix( rows, cols );
        this.totalBitsPerItem = totalBitsPerItem;
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * @return
     */
    public int getBitNum() {
        return this.totalBitsPerItem;
    }

    /**
     * @param row
     * @param col
     * @return
     */
    public long[] getAllBits( int row, int col ) {
        long[] allBits = new long[this.matrix.length];
        for ( int i = 0; i < this.matrix.length; i++ )
            allBits[i] = Double.doubleToRawLongBits( this.matrix[i].get( row, col ) );
        return allBits;
    }

    /**
     * @param rows
     * @param cols
     * @param index
     */
    public void set( int rows, int cols, int index ) {
        if ( index >= this.totalBitsPerItem || rows > this.rows || cols > this.cols ) return;
        int num = ( int ) ( index / CompressedNamedBitMatrix.DOUBLE_LENGTH );
        int bit_index = index % CompressedNamedBitMatrix.DOUBLE_LENGTH;
        long binVal = Double.doubleToRawLongBits( matrix[num].get( rows, cols ) );
        double res = Double.longBitsToDouble( binVal | CompressedNamedBitMatrix.BIT1 << bit_index );
        matrix[num].set( rows, cols, res );
    }
    
    public void reset( int rows, int cols) {
        for ( int i = 0; i < this.matrix.length; i++ )
        	this.matrix[i].set(rows,cols,0);
    }


    /**
     * @param rows
     * @param cols
     * @param index
     * @return
     */
    public boolean check( int rows, int cols, int index ) {
        if ( index >= this.totalBitsPerItem || rows > this.rows || cols > this.cols ) return false;
        int num = ( int ) ( index / CompressedNamedBitMatrix.DOUBLE_LENGTH );
        int bit_index = index % CompressedNamedBitMatrix.DOUBLE_LENGTH;
        long binVal = Double.doubleToRawLongBits( matrix[num].get( rows, cols ) );
        long res = binVal & CompressedNamedBitMatrix.BIT1 << bit_index;
        if ( res == 0 ) return false;
        return true;
    }

    /**
     * @param val
     * @return
     */
    static public int countBits( double val ) {
    	if(val == 0.0) return 0;
        long binVal = Double.doubleToRawLongBits( val );
        return Long.bitCount( binVal );
    }
    
    public int[] getRowBits(int row, int[] bits){
    	for(int i = 0; i < this.matrix.length; i++){
    		SparseVector vector = this.matrix[i].getRow(row);
    		double[] data = vector.getData();
    		int[] indices = vector.getIndex();
	    	for(int j = 0; j < data.length; j++){
	    		if(indices[j] == 0 && j > 0) break;
	    		if(data[j] != 0.0)
	    			bits[indices[j]] = bits[indices[j]] + countBits(data[j]);
	    	}
    	}
    	return bits;
    }
    /**
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
     * @param row1
     * @param col1
     * @param row2
     * @param col2
     * @return
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

    public int columns() {
        return this.cols;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.AbstractNamedMatrix#getColObj(int)
     */
    public Object[] getColObj( int i ) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.AbstractNamedMatrix#getRowObj(int)
     */
    public Object[] getRowObj( int i ) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.dataStructure.matrix.AbstractNamedMatrix#isMissing(int, int)
     */
    public boolean isMissing( int i, int j ) {
        // TODO Auto-generated method stub
        return false;
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
        // TODO Auto-generated method stub

    }

    /**
     * @param i
     * @param j
     * @param val
     * @return
     */
    public boolean set( int i, int j, double[] val ) {
        // TODO Auto-generated method stub
        if ( val.length != this.matrix.length || i >= this.rows || j >= this.cols ) return false;
        for ( int mi = 0; mi < val.length; mi++ )
            this.matrix[mi].set( i, j, val[mi] );
        return true;
    }

    /**
     * @param fileName
     * @return
     */
    public boolean toFile( String fileName ) {
        try {
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
        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
