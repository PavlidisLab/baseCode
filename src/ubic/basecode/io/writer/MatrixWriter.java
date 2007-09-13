package ubic.basecode.io.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.Format;
import java.util.Iterator;
import java.util.Map;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.ObjectMatrix2D;
import cern.colt.matrix.impl.AbstractMatrix;
import cern.colt.matrix.impl.AbstractMatrix2D;

import no.uib.cipr.matrix.Matrix;

import ubic.basecode.dataStructure.matrix.AbstractNamedMatrix;

/**
 * Class for writing matrices to disk
 * 
 * @author Raymond Lim
 * 
 */
public class MatrixWriter {
	public static final String DEFAULT_SEP = "\t";
	public static final String DEFAULT_TOP_LEFT = "";
	protected Writer out;
	protected Format formatter;
	protected String sep;

	protected String topLeft;

	protected Map rowNameMap;
	protected Map colNameMap;
	private boolean flush;

	public MatrixWriter(Writer out, Format formatter, String sep, boolean flush) {
		this.out = out;
		this.formatter = formatter;
		this.sep = sep;
		this.flush = flush;
		this.topLeft = DEFAULT_TOP_LEFT;
	}

	public MatrixWriter(OutputStream out) {
		this(out, null, DEFAULT_SEP, false);
	}

	public MatrixWriter(OutputStream out, boolean flush) {
		this(out, null, DEFAULT_SEP, flush);
	}

	public MatrixWriter(OutputStream out, Format formatter) {
		this(out, formatter, DEFAULT_SEP, false);
	}

	public MatrixWriter(OutputStream out, Format formatter, String sep,
			boolean flush) {
		this(new BufferedWriter(new OutputStreamWriter(out)), formatter, sep,
				flush);
	}

	public MatrixWriter(Writer out, String sep, boolean flush) {
		this(out, null, sep, flush);
	}

	public MatrixWriter(Writer out, Format formatter) {
		this(out, formatter, DEFAULT_SEP, false);
	}

	public MatrixWriter(String fileName, Format formatter, String sep)
			throws IOException {
		this(fileName, formatter, sep, false);
	}

	public MatrixWriter(String fileName, Format formatter) throws IOException {
		this(fileName, formatter, DEFAULT_SEP, false);
	}

	public MatrixWriter(String fileName, Format formatter, Map rowNameMap,
			Map colNameMap) throws IOException {
		this(fileName, formatter, DEFAULT_SEP, false);
		setRowNameMap(rowNameMap);
		setColNameMap(colNameMap);
	}

	public MatrixWriter(String fileName, Format formatter, String sep,
			boolean flush) throws IOException {
		this(new BufferedWriter(new FileWriter(fileName)), formatter, sep,
				flush);
	}

	public MatrixWriter(Writer out, boolean flush) {
		this(out, DEFAULT_SEP, flush);
	}

	public MatrixWriter(Writer out, String sep) {
		this(out, sep, false);
	}

	public MatrixWriter(Writer out) {
		this(out, DEFAULT_SEP, false);
	}

	public void writeMatrix(AbstractNamedMatrix matrix, boolean printNames)
			throws IOException {
		// write headers
		StringBuffer buf = new StringBuffer(topLeft);
		if (printNames) {
			for (Iterator it = matrix.getColNames().iterator(); it.hasNext();) {
				Object colName = it.next();
				buf.append(sep);
				if (colNameMap != null && colNameMap.get(colName) != null)
					colName = colNameMap.get(colName);
				buf.append(colName);
			}
			buf.append("\n");
			out.write(buf.toString());
			if (flush)
				out.flush();
		}

		for (Iterator rowIt = matrix.getRowNames().iterator(); rowIt.hasNext();) {
			Object rowName = rowIt.next();
			int rowIndex = matrix.getRowIndexByName(rowName);
			if (rowNameMap != null && rowNameMap.get(rowName) != null)
				rowName = rowNameMap.get(rowName);
			if (printNames)
				buf = new StringBuffer(rowName + sep);
			else
				buf = new StringBuffer();
			for (Iterator colIt = matrix.getColNames().iterator(); colIt
					.hasNext();) {
				Object colName = colIt.next();
				int colIndex = matrix.getColIndexByName(colName);
				Object val = matrix.getObj(rowIndex, colIndex);
				if (val != null) {
					String s = val.toString();
					// replace using the valMap after formatting
					if (formatter != null)
						s = formatter.format(val);
					buf.append(s);
				}
				if (colIt.hasNext())
					buf.append(sep);
			}
			buf.append("\n");
			out.write(buf.toString());
			if (flush)
				out.flush();
		}
	}

	public void writeMatrix(ObjectMatrix2D matrix) throws IOException {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < matrix.rows(); i++) {
			for (int j = 0; j < matrix.columns(); j++) {
				Object o = matrix.get(i, j);
				if (o != null) {
					String s = o.toString();
					if (formatter != null)
						s = formatter.format(o);
					buf.append(s);
				}
				if (j != matrix.columns() - 1)
					buf.append(sep);
			}

			buf.append("\n");
			out.write(buf.toString());
			if (flush)
				out.flush();
		}

	}

	public void close() throws IOException {
		out.close();
	}

	public Map getColNameMap() {
		return colNameMap;
	}

	public void setColNameMap(Map colNameMap) {
		this.colNameMap = colNameMap;
	}

	public Map getRowNameMap() {
		return rowNameMap;
	}

	public void setRowNameMap(Map rowNameMap) {
		this.rowNameMap = rowNameMap;
	}

	public String getTopLeft() {
		return topLeft;
	}

	public void setTopLeft(String topLeft) {
		this.topLeft = topLeft;
	}

	public String getSep() {
		return sep;
	}

	public void setSep(String sep) {
		this.sep = sep;
	}
}
