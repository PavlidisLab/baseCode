/**
 * 
 */
package ubic.basecode.dataStructure.matrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

/**
 * @author xwan
 *
 */
public class CompressedNamedBitMatrix extends AbstractNamedMatrix {
	
    private FlexCompRowMatrix[] matrix;
	/* (non-Javadoc)
	 * @see ubic.basecode.dataStructure.matrix.AbstractNamedMatrix#columns()
	 */
    public static int DOUBLE_LENGTH = 64;
    private int total_bits_per_item;
    private int rows = 0, cols = 0;
    //static long BIT1 = 0x8000000000000000L;
    public static long BIT1 = 0x0000000000000001L;
        
    public CompressedNamedBitMatrix(int rows, int cols, int total_bits_per_item){
    	super();
    	int num = (int)(total_bits_per_item/CompressedNamedBitMatrix.DOUBLE_LENGTH) + 1;
    	matrix = new FlexCompRowMatrix[num];
    	for(int i = 0; i < num; i++)
    		matrix[i] = new FlexCompRowMatrix( rows, cols );
    	this.total_bits_per_item = total_bits_per_item;
    	this.rows = rows;
    	this.cols = cols;
    }
    public int getBitNum(){
    	return this.total_bits_per_item;
    }
    public long[] getAllBits(int row, int col){
    	long[] allBits = new long[this.matrix.length];
    	for(int i = 0; i < this.matrix.length; i++)
    		allBits[i] = Double.doubleToRawLongBits(this.matrix[i].get(row,col));
    	return allBits;
    }
    public void set(int rows, int cols, int index){
    	if(index >= this.total_bits_per_item || rows > this.rows || cols > this.cols) return;
    	int num = (int)(index/CompressedNamedBitMatrix.DOUBLE_LENGTH);
    	int bit_index = index%CompressedNamedBitMatrix.DOUBLE_LENGTH;
    	long binVal = Double.doubleToRawLongBits(matrix[num].get(rows,cols));
    	double res = Double.longBitsToDouble(binVal | CompressedNamedBitMatrix.BIT1 << bit_index);
    	matrix[num].set(rows, cols, res);
    }
    
    public boolean check(int rows, int cols, int index){
    	if(index >= this.total_bits_per_item || rows > this.rows || cols > this.cols) return false;
    	int num = (int)(index/CompressedNamedBitMatrix.DOUBLE_LENGTH);
    	int bit_index = index%CompressedNamedBitMatrix.DOUBLE_LENGTH;
    	long binVal = Double.doubleToRawLongBits(matrix[num].get(rows,cols));
    	long res = binVal & CompressedNamedBitMatrix.BIT1 << bit_index;
    	if(res == 0) return false;
    	return true;
    }
    static public int countBits(double val){
        long binVal = Double.doubleToRawLongBits(val);
        return countBits(binVal);
    }
    static public int countBits(long val){
        int bitCount = 0;
        for(int i = 0; i <CompressedNamedBitMatrix.DOUBLE_LENGTH; i++){
            long res = val & CompressedNamedBitMatrix.BIT1 << i;
            if(res != 0) bitCount ++;
        }
        return bitCount;
    }

    public int bitCount(int rows, int cols){
        int bits = 0;
        if(rows > this.rows || cols > this.cols) return bits;
        for(int i = 0; i < this.matrix.length; i++){
            double val = this.matrix[i].get(rows,cols);
            if(val != 0)
                bits = bits + countBits(val);
        }
        return bits; 
    }


    public int overlap(int row1, int col1, int row2, int col2){
        int bits = 0;
        for(int i = 0; i < this.matrix.length; i++){
            double val1 = this.matrix[i].get( row1,col1);
            double val2 = this.matrix[i].get( row2,col2);
            if(val1 == 0 || val2 == 0) continue;
            long binVal1 = Double.doubleToRawLongBits(val1);
            long binVal2 = Double.doubleToRawLongBits(val2);
            bits = bits + countBits(binVal1&binVal2);
        }
        return bits;
    }

	public int columns() {
		// TODO Auto-generated method stub
    	return this.cols;
	}

	/* (non-Javadoc)
	 * @see ubic.basecode.dataStructure.matrix.AbstractNamedMatrix#getColObj(int)
	 */
	public Object[] getColObj(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ubic.basecode.dataStructure.matrix.AbstractNamedMatrix#getRowObj(int)
	 */
	public Object[] getRowObj(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ubic.basecode.dataStructure.matrix.AbstractNamedMatrix#isMissing(int, int)
	 */
	public boolean isMissing(int i, int j) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see ubic.basecode.dataStructure.matrix.AbstractNamedMatrix#rows()
	 */
	public int rows() {
		// TODO Auto-generated method stub
		return this.rows;
	}

	/* (non-Javadoc)
	 * @see ubic.basecode.dataStructure.matrix.AbstractNamedMatrix#set(int, int, java.lang.Object)
	 */
	public void set(int i, int j, Object val) {
		// TODO Auto-generated method stub

	}
    public boolean set(int i, int j, double[] val) {
        // TODO Auto-generated method stub
        if(val.length != this.matrix.length || i >= this.rows || j >= this.cols) return false;
        for(int mi = 0; mi < val.length; mi++)
            this.matrix[mi].set( i,j,val[mi] );
        return true;
    }
	public boolean toFile(String fileName){
        try{
            FileWriter out = new FileWriter(new File(fileName));
            out.write(this.rows+"\t"+this.cols+"\t"+this.total_bits_per_item+"\n");
            Object [] rowNames = this.getRowNames().toArray();
            for(int i = 0; i < rowNames.length; i++){
                out.write( rowNames[i].toString() );
                if(i != rowNames.length - 1) out.write( "\t" );
            }
            out.write( "\n" );
            Object [] colNames = this.getColNames().toArray();
            for(int i = 0; i < colNames.length; i++){
                out.write( colNames[i].toString() );
                if(i != colNames.length - 1) out.write( "\t" );
            }
            out.write( "\n" );
            for(int i = 0; i < this.rows; i++)
                for(int j = 0; j < this.cols; j++){
                    if(this.bitCount( i, j ) != 0){
                        out.write(i+"\t"+j);
                        for(int k = 0; k < this.matrix.length; k++){
                            long binVal = Double.doubleToRawLongBits(this.matrix[k].get(i,j));
                            /*Long.parseLong( hexString, 16) to get it back;*/
                            String hexString = Long.toHexString( binVal );
                            out.write( "\t"+ hexString);
                        }
                        out.write( "\n" );
                    }
                }
            out.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
