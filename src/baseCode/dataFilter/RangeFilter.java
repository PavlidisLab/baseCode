package baseCode.dataFilter;

import cern.colt.list.DoubleArrayList;
import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.NamedMatrix;

/**
 * Remove rows from a matrix that have a low difference between minimum and maximum values.
 * <p> Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 * @todo implement me
 */
public class RangeFilter
    extends AbstractFilter
    implements Filter {

   
   public NamedMatrix filter( NamedMatrix data ) {
      
      
      if (!(data instanceof DenseDoubleMatrix2DNamed)) {
         throw new IllegalArgumentException("Only valid for DenseDoubleMatrix2DNamed");
      }
      
      int numRows = data.rows();
      int numCols = data.columns();
      
      DoubleArrayList minLevel = new DoubleArrayList( numRows );
      DoubleArrayList maxLevel = new DoubleArrayList( numRows );
      
      for ( int i = 0; i < numRows; i++ ) {

      }
      
      return null;
   }
}
