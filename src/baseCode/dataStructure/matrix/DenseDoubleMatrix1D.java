package baseCode.dataStructure.matrix;


/**
 * Use this class when fast iteration over the matrix is of primary interest. The primary change
 * is that getQuick is faster, and toArray returns the actual elements, not a copy. The latter
 * is an important change to the contract of DouleMatrix1D, the former might have some unintended consequences
 * because it hasn't been tested thoroughly.
 *
 * <hr>
 * <p>Copyright (c) 2004 Columbia University
 * @author pavlidis
 * @version $Id$
 */
public class DenseDoubleMatrix1D extends
      cern.colt.matrix.impl.DenseDoubleMatrix1D {

   /**
    * @param size
    * @param elements
    * @param zero
    * @param stride
    */
   protected DenseDoubleMatrix1D( int size, double[] elements, int zero, int stride ) {
      super( size, elements, zero, stride );
   }

   /**
    * @param values
    */
   public DenseDoubleMatrix1D( double[] values ) {
      super( values );
   }

   /**
    * @param size
    */
   public DenseDoubleMatrix1D( int size ) {
      super( size );
   }
   
  
 
   /**
    * This is an optimized version of getQuick. Implementation note: the superclass uses an add and a multiply.
    * This is faster, but there might be unforseen consequences...
    * 
    * @see cern.colt.matrix.DoubleMatrix1D#getQuick(int)
    */
   public double getQuick(int i) {
      return elements[i];
   }
   
  
   /**
    * WARNING unlike the superclass, this returns the actual underlying array, not a copy.
    * 
    * @return the elements
    * @see cern.colt.matrix.DoubleMatrix1D#toArray()
    */
   public double[] toArray() {
      return elements;
   }
   
}
