package baseCode.Util;

/**
 * <p>Title: Stupid debugging helper.</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 * @todo replace this with commons-logging.
 */

public class Debug {

   private static boolean dodebug = false;
   private static int debuglevel = 1;

   public static void setDebug(boolean b) {
      dodebug = b;
   }

   private static void println(String msg) {
      if (dodebug) {
         System.err.println(msg);
      }
   }

   private static void print(String msg) {
      if (dodebug) {
         System.err.print(msg);
      }
   }

}
