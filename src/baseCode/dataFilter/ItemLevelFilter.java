package baseCode.dataFilter;

import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.NamedMatrix;

/**
 * Filter that remove individual values that are outside of a range.
 * Removed values are set to NaN.
 * <p>
 * Copyright (c) 2004 Columbia University
 * @author Pavlidis
 * @version $Id$
 */
public class ItemLevelFilter extends AbstractLevelFilter {

   public NamedMatrix filter( NamedMatrix data ) {
      if ( !( data instanceof DenseDoubleMatrix2DNamed ) ) {
         throw new IllegalArgumentException(
               "Only valid for DenseDoubleMatrix2DNamed" );
      }

      if ( lowCut == -Double.MAX_VALUE && highCut == Double.MAX_VALUE ) {
         log.info( "No filtering requested" );
         return data;
      }

      int numRows = data.rows();
      int numCols = data.columns();
      DenseDoubleMatrix2DNamed returnval = new DenseDoubleMatrix2DNamed(
            numRows, numCols );
      for ( int i = 0; i < numRows; i++ ) {

         for ( int j = 0; j < numCols; j++ ) {
            
            double newVal = ((DenseDoubleMatrix2DNamed)data).get(i,j);
            if (newVal < lowCut || newVal > highCut) {
               newVal = Double.NaN;
            }
            
            returnval.set( i, j, newVal );
         }
      }
      returnval.setColumnNames( data.getColNames() );
      returnval.setRowNames( data.getRowNames() );

      return returnval;
   }

}
