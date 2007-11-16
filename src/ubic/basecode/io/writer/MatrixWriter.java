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
package ubic.basecode.io.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.Format;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cern.colt.matrix.ObjectMatrix2D;

import ubic.basecode.dataStructure.matrix.AbstractNamedMatrix;
import ubic.basecode.dataStructure.matrix.AbstractNamedMatrix3D;

/**
 * Class for writing matrices to disk
 * 
 * @author Raymond Lim
 * @version $Id$
 */
public class MatrixWriter {
	public static final String DEFAULT_SEP = "\t";
	public static final String DEFAULT_TOP_LEFT = "";
	protected Writer out;
	protected Format formatter;
	protected String sep;

	protected String topLeft;
	
	protected Map sliceNameMap;
	protected Map colNameMap;
	protected Map rowNameMap;

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

	/**
	 * Writes a 3d matrix, collapsing the rows and columns
	 * 
	 * @param matrix
	 * @param printNames
	 * @throws IOException
	 */
	public void writeMatrix(AbstractNamedMatrix3D matrix,
			boolean printNames) throws IOException {
		if (printNames) {
			StringBuffer buf = new StringBuffer(topLeft);
			for (Iterator it = matrix.getSliceNameIterator(); it.hasNext();) {
				Object sliceObj = it.next();
				String sliceName = sliceObj.toString();
				if (sliceNameMap != null && sliceNameMap.get(sliceObj) != null)
					sliceName = sliceNameMap.get(sliceObj).toString();
				buf.append(sep);
				buf.append(sliceName);
			}
			buf.append("\n");
			out.write(buf.toString());
			if (flush) out.flush();
		}
		for (int i = 0; i < matrix.rows(); i++) {
			for (int j = 0; j < matrix.columns(); j++) {
				Object rowObj = matrix.getRowName(i);
				Object colObj = matrix.getColName(j);
				String rowName = rowObj.toString();
				String colName = colObj.toString();
				if (rowNameMap != null && rowNameMap.get(rowObj) != null)
					rowName = rowNameMap.get(rowObj).toString();
				if (colNameMap != null && colNameMap.get(colObj) != null)
					colName = colNameMap.get(colObj).toString();
				String name = rowName + ":" + colName;
				
				StringBuffer buf = new StringBuffer();
				if (printNames)
					buf.append(name + sep);
				for (int k = 0; k < matrix.slices(); k++) {
					Object val = matrix.getObj(k, i, j);
					if (val != null) {
						String s = val.toString();
						if (formatter != null)
							s = formatter.format(val);
						buf.append(s);
					}
					if (k + 1 < matrix.slices())
						buf.append(sep);
				}
				buf.append("\n");
				out.write(buf.toString());
				if (flush) out.flush();
			}
		}
	}

	public void writeMatrix(AbstractNamedMatrix matrix, boolean printNames)
			throws IOException {
		// write headers
		StringBuffer buf = new StringBuffer(topLeft);
		if (printNames) {
			for (Iterator it = matrix.getColNames().iterator(); it.hasNext();) {
				Object colName = it.next();
				buf.append(sep);
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
			buf = new StringBuffer();
			if (printNames)
				buf.append(rowName + sep);
			for (Iterator colIt = matrix.getColNames().iterator(); colIt
					.hasNext();) {
				Object colName = colIt.next();
				int colIndex = matrix.getColIndexByName(colName);
				Object val = matrix.getObj(rowIndex, colIndex);
				if (val != null) {
					String s = val.toString();
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

	public Map getSliceNameMap() {
		return sliceNameMap;
	}

	public void setSliceNameMap(Map sliceNameMap) {
		this.sliceNameMap = sliceNameMap;
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
}
