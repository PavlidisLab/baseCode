package baseCode.dataFilter;

import java.text.DecimalFormat;
import java.util.Vector;

import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import cern.colt.list.DoubleArrayList;

/**
 * Remove rows from a matrix that have values too high and/or too low.
 * <p> Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 * @todo implement high level filtering.
 */
public class LevelFilter
    extends AbstractFilter
    implements Filter {

   private double lowCut;
   private double highCut;
   private boolean lowSet = false;
   private boolean highSet = false;
   private boolean useAsFraction = true;
   private boolean removeAllNegative = false; // remove rows that have all negative values IN ADDITION TO as removing rows according to the critier.
   private double background;

   /**
    * Set the low threshold for removal. If not set, no filtering will occur.
    * @param lowCut the threshold
    */
   public void setLowCut( double lowCut ) {
      this.lowCut = lowCut;
      lowSet = true;
   }

   /**
    * Set the high threshold for removal. If not set, no filtering will occur.
    * @param h the threshold
    */
   public void setHighCut( double h ) {
      highCut = h;
      highSet = true;
      throw new UnsupportedOperationException( "High-level filtering not supported yet." );
   }

   /**
    * Set the filter to remove all rows that have only negative values. Default
    * = false.
    *
    * @param t boolean
    */
   public void setRemoveAllNegative( boolean t ) {
      log.info( "Rows with all negative values will be removed PRIOR TO applying the level filter." );
      removeAllNegative = t;
   }

   /**
    * Set the filter to interpret the low and high cuts as fractions; that is,
    * if true, lowcut 0.1 means remove 0.1 of the rows with the lowest values.
    * Otherwise the cuts are interpeted as actual values. Default = false.
    *
    * @param t boolean
    */
   public void setUseAsFraction( boolean t ) {
      useAsFraction = t;
   }

   /**
    *
    *
    * @param data DenseDoubleMatrix2DNamed
    * @return baseCode.dataStructure.DenseDoubleMatrix2DNamed
    * @todo implement high level filtering.
    */
   public DenseDoubleMatrix2DNamed filter( DenseDoubleMatrix2DNamed data ) {
      DecimalFormat fo = new DecimalFormat();

      double realLowCut;
      double realHighCut;

      DoubleArrayList sortedMax;
      Vector MTemp = new Vector();
      Vector rowNames = new Vector();

      int numRows = data.rows();
      int numCols = data.columns();
      DoubleArrayList minLevel = new DoubleArrayList( numRows );
      DoubleArrayList maxLevel = new DoubleArrayList( numRows );
      //  sortedMax = new DoubleArrayList(numRows);

      /* set criteria. As coded here, we use the maximum level of expression of the gene to set a cutpoint. */
      for ( int i = 0; i < numRows; i++ ) {
         double max = -1e20;
         double min = 1e20;

         for ( int j = 0; j < numCols; j++ ) {
            double item = data.get( i, j );
            if ( !Double.isNaN( item ) && item > max ) {
               max = item;
            } else if ( !Double.isNaN( item ) && item < min ) {
               min = item;
            }
         }
         maxLevel.add( max );
         minLevel.add( min ); // don't really need this.
      }
      sortedMax = maxLevel.copy();
      sortedMax.sort();

      if ( useAsFraction ) {
         if ( lowCut > 1.0 || lowCut < 0.0 ) {
            throw new IllegalArgumentException(
                "Illegal value for level cut, must be a fraction between 0 and 1" );
         }

         //        fprintf(stderr, "Removing %f of the genes. For affymetrix arrays, genes with all negative values are filtered out before determining that cut.\n", lowcut);
         if ( removeAllNegative ) {
            // find the first gene that has a positive mean.
            double v;
            int i;
            for ( i = 0; i < numRows; i++ ) {
               v = sortedMax.get( i );
               if ( v > 0 ) {
                  break;
               }
            }
            realLowCut = sortedMax.get( ( int ) Math.ceil( ( double ) ( numRows - i ) * lowCut + i ) );
         } else {
            realLowCut = sortedMax.get( ( int ) Math.ceil( ( ( double ) numRows * lowCut ) ) ); // items with low CVs are removed.
         }
      } else {
         realLowCut = lowCut;
      }

      // go back over the data now using the cutpoint. We could make this more (memory) efficient by not making a copy like this,
      // just storing indices.
      int kept = 0;
      for ( int i = 0; i < numRows; i++ ) {
         if ( maxLevel.get( i ) > realLowCut ) {
            kept++;
            MTemp.add( data.getRow( i ) );
            rowNames.add( data.getRowName( i ) );
         }
      }

      DenseDoubleMatrix2DNamed returnval = new DenseDoubleMatrix2DNamed( MTemp.size(), numCols );
      for ( int i = 0; i < MTemp.size(); i++ ) {
         for ( int j = 0; j < numCols; j++ ) {
            returnval.set( i, j, ( ( double[] ) MTemp.get( i ) )[j] );
         }
      }
      returnval.setColumnNames( data.getColNames() );
      returnval.setRowNames( rowNames );

      log.info(
          "There are " + kept + " rows left after filtering." );

      return ( returnval );

   }

   /**
    * The data is going to be filtered in response to values that are contained
    * in levelData.
    *
    * @param data DenseDoubleMatrix2DNamed
    * @param levelData DenseDoubleMatrix2DNamed
    * @return baseCode.dataStructure.DenseDoubleMatrix2DNamed
    * @todo implement this.
    */
   public DenseDoubleMatrix2DNamed filter( DenseDoubleMatrix2DNamed data,
                                           DenseDoubleMatrix2DNamed levelData ) {
      return null;
   }

   /**
    * The data is going to be filtered in response to values in both levelDataA
    * and B.
    *
    * @param data DenseDoubleMatrix2DNamed
    * @param levelDataA DenseDoubleMatrix2DNamed
    * @param levelDataB DenseDoubleMatrix2DNamed
    * @return baseCode.dataStructure.DenseDoubleMatrix2DNamed
    * @todo implement this.
    */
   public DenseDoubleMatrix2DNamed filter( DenseDoubleMatrix2DNamed data,
                                           DenseDoubleMatrix2DNamed levelDataA,
                                           DenseDoubleMatrix2DNamed levelDataB ) {
      return null;
   }

}
