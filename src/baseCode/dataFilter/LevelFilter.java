package baseCode.dataFilter;

import java.text.DecimalFormat;
import java.util.Vector;

import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import baseCode.dataStructure.NamedMatrix;
import baseCode.math.DescriptiveWithMissing;
import cern.colt.list.DoubleArrayList;

/**
 * Remove rows from a matrix that have values too high and/or too low.
 * <p>
 * There are a number of decisions/caveats to consider:
 * <h2>Cutpoint determination</h2>
 * <p>
 * There are multiple ways of determining cutpoints. Considering the case of
 * low-cuts, the obvious possibilities are the maximum value, the minimum value,
 * the mean value, or the median value.
 * <h2>Filtering ratiometric data</h2>
 * <p>
 * For data that are normalized or ratios, it does not make sense to use this
 * method on the raw data. In that situation, you should filter the data based
 * on the raw data, and then use a {@link RowNameFilter}to select the rows from
 * the ratio data.
 * 
 * <h2>Negative values</h2>
 * <p>
 * For microarray expression data based on the Affymetrix MAS4.0 protocol (and
 * possibly others), negative values can occur. In some cases all the values can
 * be negative. As these values are generally viewed as non-sensical, one might
 * decide that data rows that are all negative should be filtered.
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * <p>
 * 
 * @author Paul Pavlidis
 * @version $Id$
 * @todo implement high level filtering.
 * @todo implement using mean instead of max
 * @todo implement using median instead of max.
 * @todo implement using min instead of max.
 */
public class LevelFilter extends AbstractFilter implements Filter {

   private double lowCut;
   private double highCut;
   private boolean lowSet = false;
   private boolean highSet = false;
   private boolean useAsFraction = true;
   private boolean removeAllNegative = false;

   public static final String MIN = "min";
   public static final String MAX = "max";
   public static final String MEDIAN = "median";
   public static final String MEAN = "mean";
   private String method = MAX;

   /**
    * Choose the method that will be used for filtering. Default is 'MAX'. Those
    * rows with the lowest values are removed during 'low' filtering.
    * 
    * @param method
    *           one of LevelFilter.MIN, LevelFilter.MAX, LevelFilter.MEDIAN, or
    *           LevelFilter.MEAN.
    */
   public void setMethod( String method ) {
      if ( method.compareTo( MIN ) != 0 && method.compareTo( MAX ) != 0
            && method.compareTo( MEDIAN ) != 0 && method.compareTo( MEAN ) != 0 ) {
         throw new IllegalArgumentException(
               "Unknown filtering method requested" );
      }
      this.method = method;
   }

   /**
    * Set the low threshold for removal. If not set, no filtering will occur.
    * 
    * @param lowCut
    *           the threshold
    */
   private void setLowCut( double lowCut ) {
      this.lowCut = lowCut;
      lowSet = true;
   }

   /**
    * 
    * @param lowCut
    * @param isFraction
    */
   public void setLowCut( double lowCut, boolean isFraction ) {
      if ( isFraction == true && ( lowCut < 0.0 || lowCut > 1.0 ) ) {
         throw new IllegalArgumentException(
               "Value "
                     + lowCut
                     + " for cut is invalid for using as fractions, must be >0.0 and <1.0," );
      }
      setLowCut( lowCut );
      useAsFraction = isFraction;
   }

   /**
    * Set the high threshold for removal. If not set, no filtering will occur.
    * 
    * @param h
    *           the threshold
    */
   private void setHighCut( double h ) {
      highCut = h;
      highSet = true;
      throw new UnsupportedOperationException(
            "High-level filtering not supported yet." );
   }

   /**
    * 
    * @param h
    * @param isFraction
    */
   public void setHighCut( double h, boolean isFraction ) {
      if ( isFraction == true && ( highCut > 1.0 || highCut < 0.0 ) ) {
         throw new IllegalArgumentException(
               "Value for cut is invalid for using as fractions, must be >0.0 and <1.0," );
      }
      setHighCut( h );
      useAsFraction = isFraction;
   }

   /**
    * Set the filter to remove all rows that have only negative values. This is
    * applied BEFORE applying other criteria. In other words, if you request
    * filtering 0.5 of the values, and 0.5 have all negative values, you will
    * get 0.25 of the data back. Default = false.
    * 
    * @param t
    *           boolean
    */
   public void setRemoveAllNegative( boolean t ) {
      log
            .info( "Rows with all negative values will be removed PRIOR TO applying the level filter." );
      removeAllNegative = t;
   }

   /**
    * Set the filter to interpret the low and high cuts as fractions; that is,
    * if true, lowcut 0.1 means remove 0.1 of the rows with the lowest values.
    * Otherwise the cuts are interpeted as actual values. Default = false.
    * 
    * @param t
    *           boolean
    */
   public void setUseAsFraction( boolean t ) {
      if ( t == true
            && ( lowCut < 0.0 || highCut > 1.0 || highCut < 0.0 || lowCut > 1.0 ) ) {
         throw new IllegalArgumentException(
               "Value for cut(s) are invalid for using as fractions, must be >0.0 and <1.0," );
      }
      useAsFraction = t;
   }

   /**
    * 
    * 
    * @param data
    *           DenseDoubleMatrix2DNamed
    * @return baseCode.dataStructure.DenseDoubleMatrix2DNamed
    * @todo implement high level filtering.
    */
   public NamedMatrix filter( NamedMatrix data ) {

      if ( !( data instanceof DenseDoubleMatrix2DNamed ) ) {
         throw new IllegalArgumentException(
               "Only valid for DenseDoubleMatrix2DNamed" );
      }

      if ( highSet ) {
         throw new UnsupportedOperationException(
               "High-level filtering not implemented" );
      }

      if ( !lowSet ) {
         log.info( "No filtering requested" );
         return data;
      }

      DecimalFormat fo = new DecimalFormat();

      double realLowCut;
      double realHighCut;

      int numRows = data.rows();
      int numCols = data.columns();
      DoubleArrayList minLevel = new DoubleArrayList( numRows );
      DoubleArrayList maxLevel = new DoubleArrayList( numRows );
      DoubleArrayList meanLevel = new DoubleArrayList( numRows );
      DoubleArrayList medianLevel = new DoubleArrayList( numRows );
      DoubleArrayList numNegative = new DoubleArrayList( numRows );

      /*
       * compute criteria.
       */
      DoubleArrayList rowList = new DoubleArrayList( new double[numCols] );
      int numAllNeg = 0;
      for ( int i = 0; i < numRows; i++ ) {
         Double[] row = ( Double[] ) data.getRowObj( i );
         int numNeg = 0;
         /* stupid, copy into a DoubleArrayList */
         for ( int j = 0; j < numCols; j++ ) {
            double item = row[j].doubleValue();
            rowList.set( j, item );
            if ( item < 0.0 || Double.isNaN( item ) ) {
               numNeg++;
            }
         }
         if ( numNeg == numRows ) {
            numAllNeg++;
         }

         /**
          * @todo make this select among the needed values instead of computing
          *       them all
          */
         meanLevel.add( DescriptiveWithMissing.mean( rowList ) );
         medianLevel.add( DescriptiveWithMissing.median( rowList ) );
         maxLevel.add( DescriptiveWithMissing.max( rowList ) );
         minLevel.add( DescriptiveWithMissing.min( rowList ) );
         numNegative.add( numNeg );
      }

      DoubleArrayList sortedCriterion;

      if ( method.compareTo( MEAN ) == 0 ) {
         sortedCriterion = meanLevel.copy(); // remove those with lowest mean
      } else if ( method.compareTo( MEDIAN ) == 0 ) {
         sortedCriterion = medianLevel.copy(); // remove those with lowest
         // median
      } else if ( method.compareTo( MAX ) == 0 ) {
         sortedCriterion = maxLevel.copy(); // remove those with lowest max
      } else if ( method.compareTo( MIN ) == 0 ) {
         sortedCriterion = minLevel.copy(); // remoe those with lowest min
      } else {
         throw new IllegalStateException( "Invalid method selected" );
      }

      sortedCriterion.sort();

      if ( useAsFraction ) {
         if ( lowCut > 1.0 || lowCut < 0.0 ) {
            throw new IllegalArgumentException(
                  "Illegal value for level cut, must be a fraction between 0 and 1" );
         }

         /* Note that 'ceil' is used to determine the cut */
         if ( removeAllNegative ) {

            realLowCut = sortedCriterion
                  .get( ( int ) Math.ceil( ( double ) ( ( numRows - numAllNeg )
                        * lowCut + numAllNeg ) ) );
         } else {
            realLowCut = sortedCriterion.get( ( int ) Math
                  .ceil( ( ( double ) numRows * lowCut ) ) );
         }
      } else {
         realLowCut = lowCut;
      }

      // go back over the data now using the cutpoint.
      int kept = 0;
      Vector MTemp = new Vector();
      Vector rowNames = new Vector();

      for ( int i = 0; i < numRows; i++ ) {
         if ( maxLevel.get( i ) > realLowCut ) {
            kept++;
            MTemp.add( data.getRowObj( i ) );
            rowNames.add( data.getRowName( i ) );
         }
      }

      DenseDoubleMatrix2DNamed returnval = new DenseDoubleMatrix2DNamed( MTemp
            .size(), numCols );
      for ( int i = 0; i < MTemp.size(); i++ ) {
         for ( int j = 0; j < numCols; j++ ) {
            returnval.set( i, j, ( ( ( Double[] ) MTemp.get( i ) )[j] )
                  .doubleValue() );
         }
      }
      returnval.setColumnNames( data.getColNames() );
      returnval.setRowNames( rowNames );

      log.info( "There are " + kept + " rows left after filtering." );

      return ( returnval );

   }

}