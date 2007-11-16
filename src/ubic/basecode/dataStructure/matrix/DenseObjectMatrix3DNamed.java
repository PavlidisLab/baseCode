package ubic.basecode.dataStructure.matrix;

import java.util.List;

import cern.colt.matrix.impl.DenseObjectMatrix3D;

public class DenseObjectMatrix3DNamed extends AbstractNamedMatrix3D {
	private DenseObjectMatrix3D matrix;

	public DenseObjectMatrix3DNamed(int slices, int rows, int columns) {
		super();
		matrix = new DenseObjectMatrix3D(slices, rows, columns);
	}
	
	public DenseObjectMatrix3DNamed(List sliceNames, List rowNames, List colNames) {
		super();
		setRowNames(rowNames);
		setColumnNames(colNames);
		setSliceNames(sliceNames);
		matrix = new DenseObjectMatrix3D(sliceNames.size(), rowNames.size(), colNames.size());
	}

	public DenseObjectMatrix3DNamed(Object[][][] data, List sliceNames,
			List rowNames, List colNames) {
		super();
		matrix = new DenseObjectMatrix3D(data);
		setRowNames(rowNames);
		setColumnNames(colNames);
		setSliceNames(sliceNames);
	}
	
	public DenseObjectMatrix3DNamed(Object[][][] data) {
		super();
		matrix = new DenseObjectMatrix3D(data);
	}

	public int columns() {
		return matrix.columns();
	}

	public Object[] getCol(int slice, int col) {
		Object[] colObjs = new Object[rows()];
		for (int i = 0; i < rows(); i++)
			colObjs[i] = matrix.get(slice, i, col);
		return colObjs;
	}
	public Object[] getRow(int slice, int row) {
		Object[] rowObjs = new Object[columns()];
		for (int i = 0; i < columns(); i++)
			rowObjs[i] = matrix.get(slice, row, i);
		return rowObjs;
	}

	public Object[][] getSlice(int slice) {
		Object[][] sliceObjs = new Object[rows()][columns()];
		for (int i = 0; i < rows(); i++)
			for (int j = 0; j < columns(); j++)
				sliceObjs[i][j] = matrix.get(slice, i, j);
		return sliceObjs;
	}

	public boolean isMissing(int slice, int row, int col) {
		return slice < slices() || row < rows() || col < columns()
				|| matrix.get(slice, row, col) == null;
	}

	public int numMissing() {
		int num = 0;
		for (int i = 0; i < slices(); i++)
			for (int j = 0; j < rows(); j++)
				for (int k = 0; k < columns(); k++)
					if (isMissing(i, j, k))
						num++;
		return num;
	}

	public int rows() {
		return matrix.rows();
	}

	public void set(int slice, int row, int col, Object val) {
		matrix.set(slice, row, col, val);

	}

	public int slices() {
		return matrix.slices();
	}
	
	public Object get(int slice, int row, int col) {
		return matrix.get(slice, row, col);
	}
	
	public Object getObj(int slice, int row, int col) {
		return get(slice, row, col);
	}

}
