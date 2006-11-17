/**
 * 
 */
package ubic.basecode.dataStructure.matrix;

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
    private static int DOUBLE_LENGTH = 64;
    private int total_bits_per_item;
    private int rows = 0, cols = 0;
    //static long BIT1 = 0x8000000000000000L;
    static long BIT1 = 0x0000000000000001L;
        
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
    public int bitCount(int rows, int cols){
    	int bits = 0;
    	if(rows > this.rows || cols > this.cols) return bits;
    	for(int i = 0; i < this.total_bits_per_item; i++){
    		if(check(rows, cols, i)) bits++;
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

}
