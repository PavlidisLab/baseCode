package baseCode.dataStructure;

import java.text.*;
import java.util.*;

import cern.colt.matrix.impl.*;

/**
  @author Paul Pavlidis
  @version $Id$
 */
public class SparseDoubleMatrix2DNamed
    extends SparseDoubleMatrix2D
    implements NamedMatrix {

   private Vector rowNames;
   private Vector colNames;
   private Map rowMap; //contains a map of each row and elements in the row
   private Map colMap;
   /**
    */
   public SparseDoubleMatrix2DNamed(int rows, int cols, double T[][]) {
      super(T);
      rowMap = new HashMap(); //contains a map of each row name to index of the row.
      colMap = new HashMap();
      rowNames = new Vector();
      colNames = new Vector();
   }

   /**
    */
   public SparseDoubleMatrix2DNamed(int rows, int cols) {
      super(rows, cols);
      rowMap = new LinkedHashMap(); //contains a map of each row name to index of the row.
      rowNames = new Vector();
      colNames = new Vector();

   }

   /**
    * Return a reference to a specific row.
    * @param row
    * @return
    */
   public double[] getRow(int row) {
      return viewRow(row).toArray();
   }

   /**
    *
    * @return
    */
   public Iterator getRowNameMapIterator() {
      return this.rowMap.keySet().iterator();
   }

   /**
    * Return a copy of a given column.
    * @param i
    * @return
    */
   public double[] getCol(int col) {
      double[] result = new double[rows()];
      for (int i = 0; i < rows(); i++) {
         result[i] = get(i, col);
      }
      return result;
   }

   /**
    *
    * @param r
    * @return
    */
   public boolean hasRow(String r) {
      return this.rowMap.containsKey(r);
   }

   /**
    *
    * @return
    */
   public String toString() {
      NumberFormat nf = NumberFormat.getInstance();
      String result = "";
      if (this.hasColNames() || this.hasRowNames()) {
         result = "label";
      }

      if (this.hasColNames()) {
         for (int i = 0; i < columns(); i++) {
            result = result + "\t" + getColName(i);
         }
         result += "\n";
      }

      for (int i = 0; i < rows(); i++) {
         if (this.hasRowNames()) {
            result += getRowName(i);
         }
         for (int j = 0; j < columns(); j++) {
            result = result + "\t" + nf.format(get(i, j));
         }
         result += "\n";
      }
      return result;
   }

   /**
    *
    * @param s
    */
   public void addColumnName(String s) {
      this.colNames.add(s);
      this.colMap.put(s, new Integer(columns()));
   }

   /**
    *
    * @param s
    */
   public void addRowName(String s) {
      this.rowNames.add(s);
      this.rowMap.put(s, new Integer(rows()));
   }

   /**
    *
    * @param s
    * @return
    */
   public int getRowIndexByName(String s) {
      return ( (Integer) rowMap.get(s)).intValue();
   }

   /**
    *
    * @param r
    * @return
    */
   public int getColIndexByName(String r) {
      return ( (Integer)this.colMap.get(r)).intValue();
   }

   /**
    *
    * @param s
    * @return
    */
   public double[] getRowByName(String s) {
      return getRow(getRowIndexByName(s));
   }

   /**
    *
    * @param i
    * @return
    */
   public String getRowName(int i) {
      return (String) rowNames.get(i);
   }

   /**
    *
    * @param i
    * @return
    */
   public String getColName(int i) {
      return (String) colNames.get(i);
   }

   /**
    *
    * @return
    */
   public boolean hasRowNames() {
      return rowNames.size() == rows();
   }

   /**
    *
    * @return
    */
   public boolean hasColNames() {
      return colNames.size() == columns();
   }

   /**
    *
    * @param v
    */
   public void setRowNames(Vector v) {
      for (int i = 0; i < v.size(); i++) {
         addRowName( (String) v.get(i));
      }
   }

   /**
    *
    * @param v
    */
   public void setColumnNames(Vector v) {
      this.colNames = v;
   }

   /**
    *
    * @return
    */
   public Vector getColNames() {
      return colNames;
   }
}
