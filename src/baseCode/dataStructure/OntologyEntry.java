package baseCode.dataStructure;

public class OntologyEntry {

   private String id = "";
   private String name = "";
   private String definition = "";

   public OntologyEntry(String id) {
      this(id, null, null);
   }

   public OntologyEntry(String id, String name, String def) {
      this.id = id;
      this.name = name;
      this.definition = def;
   }

   public String getName() {
      return name;
   }
   public String getId() {
      return id;
   }
   public String getDefinition() {
      return definition;
   }

   public void setName(String n) {
      name = n;
   }

   public void setDefinition(String d) {
      definition = d;
   }

   public String toString() {
      return new String(id + ": \t" + name);
   }

   /**
    * Two OntologyEntry instances are equal if their ids are equal.
    * 
    * @param k Object to be compared to.
    */
   public boolean equals(Object k) {
      if (k.getClass() != this.getClass()) {
         return false;
      }

      if (((OntologyEntry)k).getId() == id) {
         return true;
      }
      return false;

   }

}
