package baseCode.dataFilter;

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
 * There are multiple ways of determining cutpoints. The obvious possibilities
 * are the maximum value, the minimum value, the mean value, or the median
 * value.
 * <p>
 * Note that if you want to use different methods for high-level filtering than
 * for low-level filtering (e.g., using max for the low-level, and min for the
 * high-level, you have to filter twice. This could cause problems if you are
 * using fractional filtering and there are negative values (see below).
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
 */
public class LevelFilter extends AbstractFilter implements Filter {

   private double lowCut = Double.MIN_VALUE;
   private double highCut = Double.MAX_VALUE;
   private boolean useLowAsFraction = false;
   private boolean useHighAsFraction = false;
   private boolean removeAllNegative = false;

   public static final int MIN = 1;
   public static final int MAX = 2;
   public static final int MEDIAN = 3;
   public static final int MEAN = 4;
   private int method = MAX;

   /**
    * Choose the method that will be used for filtering. Default is 'MAX'. Those
    * rows with the lowest values are removed during 'low' filtering.
    * 
    * @param method one of LevelFilter.MIN, LevelFilter.MAX, LevelFilter.MEDIAN,
    *        or LevelFilter.MEAN.
    */
   public void setMethod( int method ) {
      if ( method != MIN && method != MAX && method != MEDIAN && method != MEAN ) {
         throw new IllegalArgumentException(
               "Unknown filtering method requested" );
      }
      this.method = method;
   }

   /**
    * Set the low threshold for removal.
    * 
    * @param lowCut the threshold
    */
   private void setLowCut( double lowCut ) {
      this.lowCut = lowCut;
   }

   /**
    * 
    * @param lowCut
    * @param isFraction
    */
   public void setLowCut( double lowCut, boolean isFraction ) {
      if ( isFraction && ( lowCut < 0.0 || lowCut > 1.0 ) ) {
         throw new IllegalArgumentException(
               "Value "
                     + lowCut
                     + " for low cut is invalid for using as fractions, must be >0.0 and <1.0," );
      }
      setLowCut( lowCut );
      useLowAsFraction = isFraction;
   }

   /**
    * Set the high threshold for removal. If not set, no filtering will occur.
    * 
    * @todo make this a separate filtering step?
    * @param h the threshold
    */
   private void setHighCut( double h ) {
      highCut = h;
   }

   /**
    * 
    * @param h
    * @param isFraction
    */
   public void setHighCut( double highCut, boolean isFraction ) {
      if ( isFraction && ( highCut > 1.0 || highCut < 0.0 ) ) {
         throw new IllegalArgumentException(
               "Value "
                     + highCut
                     + "  for high cut is invalid for using as fractions, must be >0.0 and <1.0," );
      }
      setHighCut( highCut );
      useHighAsFraction = isFraction;
   }

   /**
    * Set the filter to remove all rows that have only negative values. This is
    * applied BEFORE applying fraction-based criteria. In other words, if you
    * request filtering 0.5 of the values, and 0.5 have all negative values, you
    * will get 0.25 of the data back. Default = false.
    * 
    * @param t boolean
    */
   public void setRemoveAllNegative( boolean t ) {
      log.info( "Rows with all negative values will be "
            + "removed PRIOR TO applying fraction-based criteria." );
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
      if ( t == true
            && ( lowCut < 0.0 || highCut > 1.0 || highCut < 0.0 || lowCut > 1.0 ) ) {
         throw new IllegalArgumentException(
               "Value for cut(s) are invalid for using "
                     + "as fractions, must be >0.0 and <1.0," );
      }
      useLowAsFraction = t;
   }

   /**
    * 
    * @param data
    * @return
    */
   public NamedMatrix filter( NamedMatrix data ) {

      if ( !( data instanceof DenseDoubleMatrix2DNamed ) ) {
         throw new IllegalArgumentException(
               "Only valid for DenseDoubleMatrix2DNamed" );
      }

      if ( lowCut == Double.MIN_VALUE && highCut == Double.MAX_VALUE ) {
         log.info( "No filtering requested" );
         return data;
      }

      int numRows = data.rows();
      int numCols = data.columns();

      DoubleArrayList criteria = new DoubleArrayList(new double[numRows]);

      /*
       * compute criteria.
       */
      DoubleArrayList rowAsList = new DoubleArrayList( new double[numCols] );
      int numAllNeg = 0;
      for ( int i = 0; i < numRows; i++ ) {
         Double[] row = ( Double[] ) data.getRowObj( i );
         int numNeg = 0;
         /* stupid, copy into a DoubleArrayList so we can do stats */
         for ( int j = 0; j < numCols; j++ ) {
            double item = row[j].doubleValue();
            rowAsList.set( j, item );
            if ( item < 0.0 || Double.isNaN( item ) ) {
               numNeg++;
            }
         }
         if ( numNeg == numCols ) {
            numAllNeg++;
         }

         switch ( method ) {
            case MIN: {
               criteria.set(i, DescriptiveWithMissing.min( rowAsList ) );
               break;
            }
            case MAX: {
               criteria.set(i, DescriptiveWithMissing.max( rowAsList ) );
               break;
            }
            case MEAN: {
               criteria.set(i, DescriptiveWithMissing.mean( rowAsList ) );
               break;
            }
            case MEDIAN: {
               criteria.set(i, DescriptiveWithMissing.median( rowAsList ) );
               break;
            }
            default: {
               break;
            }
         }
      }

      DoubleArrayList sortedCriteria = criteria.copy();
      sortedCriteria.sort();

      double realLowCut = Double.MIN_VALUE; 
      double realHighCut = Double.MAX_VALUE;

      if ( useHighAsFraction ) {
         if ( highCut > 1.0 || highCut < 0.0 ) {
            throw new IllegalStateException(
                  "High level cut must be a fraction between 0 and 1" );
         }
         /* Note that 'ceil' is used to determine the cut */
         if ( removeAllNegative ) {
            realHighCut = sortedCriteria
                  .get( numRows - ( int ) Math
                        .ceil(  ( double ) ( ( numAllNeg ) * highCut + numAllNeg ) ) );
         } else {
            realHighCut = sortedCriteria.get( numRows
                  - ( int ) Math.ceil( ( ( double ) numRows * highCut ) ) );
         }
         realHighCut = highCut;
      }

      if ( useLowAsFraction ) {
         if ( lowCut > 1.0 || lowCut < 0.0 ) {
            throw new IllegalStateException(
                  "Low level cut must be a fraction between 0 and 1" );
         }

         /* Note that 'ceil' is used to determine the cut */
         if ( removeAllNegative ) {
            realLowCut = sortedCriteria
                  .get( ( int ) Math.ceil( ( double ) ( ( numRows - numAllNeg )
                        * lowCut + numAllNeg ) ) );

         } else {
            realLowCut = sortedCriteria.get( ( int ) Math
                  .ceil( ( ( double ) numRows * lowCut ) ) );
         }
      } else {
         realLowCut = lowCut;
      }

      // go back over the data now using the cutpoints. This is not optimally
      // efficient.
      int kept = 0;
      Vector rowsToKeep = new Vector();
      Vector rowNames = new Vector();

      for ( int i = 0; i < numRows; i++ ) {
         if ( criteria.get( i ) >= realLowCut && criteria.get( i ) <= realHighCut ) {
            kept++;
            rowsToKeep.add( data.getRowObj( i ) );
            rowNames.add( data.getRowName( i ) );
         }
      }

      DenseDoubleMatrix2DNamed returnval = new DenseDoubleMatrix2DNamed(
            rowsToKeep.size(), numCols );
      for ( int i = 0; i < rowsToKeep.size(); i++ ) {
         for ( int j = 0; j < numCols; j++ ) {
            returnval.set( i, j, ( ( ( Double[] ) rowsToKeep.get( i ) )[j] )
                  .doubleValue() );
         }
      }
      returnval.setColumnNames( data.getColNames() );
      returnval.setRowNames( rowNames );

      log.info( "There are " + kept + " rows left after filtering." );

      return ( returnval );

   }

}