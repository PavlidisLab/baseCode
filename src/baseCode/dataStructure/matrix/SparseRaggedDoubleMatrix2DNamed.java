package baseCode.dataStructure.matrix;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Vector;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;

/**
 * A sparse matrix class where the rows are ragged.
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

      columns = max ;
      System.err.println( "Computed columns: " + columns );
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
    * (non-Javadoc)
    *  Note that in a sparse matrix, zero values are considered "missing"!
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
      String result = "";
      if ( this.hasColNames() || this.hasRowNames() ) {
         result = "label";
      }

      if ( this.hasColNames() ) {
         for ( int i = 0; i < columns(); i++ ) {
            result = result + "\t" + getColName( i );
         }
         result += "\n";
      }

      for ( int i = 0; i < rows(); i++ ) {
         if ( this.hasRowNames() ) {
            result += getRowName( i );
         }
         for ( int j = 0; j < columns(); j++ ) {

            double value = get( i, j );

            if ( value == 0.0 ) {
               result = result + "\t";
            } else {

               result = result + "\t" + nf.format( value );
            }
         }
         result += "\n";
      }
      return result;
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
      //return ( DoubleArrayList ) matrix.get( row );
      DoubleArrayList returnVal = new DoubleArrayList();
      ( ( DoubleMatrix1D ) matrix.get( row ) ).getNonZeros( new IntArrayList(),
            returnVal );
      return returnVal;
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.dataStructure.matrix.AbstractNamedDoubleMatrix#getRowMatrix1D(int)
    */
   public DoubleMatrix1D getRowMatrix1D( int i ) {
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
    * @param values
    * @param indexes
    */
   public void addRow( String name, DoubleArrayList values, IntArrayList ind ) {

      DoubleMatrix1D rowToAdd = new RCDoubleMatrix1D( values, ind );
       
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

 

}