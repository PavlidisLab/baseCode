package baseCode.common;

/**
 * 
 *
 * <hr>
 * <p>Copyright (c) 2004 Columbia University
 * @author pavlidis
 * @version $Id$
 */
public abstract class Visitable implements Comparable {

   private boolean mark;
   
   public boolean isVisited() {
      return mark;
   }
   
   public void mark() {
      mark = true;
   }
   
   public void unMark() {
      mark = false;
   }
   
}
