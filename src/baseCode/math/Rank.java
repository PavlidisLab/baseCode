package baseCode.Math;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.ObjectArrayList;

/**
 * Calculate rank statistics
 * <p>Title: Rank</p>
 * <p>Description: Compute rank statistics</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */
public class Rank {


    /**
     * Rank transform an array. Ties are not handled specially.
     *
     * @param array DoubleArrayList
     * @return cern.colt.list.DoubleArrayList
     */
    public static DoubleArrayList rankTransform(DoubleArrayList array) {
      ObjectArrayList ranks = new ObjectArrayList(array.size());
      DoubleArrayList result = new DoubleArrayList(array.size());

      for (int i = 0; i < array.size(); i++) {
         ranks.set(i, new rankData(i, array.getQuick(i)));
      }

      ranks.sort();

      for (int i = 0; i < array.size(); i++) {
         result.set( ( (rankData) ranks.get(i)).getIndex(), i);
      }

      return result;
   }
}


/**
 * Helper class for rankTransform.
 */
class rankData
    implements Comparable {
   private int index;
   private double value;

   public rankData(int tindex, double tvalue) {
      index = tindex;
      value = tvalue;
   }

   public int compareTo(Object a) {
      rankData other = (rankData) (a);
      if (this.value < other.getValue()) {
         return -1;
      }
      else if (this.value > other.getValue()) {
         return 1;
      }
      else {
         return 0;
      }
   }

   public int getIndex() {
      return index;
   }

   public double getValue() {
      return value;
   }
}
