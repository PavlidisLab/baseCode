/*
 * Created on Jun 16, 2004
 *
 */
package baseCode.dataFilter;

import java.util.Set;
import java.util.Vector;

import baseCode.dataStructure.NamedMatrix;

/**
 * Remove or retain rows that are on a list.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class RowNameFilter extends AbstractFilter implements Filter {

   private boolean exclude = false;
   private Set filterNames;

   /**
    * 
    * @param namesToFilter
    * @param exclude Set to true if you want the list to indicate items to be
    *        skipped, rather than selected.
    */
   public RowNameFilter( Set namesToFilter, boolean exclude ) {
      this( namesToFilter );
      this.exclude = exclude;
   }

   /**
    * 
    * @param namesToFilter
    */
   public RowNameFilter( Set namesToFilter ) {
      filterNames = namesToFilter;
   }
   
   public RowNameFilter() {
      filterNames = null;
   }
   
   public void setFilterNames(Set namesToFilter, boolean exclude) {
      this.filterNames = namesToFilter;
      this.exclude = exclude;
   }

   /**
    * Filter according to row names.
    */
   public NamedMatrix filter( NamedMatrix data ) {
      Vector MTemp = new Vector();
      Vector rowNames = new Vector();
      int numRows = data.rows();
      int numCols = data.columns();
      int numNeeded = filterNames.size();
      int kept = 0;
      for ( int i = 0; i < numRows; i++ ) {
         String name = data.getRowName( i );

         // apply the rules.
         if ( filterNames.contains( name ) ) {
            if ( exclude ) {
               continue;
            }
            MTemp.add( data.getRowObj( i ) );
            rowNames.add( name );
            kept++;
            if (kept >= numNeeded) {
               break; // no use in continuing.
            }
         }

         if ( exclude ) {
            MTemp.add( data.getRowObj( i ) );
            rowNames.add( name );
            kept++;
         }   
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