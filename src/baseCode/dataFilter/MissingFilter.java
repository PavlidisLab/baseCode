package baseCode.dataFilter;

import java.text.DecimalFormat;
import java.util.Vector;

import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import cern.colt.list.DoubleArrayList;

/**
 * Remove rows from a matrix that are missing too many points.
 * <p> Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version 1.0
 */
public class MissingFilter
    extends AbstractFilter
    implements Filter {

   private int minpresent = 5;
   private static final int ABSOLUTEMINPRESENT = 2;
   private double maxfractionRemoved = 0.0;
   private double minPresentFraction = 1.0;
   private boolean maxFractionRemovedIsSet = false;
   private boolean minPresentFractionIsSet = false;
   private boolean minPresentIsSet = false;

   /**
    * Set the minimum number of values that must be present in each row. The
    * default value is 5. This is always overridden by a hard-coded value
    * (currently 2) that must be present for a row to be kept; but this value is
    * in turn overridden by the maxfractionRemoved.
    *
    * @param m int
    */
   public void setMinPresent( int m ) {
      minPresentIsSet = true;
      minpresent = m;
   }

   /**
    *
    * @param m double
    */
   public void setMinPresentFraction( double m ) {
      minPresentFractionIsSet = true;
      minPresentFraction = m;
   }

   /**
    * Set the maximum fraction of rows which will be removed from the data set.
    * The default value is 0.3 Set it to 1.0 to remove this restriction.
    *
    * @param f double
    */
   public void setMaxFraction( double f ) {
      maxFractionRemovedIsSet = true;
      maxfractionRemoved = f;
   }

   /**
    * Run the filter on the given data using the parameters that have been set.
    *
    * @param data DenseDoubleMatrix2DNamed
    * @return A new matrix that has been filtered.
    */
   public DenseDoubleMatrix2DNamed filter( DenseDoubleMatrix2DNamed data ) {
      Vector MTemp = new Vector();
      Vector rowNames = new Vector();
      int numRows = data.rows();
      int numCols = data.columns();
      DoubleArrayList present = new DoubleArrayList( numRows );

      int kept = 0;

      if ( minPresentFractionIsSet ) {
         setMinPresent( ( int ) Math.ceil( minPresentFraction * numCols ) );
      }

      if ( !minPresentIsSet ) {
         throw new IllegalStateException( "Filtering parameters are not set." );
      }

      for ( int i = 0; i < numRows; i++ ) {
         int found = 0;
         for ( int j = 0; j < numCols; j++ ) {
            if ( !Double.isNaN( data.get( i, j ) ) ) {
               found++;
            }
         }
         present.add( found );
         if ( found >= ABSOLUTEMINPRESENT && found >= minpresent ) {
            kept++;
         }
      }

      if ( kept < ( double ) numRows * ( 1.0 - maxfractionRemoved ) && maxfractionRemoved != 0.0 ) {
         DoubleArrayList sortedPresent = new DoubleArrayList( numRows );
         sortedPresent = present.copy();
         sortedPresent.sort();
         sortedPresent.reverse();
         DecimalFormat fo = new DecimalFormat();
         log.info( "There are " + fo.format( kept ) +
                   " rows that meet criterion of at least " + fo.format( minpresent ) +
                   " non-missing values, but that's too many given the max fraction of " +
                   maxfractionRemoved + "; minpresent adjusted to " +
                   ( int ) sortedPresent.get( ( int ) ( ( double ) numRows * ( maxfractionRemoved ) ) ) );
         minpresent = ( int ) sortedPresent.get( ( int ) ( ( double ) numRows *
             ( maxfractionRemoved ) ) );
      }

      // go back over the data now using the new cutpoint.
      kept = 0;
      for ( int i = 0; i < numRows; i++ ) {
         if ( present.get( i ) >= minpresent &&
              present.get( i ) >= ABSOLUTEMINPRESENT ) {

            MTemp.add( data.getRow( i ) );
            rowNames.add( data.getRowName( i ) );
            kept++;
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
          "There are " + kept + " rows after removing rows which have fewer than " + minpresent +
          " values (or fewer than " + ABSOLUTEMINPRESENT + ")" );

      return ( returnval );

   }
}
