package baseCode.util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Copyright (c) 2004 Columbia University
 * @author Paul Pavlidis
 * @version $Id$
 */
public class StatusDebugLogger implements StatusViewer {

   protected static final Log log = LogFactory.getLog( StatusDebugLogger.class );
   
   /* (non-Javadoc)
    * @see baseCode.util.StatusViewer#setStatus(java.lang.String)
    */
   public void setStatus( String s ) {
      log.debug(s);

   }

}
