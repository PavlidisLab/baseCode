package baseCode.dataStructure.matrix;

import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;

/**
 * 
 *
 * <hr>
 * <p>Copyright (c) 2004 Columbia University
 * @author pavlidis
 * @version $Id$
 */
public abstract class AbstractNamedDoubleMatrix extends AbstractNamedMatrix {

   /* (non-Javadoc)
    * @see baseCode.dataStructure.matrix.NamedMatrix#rows()
    */
   public abstract int rows() ;

   /* (non-Javadoc)
    * @see baseCode.dataStructure.matrix.NamedMatrix#columns()
    */
   public abstract int columns();

   /* (non-Javadoc)
    * @see baseCode.dataStructure.matrix.NamedMatrix#set(int, int, java.lang.Object)
    */
   public abstract void set( int i, int j, Object val );

   /* (non-Javadoc)
    * @see baseCode.dataStructure.matrix.NamedMatrix#getRowObj(int)
    */
   public abstract Object[] getRowObj( int i );

   /* (non-Javadoc)
    * @see baseCode.dataStructure.matrix.NamedMatrix#getColObj(int)
    */
   public abstract Object[] getColObj( int i );

   /* (non-Javadoc)
    * @see baseCode.dataStructure.matrix.NamedMatrix#isMissing(int, int)
    */
   public abstract boolean isMissing( int i, int j );

   public abstract double[] getRow(int i);
   
   public abstract DoubleArrayList getRowArrayList(int i);
   
   public abstract DoubleMatrix1D getRowMatrix1D(int i);
   
}
