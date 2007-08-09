package ubic.basecode.dataStructure.matrix;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class AbstractNamedMatrix3D implements NamedMatrix3D {
	public LinkedHashMap colMap;
	public LinkedHashMap rowMap;
	public LinkedHashMap sliceMap;
	public List colNames;
	public List rowNames;
	public List sliceNames;
	
	public AbstractNamedMatrix3D() {
		colMap = new LinkedHashMap();
		rowMap = new LinkedHashMap();
		sliceMap = new LinkedHashMap();
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

	public final int getColIndexByName(Object s) {
		Integer index = (Integer) colMap.get(s);
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

	public int getRowIndexByName(Object s) {
		Integer index = (Integer) rowMap.get(s);
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

	public int getSliceIndexByName(Object s) {
		Integer index = (Integer) sliceMap.get(s);
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
	
}
