package baseCode.dataStructure;

/**
 * A class representing a descriptive term that can be associated with things.
 * 
 * Copyright (c) 2004 Columbia University
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class OntologyEntry {

   private String id = "";
   private String name = "";
   private String definition = "";

   /**
    * 
    * @param id
    */
   public OntologyEntry( String id ) {
      this( id, null, null );
   }

   /**
    * 
    * @param id
    * @param name
    * @param def
    */
   public OntologyEntry( String id, String name, String def ) {
      this.id = id.intern();
      this.name = name.intern();
      this.definition = def.intern();
   }

   /**
    * 
    * @return
    */
   public String getName() {
      return name.intern();
   }

   /**
    * 
    * @return
    */
   public String getId() {
      return id;
   }

   /**
    * 
    * @return
    */
   public String getDefinition() {
      return definition;
   }

   /**
    * 
    * @param n
    */
   public void setName( String n ) {
      name = n;
   }

   /**
    * 
    * @param d
    */
   public void setDefinition( String d ) {
      definition = d;
   }

   public String toString() {
      return new String( id + ": \t" + name );
   }

}