package baseCode.dataFilter;

import java.util.Vector;

import baseCode.dataStructure.matrix.NamedMatrix;

/**
 * Remove probes that have names meeting certain rules indicating they may have low reliability. This is targeted at
 * cases like "AFFX", "_st", "_f_at" and so forth.
 * <p>
 * Copyright (c) 2004
 * </p>
 * <p>
 * Institution:: Columbia University
 * </p>
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class AffymetrixProbeNameFilter extends AbstractFilter implements Filter {

   private boolean skip_ST = false;
   private boolean skip_AFFX = false;
   private boolean skip_F = false;
   private boolean skip_X = false;
   private boolean skip_G = false;

   /**
    * Filter probes that contain the '_st' (sense strand) tag
    */
   public static final int ST = 1;

   /**
    * Filter probes that have the AFFX prefix.
    */
   public static final int AFFX = 2;

   /**
    * Filter probes that have the "_f_at" (family) tag.
    */
   public static final int F = 3;

   /**
    * Filter probes that have the "_x_at" tag.
    */
   public static final int X = 4;

   /**
    * Filter probes that have the "_g_at" (group) tag.
    */
   public static final int G = 5;

   /**
    * @param criteria int[] of constants indicating the criteria to use.
    */
   public AffymetrixProbeNameFilter( int[] criteria ) {
      this.setCriteria( criteria );
   }

   private void setCriteria( int[] criteria ) {
      for ( int i = 0; i < criteria.length; i++ ) {
         switch ( criteria[i] ) {
            case ST: {
               skip_ST = true;
            }
            case AFFX: {
               skip_AFFX = true;
            }
            case F: {
               skip_F = true;
            }
            case X: {
               skip_X = true;
            }
            case G: {
               skip_G = true;
            }
            default: {
               break;
            }
         }
      }
   }

   public NamedMatrix filter( NamedMatrix data ) {
      Vector MTemp = new Vector();
      Vector rowNames = new Vector();
      int numRows = data.rows();
      int numCols = data.columns();

      int kept = 0;
      for ( int i = 0; i < numRows; i++ ) {
         String name = data.getRowName( i );

         // apply the rules.
         if ( skip_ST && name.endsWith( "_st" ) ) { // 'st' means sense strand.
            continue;
         }

         if ( skip_AFFX && name.startsWith( "AFFX" ) ) {
            continue;
         }

         if ( skip_F && name.endsWith( "_f_at" ) ) { // gene family. We don't
            // like.
            continue;
         }

         if ( skip_X && name.endsWith( "_x_at" ) ) {
            continue;
         }
         if ( skip_G && name.endsWith( "_g_at" ) ) {
            continue;
         }
         MTemp.add( data.getRowObj( i ) );
         rowNames.add( name );
         kept++;
      }

      NamedMatrix returnval = getOutputMatrix( data, MTemp.size(), numCols );

      for ( int i = 0; i < MTemp.size(); i++ ) {
         for ( int j = 0; j < numCols; j++ ) {
            returnval.set( i, j, ( ( Object[] ) MTemp.get( i ) )[j] );
         }
      }
      returnval.setColumnNames( data.getColNames() );
      returnval.setRowNames( rowNames );
      log.info( "There are " + kept + " rows left after filtering." );

      return ( returnval );

   }
}