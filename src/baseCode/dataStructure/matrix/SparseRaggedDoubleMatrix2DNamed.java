package baseCode.dataStructure.matrix;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Vector;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;

/**
 * A sparse matrix class where the rows are ragged and compressed.
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class SparseRaggedDoubleMatrix2DNamed extends AbstractNamedDoubleMatrix {

   private Vector matrix; // a vector of DoubleArrayList containing the values of the matrix

   int columns = 0;
   private boolean isDirty = true;

   public SparseRaggedDoubleMatrix2DNamed() {
      matrix = new Vector();
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.dataStructure.matrix.NamedMatrix#rows()
    */
   public int rows() {
      return matrix.size();
   }

   /*
    * (non-Javadoc) Unfortunately this has to iterate over the entire array.
    * 
    * @see baseCode.dataStructure.matrix.NamedMatrix#columns()
    */
   public int columns() {

      if ( !isDirty ) {
         return columns;
      }

      int max = 0;
      for ( Iterator iter = matrix.iterator(); iter.hasNext(); ) {
         DoubleMatrix1D element = ( DoubleMatrix1D ) iter.next();

         int value = element.size();
         if ( value > max ) {
            max = value;
         }

      }

      columns = max;
      isDirty = false;
      return columns;
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.dataStructure.matrix.NamedMatrix#set(int, int, java.lang.Object)
    */
   public void set( int i, int j, Object val ) {
      set( i, j, ( ( Double ) val ).doubleValue() );
   }

   /**
    * @param i row
    * @param j column
    * @param d value
    */
   public void set( int i, int j, double d ) {

      ( ( DoubleMatrix1D ) matrix.get( i ) ).set( j, d );

   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.dataStructure.matrix.NamedMatrix#getRowObj(int)
    */
   public Object[] getRowObj( int i ) {
      Double[] result = new Double[columns()];

      double[] row = getRow( i );

      for ( int j = 0; j < columns(); j++ ) {
         result[i] = new Double( row[j] );
      }
      return result;
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.dataStructure.matrix.NamedMatrix#getColObj(int)
    */
   public Object[] getColObj( int i ) {
      throw new UnsupportedOperationException();
   }

   /**
    * (non-Javadoc) Note that in a sparse matrix, zero values are considered "missing"!
    * 
    * @see baseCode.dataStructure.matrix.NamedMatrix#isMissing(int, int)
    */
   public boolean isMissing( int i, int j ) {
      return get( i, j ) == 0.0;
   }

   /**
    * @return java.lang.String
    */
   public String toString() {
      NumberFormat nf = NumberFormat.getInstance();
      
      StringBuffer buf = new StringBuffer();
      
      String result = "";
      if ( this.hasColNames() || this.hasRowNames() ) {
         buf.append( "label");
      }

      if ( this.hasColNames() ) {
         for ( int i = 0; i < columns(); i++ ) {
            buf.append( "\t" + getColName( i ));
         }
         buf.append( "\n");
      }

      for ( int i = 0; i < rows(); i++ ) {
         if ( this.hasRowNames() ) {
            buf.append( getRowName( i ));
         }
         for ( int j = 0; j < columns(); j++ ) {

            double value = get( i, j );

            if ( value == 0.0 ) {
               buf.append( "\t");
            } else {
               buf.append( "\t" + nf.format( value ));
            }
         }

         buf.append( "\n");
      }
      return buf.toString();
   }

   /**
    * @param row
    * @param column
    * @return
    */
   public double get( int i, int j ) {
      return ( ( DoubleMatrix1D ) matrix.get( i ) ).getQuick( j );
   }

   /**
    * This gives just the list of values in the row - make sure this is what you want. It does not include the zero
    * values.
    * 
    * @param row
    * @return
    */
   public DoubleArrayList getRowArrayList( int row ) {
      DoubleArrayList returnVal = new DoubleArrayList();
      ( ( DoubleMatrix1D ) matrix.get( row ) ).getNonZeros( new IntArrayList(),
            returnVal );
      return returnVal;
   }


   /*
    *  (non-Javadoc)
    * @see baseCode.dataStructure.matrix.AbstractNamedDoubleMatrix#viewRow(int)
    */
   public DoubleMatrix1D viewRow( int i ) {
      return ( DoubleMatrix1D ) matrix.get( i );
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.dataStructure.matrix.AbstractNamedDoubleMatrix#getRow(int)
    */
   public double[] getRow( int i ) {
      // return getRowMatrix1D( i ).toArray();
      return ( ( DoubleMatrix1D ) matrix.get( i ) ).toArray();
   }

   /**
    * @param name
    * @param indexes
    * @param values
    */
   public void addRow( String name, IntArrayList indexes, DoubleArrayList values ) {
      DoubleMatrix1D rowToAdd = new RCDoubleMatrix1D( indexes, values );

      matrix.add( rowToAdd );
      this.addColumnName( name, matrix.size() - 1 );
      this.addRowName( name, matrix.size() - 1 );
      isDirty = true;
   }

   /**
    * @param matrix1D
    */
   public void addRow( String name, DoubleMatrix1D matrix1D ) {
      matrix.add( matrix1D );
      this.addColumnName( name, matrix.size() - 1 );
      this.addRowName( name, matrix.size() - 1 );
      isDirty = true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.dataStructure.matrix.AbstractNamedDoubleMatrix#getQuick(int, int)
    */
   public double getQuick( int i, int j ) {
      return get( i, j );
   }

}