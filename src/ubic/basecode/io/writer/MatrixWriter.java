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

import ubic.basecode.dataStructure.matrix.AbstractNamedMatrix;

/**
 * Class for writing matrices to disk
 * 
 * @author Raymond Lim
 * 
 */
public class MatrixWriter {
	public static final String DEFAULT_SEP = "\t";
	protected Writer out;
	protected Format formatter;
	protected String sep;
    
    protected Map rowNameMap;
    protected Map colNameMap;
    protected Map valMap;
	private boolean flush;

	public MatrixWriter(Writer out, Format formatter, String sep, boolean flush) {
		this.out = out;
		this.formatter = formatter;
		this.sep = sep;
		this.flush = flush;
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
    
    public MatrixWriter(String fileName, Format formatter, Map rowNameMap, Map colNameMap, Map valMap) throws IOException {
        this(fileName, formatter, DEFAULT_SEP, false);
        setRowNameMap( rowNameMap );
        setColNameMap( colNameMap );
        setValMap(valMap);
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

	public void writeMatrix(AbstractNamedMatrix matrix) throws IOException {
		this.writeMatrix(matrix, "\t");
	}
    

	public void writeMatrix(AbstractNamedMatrix matrix, String topLeft)
			throws IOException {
		// write headers
		StringBuffer buf = new StringBuffer(topLeft);
		for (Iterator it = matrix.getColNames().iterator(); it.hasNext();) {
			Object colName = it.next();
			buf.append(sep);
            if (colNameMap != null && colNameMap.get(colName) != null)
                colName = colNameMap.get( colName );
			buf.append(colName);
		}
		buf.append("\n");
		out.write(buf.toString());
		if (flush)
			out.flush();

		for (Iterator rowIt = matrix.getRowNames().iterator(); rowIt.hasNext();) {
			Object rowName = rowIt.next();
			int rowIndex = matrix.getRowIndexByName(rowName);
            if (rowNameMap != null && rowNameMap.get( rowName ) != null)
                rowName = rowNameMap.get( rowName );
			buf = new StringBuffer(rowName.toString());
			for (Iterator colIt = matrix.getColNames().iterator(); colIt
					.hasNext();) {
				Object colName = colIt.next();
				int colIndex = matrix.getColIndexByName(colName);
				Object val = matrix.getObj(rowIndex, colIndex);
				buf.append(sep);
				if (val != null) {
					String s = val.toString();
					// replace using the valMap after formatting
					if (formatter != null)
						s = formatter.format(val);
					if (valMap != null && valMap.get(s) != null)
						s = valMap.get(s).toString();
					buf.append(s);
				}
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

    public void setColNameMap( Map colNameMap ) {
        this.colNameMap = colNameMap;
    }

    public Map getRowNameMap() {
        return rowNameMap;
    }

    public void setRowNameMap(Map rowNameMap) {
        this.rowNameMap = rowNameMap;
    }

	public Map getValMap() {
		return valMap;
	}

	public void setValMap(Map valMap) {
		this.valMap = valMap;
	}
}
