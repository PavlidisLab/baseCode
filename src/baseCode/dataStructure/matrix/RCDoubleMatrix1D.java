package baseCode.dataStructure.matrix;

import cern.colt.function.DoubleFunction;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;

import cern.colt.matrix.impl.RCDoubleMatrix2D;

/**
 * A row-compressed 1D matrix. The only deviation from the contract of DoubleMatrix1D is in apply(), which only operates
 * on the non-empty elements. This implementation has a highly optimized dot product computer. If you need to compute
 * the dot product of a RCDoubleMatrix1D with another DoubleMatrix1D, call zDotProduct on this, not on the other. This
 * is because getQuick() and setQuick() are not very fast for this.
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class RCDoubleMatrix1D extends DoubleMatrix1D {

   protected OpenIntIntHashMap map; // todo may need to find a more efficient way to store all this.
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
      this.indexes = new IntArrayList( length );
      this.map = new OpenIntIntHashMap( length );
      this.values = new DoubleArrayList( length );
   }

   /**
    * @param map
    */
   public RCDoubleMatrix1D( OpenIntIntHashMap map, DoubleArrayList values,
         int size ) {
      setUp( size );
      this.map = map;
      this.values = values;
      this.indexes = map.keys();
      indexes.sort();
   }

   /*
    * (non-Javadoc)
    * 
    * @see cern.colt.matrix.DoubleMatrix1D#getQuick(int)
    */
   public double getQuick( int index ) {
      if ( map.containsKey( index ) ) {
         return values.get( map.get( index ) );
      }
      return 0.0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see cern.colt.matrix.DoubleMatrix1D#like(int)
    */
   public DoubleMatrix1D like( int s ) {
      return new RCDoubleMatrix1D( s );
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
      if ( map.containsKey( column ) ) {
         values.set( map.get( column ), value );
      } else {
         map.put( column, values.size() );
         values.add( value );
         indexes.add( column );
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

   /**
    * Apply the given function to each element non-zero element in the matrix.
    * 
    * @param function
    * @return
    */
   public DoubleMatrix1D forEachNonZero(
         final cern.colt.function.DoubleFunction function ) {

      double[] elements = values.elements();
      for ( int i = elements.length; --i >= 0; ) {
         elements[i] = function.apply( elements[i] );
      }
      return this;

   }

   /*
    * (non-Javadoc)
    * 
    * @see cern.colt.matrix.DoubleMatrix1D#zDotProduct(cern.colt.matrix.DoubleMatrix1D)
    */
   public double zDotProduct( DoubleMatrix1D y ) {

      int[] idx = indexes.elements();
      double[] el = values.elements();
      double[] other = y.toArray();
      double returnVal = 0.0;
      int otherSize = y.size();
      for ( int i = idx.length; --i >= 0; ) {
         int index = idx[i];
         if ( index >= otherSize ) continue; // in case our arrays are ragged.
         returnVal += el[i] * other[index];
      }
      return returnVal;
   }

   /**
    * WARNING this only even assigns to the non-empty values, for performanc reasons. If you need to assign to any
    * index, you have to use another way.
    * 
    * @see cern.colt.matrix.DoubleMatrix1D#assign(cern.colt.function.DoubleFunction)
    */
   public DoubleMatrix1D assign( DoubleFunction function ) {

      double[] elements = values.elements();
      for ( int i = elements.length; --i >= 0; ) {
         elements[i] = function.apply( elements[i] );
      }
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see cern.colt.matrix.DoubleMatrix1D#zSum()
    */
   public double zSum() {
      double sum = 0.0;
      double[] elements = values.elements();
      for ( int i = elements.length; --i >= 0; ) {
         sum += elements[i];
      }
      return sum;
   }
}