package baseCode.dataFilter;

import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import baseCode.dataStructure.NamedMatrix;

/**
 * Base implementation of the filter class. Subclasses must implement the
 * filter() method.
 * 
 * <p>
 * Copyright (c) 2004 Columbia University
 * </p>
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */

public abstract class AbstractFilter implements Filter {

   protected static final Log log = LogFactory.getLog( AbstractFilter.class );

   protected NamedMatrix getOutputMatrix( NamedMatrix data, int numRows,
         int numCols ) {
      NamedMatrix returnval = null;

      Constructor cr;
      try {
         cr = data.getClass().getConstructor(
               new Class[] { int.class, int.class } );
         returnval = ( NamedMatrix ) cr.newInstance( new Object[] {
               new Integer( numRows ), new Integer( numCols ) } );
      } catch ( Exception e ) {
         e.printStackTrace();
      }

      return returnval;
   }

}