/*
 * Created on Jun 16, 2004
 *
 */
package baseCode.dataFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import baseCode.dataStructure.NamedMatrix;

/**
 * @author Owner
 *
 */
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2004</p>
 * <p>Institution: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */

public class AbstractFilter
    implements Filter {

   protected static Log log = LogFactory.getLog( Filter.class );

   public NamedMatrix filter( NamedMatrix data ) {
      return null;
   }

}
