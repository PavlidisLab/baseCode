package baseCode.dataStructure;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import corejava.Format;

/**
 * <p>Title: DenseDoubleMatrix2DNamed</p>
 * <p>Description: A matrix that knows about row and column names.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
  @author Paul Pavlidis
  @version $Id$
 */
public class DenseDoubleMatrix2DNamed
    extends DenseDoubleMatrix2D
    implements NamedMatrix {

   private Vector rowNames;
   private Vector colNames;
   private Map rowMap; //contains a map of each row and elements in the row
   private Map colMap;

    /**
     *
     * @param rows int
     * @param cols int
     * @param T double[][]
     */
    public DenseDoubleMatrix2DNamed(int rows, int cols, double T[][]) {
      super(T);
      rowMap = new HashMap(); //contains a map of each row name to index of the row.
      colMap = new HashMap();
      rowNames = new Vector();
      colNames = new Vector();

   }

    /**
     *
     * @param rows int
     * @param cols int
     */
    public DenseDoubleMatrix2DNamed(int rows, int cols) {
      super(rows, cols);
      rowMap = new LinkedHashMap(); //contains a map of each row name to index of the row.
      colMap = new HashMap();
      rowNames = new Vector();
      colNames = new Vector();

   }

    /**
     * Return a reference to a specific row.
     *
     * @param row int
     * @return double[]
     */
    public double[] getRow(int row) {
      return viewRow(row).toArray();
   }

    /**
     * Return a copy of a given column.
     *
     * @param col int
     * @return double[]
     */
    public double[] getCol(int col) {
      double[] result = new double[rows()];
      for (int i = 0; i < rows(); i++) {
         result[i] = get(i, col);
      }
      return result;
   }

	public Object[] getRowObj(int row) {
		Double[] result = new Double[columns()];
		for (int i = 0; i < columns(); i++) {
			result[i] = new Double(get(row, i));
		}
		return result;
	}
	
	
	public Object[] getColObj(int col) {
	   Double[] result = new Double[rows()];
	   for (int i = 0; i < rows(); i++) {
		  result[i] = new Double(get(i, col));
	   }
	   return result;
	}


    /**
     *
     * @param r String
     * @return boolean
     */
    public boolean hasRow(String r) {
      return this.rowMap.containsKey(r);
   }

    /**
     *
     * @return java.util.Iterator
     */
    public Iterator getRowNameMapIterator() {
      return this.rowMap.keySet().iterator();
   }

    /**
     *
     * @return java.lang.String
     */
    public String toString() {
      Format nf = new Format("%.4g");
      StringBuffer result = new StringBuffer(this.rows()*this.columns());
      if (this.hasColNames() || this.hasRowNames()) {
         result.append("label");
      }

      if (this.hasColNames()) {
         for (int i = 0; i < columns(); i++) {
            result.append("\t" + getColName(i));
         }
        result.append("\n");
      }

      for (int i = 0; i < rows(); i++) {
         if (this.hasRowNames()) {
            result.append(getRowName(i));
         }
         for (int j = 0; j < columns(); j++) {
            if (Double.isNaN(get(i,j))) {
               result.append("\t");
            } else {
               result.append("\t" + nf.form(get(i, j)));
            }
         }
         result.append("\n");
      }
      return result.toString();
   }

    /**
     *
     * @param s String
     */
    public void addColumnName(String s) {
      this.colNames.add(s);
      this.colMap.put(s, new Integer(columns()));
   }

    /**
     *
     * @param s String
     */
    public void addRowName(String s) {
      this.rowNames.add(s);
      this.rowMap.put(s, new Integer(rows()));
   }

    /**
     *
     * @param s String
     * @return int
     */
    public int getRowIndexByName(String s) {
      return ( (Integer) rowMap.get(s)).intValue();
   }

    /**
     *
     * @param r String
     * @return int
     */
    public int getColIndexByName(String r) {
    return ( (Integer)this.colMap.get(r)).intValue();
 }


    /**
     *
     * @param s String
     * @return double[]
     */
    public double[] getRowByName(String s) {
      return getRow(getRowIndexByName(s));
   }

    /**
     *
     * @param i int
     * @return java.lang.String
     */
    public String getRowName(int i) {
      return (String) rowNames.get(i);
   }

    /**
     *
     * @param i int
     * @return java.lang.String
     */
    public String getColName(int i) {
      return (String) colNames.get(i);
   }

    /**
     *
     * @return boolean
     */
    public boolean hasRowNames() {
      return rowNames.size() == rows();
   }

    /**
     *
     * @return boolean
     */
    public boolean hasColNames() {
      return colNames.size() == columns();
   }

    /**
     *
     * @param v Vector
     */
    public void setRowNames(Vector v) {
      for (int i = 0; i < v.size(); i++) {
         addRowName((String)v.get(i));
      }
   }

    /**
     *
     * @param v Vector
     */
    public void setColumnNames(Vector v) {
      this.colNames = v;
   }

    /**
     *
     * @return java.util.Vector
     */
    public Vector getColNames() {
      return colNames;
   }

   public void set(int row, int col, Object value) {
		   super.set(row, col, ((Double)value).doubleValue());
	  }

    /**
     *
     * @todo make this use copy instead, need to encapsulate the doublematrix2d
     *   instead of inheriting from it.
     * @return baseCode.dataStructure.DenseDoubleMatrix2DNamed
     */
    public DenseDoubleMatrix2DNamed copyMatrix() {
      DenseDoubleMatrix2DNamed returnval = new DenseDoubleMatrix2DNamed(this.rows(), this.columns());
      for (int i = 0, n = this.rows(); i < n; i++) {
         returnval.addRowName(this.getRowName(i));
         for (int j = 0, m = this.columns(); j < m; j++) {
            if (i == 0) {
               returnval.addColumnName(this.getColName(j));
            }
            returnval.set(i,j,this.get(i,j));
         }
      }
      return returnval;
   }

}
