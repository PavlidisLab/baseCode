package ubic.basecode.dataStructure.matrix;

import java.util.Iterator;
import java.util.List;

public interface NamedMatrix3D {
	/**
	 * Add a slice name
	 * @param s name of the slice
	 * @param index
	 */
	public void addSliceName(Object s, int index);

	/**
	 * Get a slice index
	 * @param s name
	 * @return slice index
	 */
	public int getSliceIndexByName(Object s);
	
	public void setSliceNames(List v);

	/**
	 * Get a slice name
	 * @param i index
	 * @return slice name
	 */
	public Object getSliceName(int i);

	public boolean hasSliceNames();

	public List getSliceNames();

	public int slices();

	/**
	 * Add a column name associated with an index.
	 * 
	 * @param s
	 *            a column name
	 * @param index
	 *            int the column index associated with this name
	 */
	public void addColumnName(Object s, int index);

	/**
	 * Add a row name associated with a row index.
	 * 
	 * @param s
	 *            row name
	 * @param index
	 */
	public void addRowName(Object s, int index);

	/**
	 * Get the index of a row by name.
	 * 
	 * @param s
	 *            name
	 * @return row index
	 */
	public int getRowIndexByName(Object s);

	/**
	 * Get the index of a column by name.
	 * 
	 * @param s
	 *            name
	 * @return column index
	 */
	public int getColIndexByName(Object s);

	/**
	 * Get the row name for an index
	 * 
	 * @param i
	 *            row index
	 * @return name of the row
	 */
	public Object getRowName(int i);

	/**
	 * Get the column name for an index.
	 * 
	 * @param i
	 *            column index
	 * @return column name
	 */
	public Object getColName(int i);

	/**
	 * @return boolean
	 */
	public boolean hasRowNames();

	/**
	 * Check if this matrix has a valid set of column names.
	 * 
	 * @return boolean
	 */
	public boolean hasColNames();

	/**
	 * @param v
	 *            List a vector of Strings.
	 */
	public void setRowNames(List v);

	/**
	 * @param v
	 *            List a vector of Strings.
	 */
	public void setColumnNames(List v);

	/**
	 * @return List of Object
	 */
	public List getColNames();

	/**
	 * @return List of Object
	 */
	public List getRowNames();
	
	/**
	 * @param r
	 *            row name
	 * @return whether the row exists
	 */
	public boolean hasRow(Object r);

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getRowNameIterator();

	public Iterator getColNameIterator();

	public Iterator getSliceNameIterator();

	/**
	 * Get the number of rows the matrix has
	 * 
	 * @return int
	 */
	public int rows();

	/**
	 * Get the number of columns the matrix has.
	 * 
	 * @return int
	 */
	public int columns();

	/**
	 * Set a value in the matrix.
	 * 
	 * @param slice
	 *            index
	 * @param row
	 *            index
	 * @param col
	 *            index
	 * @param value
	 *            to set
	 */
	public void set(int slice, int row, int col, Object val);

	/**
	 * Get a row in the matrix.
	 * 
	 * @param slice
	 *            index
	 * @param column
	 *            index
	 * @return row
	 */
	public Object[] getRow(int slice, int row);
	
	/**
	 * Get a column in the matrix
	 * 
	 * @param slice
	 *            index
	 * @param column
	 *            index
	 * @return column
	 */
	public Object[] getCol(int slice, int col);
	
	/**
	 * Get a slice of the matrix
	 * 
	 * @param slice
	 *            index
	 * @return a slice
	 */
	public Object[][] getSlice(int slice);
	
	/**
	 * Check if the value at a given index is missing.
	 * 
	 * @param slice
	 * @param row
	 * @param column
	 * @return true if the value is missing, false otherwise.
	 */
	public boolean isMissing(int slice, int row, int column);

	/**
	 * Return the number of missing values in the matrix.
	 * 
	 * @return number missing
	 */
	public int numMissing();

	/**
	 * Check if the matrix contains a row name
	 * 
	 * @param rowName
	 * @return true if the matrix contains the row name
	 */
	public boolean containsRowName(Object rowName);

	/**
	 * Check if the matrix contains a column name
	 * 
	 * @param colName
	 * @return true if the matrix contains the column name
	 */
	public boolean containsColumnName(Object columnName);

	/**
	 * Check if the matrix contains a slice name
	 * 
	 * @param stripeName
	 * @return true if the matrix contains the slice name
	 */
	public boolean containsSliceName(Object sliceName);
	
	public Object get(int slice, int row, int column);
	
}
