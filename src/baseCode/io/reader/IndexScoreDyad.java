package baseCode.io.reader;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
final class IndexScoreDyad {

   int key;
   double value;

   /**
    * @param key
    * @param value
    */
   public IndexScoreDyad( int key, double value ) {
      this.key = key;
      this.value = value;
   }

   /**
    * @return Returns the key.
    */
   public int getKey() {
      return key;
   }

   /**
    * @return Returns the value.
    */
   public double getValue() {
      return value;
   }
}

