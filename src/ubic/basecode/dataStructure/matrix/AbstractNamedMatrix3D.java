package ubic.basecode.dataStructure.matrix;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class AbstractNamedMatrix3D<T> implements NamedMatrix3D<T> {
	private LinkedHashMap<Object, Integer> colMap;
	private LinkedHashMap<Object, Integer> rowMap;
	private LinkedHashMap<Object, Integer> sliceMap;
	private List colNames;
	private List rowNames;
	private List sliceNames;
	
	public AbstractNamedMatrix3D() {
		colMap = new LinkedHashMap<Object, Integer>();
		rowMap = new LinkedHashMap<Object, Integer>();
		sliceMap = new LinkedHashMap<Object, Integer>();
		colNames = new ArrayList();
		rowNames = new ArrayList();
		sliceNames = new ArrayList();
	}

	public final void addColumnName(Object s, int index) {
		if (colNames.contains(s))
			return;
		colMap.put(s, new Integer(index));
		colNames.add(s);
	}

	public final void addRowName(Object s, int index) {
		if (rowNames.contains(s))
			return;
		rowMap.put(s, new Integer(index));
		rowNames.add(s);

	}

	public final void addSliceName(Object s, int index) {
		if (sliceNames.contains(s))
			return;
		sliceMap.put(s, new Integer(index));
		sliceNames.add(s);
	}

	public abstract int columns();

	public final boolean containsColumnName(Object columnName) {
		return colMap.containsKey(columnName);
	}

	public final boolean containsRowName(Object rowName) {
		return rowMap.containsKey(rowName);
	}

	public final boolean containsSliceName(Object sliceName) {
		return sliceMap.containsKey(sliceName);
	}

	public abstract Object[] getCol(int slice, int col);

	public final int getColIndexByName(Object s) {
		Integer index = colMap.get(s);
		if (index == null)
			throw new IllegalArgumentException(s + " not found");
		return index.intValue();
	}

	public Object getColName(int i) {
		return colNames.get(i);
	}

	public Iterator getColNameIterator() {
		return colNames.iterator();
	}

	public List getColNames() {
		return colNames;
	}

	public abstract Object[] getRow(int slice, int row);

	public int getRowIndexByName(Object s) {
		Integer index = rowMap.get(s);
		if (index == null)
			throw new IllegalArgumentException(s + " not found");
		return index.intValue();
	}

	public Object getRowName(int i) {
		return rowNames.get(i);
	}

	public Iterator getRowNameIterator() {
		return rowMap.keySet().iterator();
	}

	public List getRowNames() {
		return rowNames;
	}

	public abstract T[] getCol(T[] a, int slice, int col);
	public abstract T[] getRow(T[] a, int slice, int row);
	public abstract T[][] getSlice(T[][] a, int slice);
	public abstract Object[][] getSlice(int slice);

	public int getSliceIndexByName(Object s) {
		Integer index = sliceMap.get(s);
		if (index == null)
			throw new IllegalArgumentException(s + " not found");
		return index.intValue();
	}

	public Object getSliceName(int i) {
		return sliceNames.get(i);
	}

	public Iterator getSliceNameIterator() {
		return sliceNames.iterator();
	}

	public List getSliceNames() {
		return sliceNames;
	}

	public boolean hasColNames() {
		return columns() == colNames.size();
	}

	public boolean hasRow(Object r) {
		return rowMap.containsKey(r);
	}

	public boolean hasRowNames() {
		return rows() == rowNames.size();
	}

	public boolean hasSliceNames() {
		return slices() == sliceNames.size();
	}

	public abstract boolean isMissing(int slice, int row, int col);

	public abstract int numMissing();

	public abstract int rows();

	public abstract void set(int slice, int row, int col, T val);
	
	public void setColumnNames(List v) {
		colNames = v;
		for (int i = 0 ; i < v.size(); i++)
			colMap.put(v.get(i), new Integer(i));
	}

	public void setRowNames(List v) {
		rowNames = v;
		for (int i = 0; i < v.size(); i++) 
			rowMap.put(v.get(i), new Integer(i));
	}
	
	public void setSliceNames(List v) {
		sliceNames = v;
		for (int i = 0; i < v.size(); i++)
			sliceMap.put(v.get(i), new Integer(i));
	}

	public abstract int slices();
	
	public abstract T get(int slice, int row, int col);

}
