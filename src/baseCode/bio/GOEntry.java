package baseCode.bio;

import baseCode.dataStructure.OntologyEntry;

/**
 * 
 *
 * <hr>
 * <p>Copyright (c) 2004 Columbia University
 * @author pavlidis
 * @version $Id$
 */
public class GOEntry extends OntologyEntry {

   private String aspect;
   
   public GOEntry(String id, String name, String def, String aspect ) {
      super(id, name, def);
      this.aspect = aspect;
   }
   
   public void setAspect( String aspect) {
      this.aspect = aspect;
   }
   
   public String getAspect () {
      return aspect;
   }
   
}
