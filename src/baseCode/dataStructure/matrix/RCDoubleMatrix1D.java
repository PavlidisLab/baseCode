package baseCode.dataStructure.matrix;

import cern.colt.function.DoubleFunction;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.RCDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class RCDoubleMatrix1D extends DoubleMatrix1D {

   protected IntArrayList indexes;
   protected DoubleArrayList values;

   public RCDoubleMatrix1D( double[] values ) {
      this( values.length );
      assign( values );
   }

   /**
    * @param length
    */
   public RCDoubleMatrix1D( int length ) {
      setUp( length );
      this.indexes = new IntArrayList();
      this.values = new DoubleArrayList();
   }

   /**
    * @param values
    * @param ind
    */
   public RCDoubleMatrix1D( DoubleArrayList values, IntArrayList ind ) {
      setUp( ind.size() );
      this.indexes = ind;
      this.values = values;

      if ( size > 0 ) {
         this.size = ind.get( ind.size() - 1 ) + 1;
      }

   }

   /*
    * (non-Javadoc)
    * 
    * @see cern.colt.matrix.DoubleMatrix1D#getQuick(int)
    */
   public double getQuick( int index ) {
      int k = indexes.binarySearch( index );
      double v = 0;
      if ( k >= 0 ) v = values.getQuick( k );
      return v;
   }

   /*
    * (non-Javadoc)
    * 
    * @see cern.colt.matrix.DoubleMatrix1D#like(int)
    */
   public DoubleMatrix1D like( int size ) {
      return new SparseDoubleMatrix1D( size );
   }

   /*
    * (non-Javadoc)
    * 
    * @see cern.colt.matrix.DoubleMatrix1D#like2D(int, int)
    */
   public DoubleMatrix2D like2D( int rows, int columns ) {
      return new RCDoubleMatrix2D( rows, columns );
   }

   /*
    * (non-Javadoc)
    * 
    * @see cern.colt.matrix.DoubleMatrix1D#setQuick(int, double)
    */
   public void setQuick( int column, double value ) {
      int k = indexes.binarySearch( column );
      if ( k >= 0 ) { // found
         if ( value == 0 )
            remove( k );
         else
            values.setQuick( k, value );
         return;
      }

      if ( value != 0 ) {
         k = -k - 1;
         insert( column, k, value );
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see cern.colt.matrix.DoubleMatrix1D#viewSelectionLike(int[])
    */
   protected DoubleMatrix1D viewSelectionLike( int[] offsets ) {
      throw new UnsupportedOperationException(); // should never be called
   }

   protected void remove( int index ) {
      indexes.remove( index );
      values.remove( index );
   }

   protected void insert( int column, int index, double value ) {
      indexes.beforeInsert( index, column );
      values.beforeInsert( index, value );
   }

   /**
    * Apply the given function to each element non-zero element in the matrix.
    * 
    * @param function
    * @return
    */
   public DoubleMatrix1D forEachNonZero(
         final cern.colt.function.IntDoubleFunction function ) {
      int[] idx = indexes.elements();
      double[] vals = values.elements();

      for ( int i = idx.length; --i >= 0; ) {
         double value = vals[i];
         double r = function.apply( i, value );
         if ( r != value ) vals[i] = r;
      }
      return this;
   }

   public double zDotProduct( DoubleMatrix1D y ) {
      int[] idx = indexes.elements();
      double[] vals = values.elements();
      int otherSize = y.size();
      double returnVal = 0.0;
      for ( int i = idx.length; --i >= 0; ) {
         int index = idx[i];
         if ( index >= otherSize ) continue; // in case our arrays are ragged.
         returnVal +=  vals[i] * y.getQuick( index );
      }
      return returnVal;
   }

   /**
    * todo This isn't really right - assign should operate on 0 values too, I think.
    */
   public DoubleMatrix1D assign( DoubleFunction function ) {
      int[] idx = indexes.elements();
      double[] vals = values.elements();

      for ( int i = idx.length; --i >= 0; ) {
         double value = vals[i];
         double r = function.apply( value );
         if ( r != value ) vals[i] = r;
      }
      return this;
   }


   public double zSum() {
      double[] vals = values.elements();
      double sum = 0.0;
      for ( int i = 0; i < vals.length; i++ ) {
         sum += vals[i];

      }
      return sum;
   }
}