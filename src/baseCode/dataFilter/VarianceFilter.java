package baseCode.dataFilter;

import baseCode.dataStructure.DenseDoubleMatrix2DNamed;

/**
 * Remove matrix rows that have a low variance.
 * <p> Copyright (c) 2004</p>
 * <p>Institution: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */
public class VarianceFilter
    extends AbstractFilter
    implements Filter {

   public DenseDoubleMatrix2DNamed filter( DenseDoubleMatrix2DNamed data ) {

      int numRows = data.rows();
      int numCols = data.columns();

      //    DoubleArrayList stdevs = MatrixRowStats.

      return null;
   }

}
