package baseCode.dataFilter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import baseCode.dataStructure.NamedMatrix;

/**
 * Remove probes that have names meeting certain rules indicating they may have low reliability. This is targeted
 * at cases like "AFFX", "_st", "_f_at" and so forth.
 * <p> Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */

public class AffymetrixProbeNameFilter
    extends AbstractFilter
    implements Filter {

   public NamedMatrix filter( NamedMatrix data ) {
      Vector MTemp = new Vector();
      Vector rowNames = new Vector();
      int numRows = data.rows();
      int numCols = data.columns();

      int kept = 0;
      for ( int i = 0; i < numRows; i++ ) {
         String name = data.getRowName( i );

         // apply the rules.
         if ( name.endsWith( "_st" ) ) { // 'st' means sense strand.
            continue;
         }

         if ( name.startsWith( "AFFX" ) ) {
            continue;
         }

         if ( name.endsWith( "_f_at" ) ) { // gene family. We don't like.
            continue;
         }

         if ( name.endsWith( "_x_at" ) ) {
            //         continue;
         }

         MTemp.add( data.getRowObj( i ) );
         rowNames.add( name );
         kept++;
      }

      NamedMatrix returnval = null;
      try {
         Constructor cr = data.getClass().getConstructor( new Class[] {int.class, int.class} );
         returnval =
             ( NamedMatrix ) cr.newInstance(
             new Object[] {new Integer( MTemp.size() ), new Integer( numCols )} );
      }
      catch ( SecurityException e ) {
         e.printStackTrace();
      }
      catch ( IllegalArgumentException e ) {
         e.printStackTrace();
      }
      catch ( NoSuchMethodException e ) {
         e.printStackTrace();
      }
      catch ( InstantiationException e ) {
         e.printStackTrace();
      }
      catch ( IllegalAccessException e ) {
         e.printStackTrace();
      }
      catch ( InvocationTargetException e ) {
         e.printStackTrace();
      }

      for ( int i = 0; i < MTemp.size(); i++ ) {
         for ( int j = 0; j < numCols; j++ ) {
            returnval.set( i, j, ( ( Object[] ) MTemp.get( i ) )[j] );
         }
      }
      returnval.setColumnNames( data.getColNames() );
      returnval.setRowNames( rowNames );
      log.info(
          "There are " + kept + " rows left after filtering." );

      return ( returnval );

   }
}
