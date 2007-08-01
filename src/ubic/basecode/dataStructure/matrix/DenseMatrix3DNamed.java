package ubic.basecode.dataStructure.matrix;

import java.util.List;

import cern.colt.matrix.impl.DenseObjectMatrix3D;

public class DenseMatrix3DNamed<T> extends AbstractNamedMatrix3D<T> {
	private DenseObjectMatrix3D matrix;

	public DenseMatrix3DNamed(int slices, int rows, int columns) {
		super();
		matrix = new DenseObjectMatrix3D(slices, rows, columns);
	}
	
	public DenseMatrix3DNamed(List sliceNames, List rowNames, List colNames) {
		super();
		setRowNames(rowNames);
		setColumnNames(colNames);
		setSliceNames(sliceNames);
		matrix = new DenseObjectMatrix3D(sliceNames.size(), rowNames.size(), colNames.size());
	}

	public DenseMatrix3DNamed(T[][][] data, List sliceNames,
			List rowNames, List colNames) {
		super();
		matrix = new DenseObjectMatrix3D(data);
		setRowNames(rowNames);
		setColumnNames(colNames);
		setSliceNames(sliceNames);
	}
	
	public DenseMatrix3DNamed(T[][][] data) {
		super();
		matrix = new DenseObjectMatrix3D(data);
	}

	@Override
	public int columns() {
		return matrix.columns();
	}

	@Override
	public Object[] getCol(int slice, int col) {
		Object[] colObjs = new Object[rows()];
		for (int i = 0; i < rows(); i++)
			colObjs[i] = matrix.get(slice, i, col);
		return colObjs;
	}

	@Override
	public T[] getCol(T[] a, int slice, int col) {
		for (int i = 0; i < rows(); i++)
			a[i] = (T) matrix.get(slice, i, col);
		return a;
	}

	@Override
	public Object[] getRow(int slice, int row) {
		Object[] rowObjs = new Object[columns()];
		for (int i = 0; i < columns(); i++)
			rowObjs[i] = matrix.get(slice, row, i);
		return rowObjs;
	}

	@Override
	public T[] getRow(T[] a, int slice, int row) {
		for (int i = 0; i < columns(); i++)
			a[i] = (T) matrix.get(slice, row, i);
		return a;
	}

	@Override
	public Object[][] getSlice(int slice) {
		Object[][] sliceObjs = new Object[rows()][columns()];
		for (int i = 0; i < rows(); i++)
			for (int j = 0; j < columns(); j++)
				sliceObjs[i][j] = matrix.get(slice, i, j);
		return sliceObjs;
	}

	@Override
	public T[][] getSlice(T[][] a, int slice) {
		for (int i = 0; i < rows(); i++)
			for (int j = 0; j < columns(); j++)
				a[i][j] = (T) matrix.get(slice, i, j);
		return a;
	}

	@Override
	public boolean isMissing(int slice, int row, int col) {
		return slice < slices() || row < rows() || col < columns()
				|| matrix.get(slice, row, col) == null;
	}

	@Override
	public int numMissing() {
		int num = 0;
		for (int i = 0; i < slices(); i++)
			for (int j = 0; j < rows(); j++)
				for (int k = 0; k < columns(); k++)
					if (isMissing(i, j, k))
						num++;
		return num;
	}

	@Override
	public int rows() {
		return matrix.rows();
	}

	@Override
	public void set(int slice, int row, int col, T val) {
		matrix.set(slice, row, col, val);

	}

	@Override
	public int slices() {
		return matrix.slices();
	}

	public T get(int slice, int row, int col) {
		return (T) matrix.get(slice, row, col);
	}
}
