package baseCode.dataFilter;

import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.NamedMatrix;

/**
 * Remove matrix rows that have a low variance.
 * <p> Copyright (c) 2004</p>
 * <p>Institution: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 * @todo implement me
 */
public class VarianceFilter
    extends AbstractFilter
    implements Filter {

   public NamedMatrix filter( NamedMatrix data ) {

      if (!(data instanceof DenseDoubleMatrix2DNamed)) {
         throw new IllegalArgumentException("Only valid for DenseDoubleMatrix2DNamed");
      }
      
      
      int numRows = data.rows();
      int numCols = data.columns();
      for ( int i = 0; i < numRows; i++ ) {

      }
      //    DoubleArrayList stdevs = MatrixRowStats.

      return null;
   }

}
