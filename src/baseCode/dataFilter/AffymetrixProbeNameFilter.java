package baseCode.dataFilter;

import baseCode.dataStructure.DenseDoubleMatrix2DNamed;
import java.util.Vector;

/**
 * <p>Title: </p>
 * <p>Description: Remove probes that have names meeting certain rules.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */

public class AffymetrixProbeNameFilter
    implements Filter {

   public DenseDoubleMatrix2DNamed filter(DenseDoubleMatrix2DNamed data) {
      Vector MTemp = new Vector();
      Vector rowNames = new Vector();
      int numRows = data.rows();
      int numCols = data.columns();

      int kept = 0;
      for (int i = 0; i < numRows; i++) {
         String name = data.getRowName(i);

         // apply the rules.
         if (name.endsWith("_st")) { // 'st' means sense strand.
            continue;
         }

         if (name.startsWith("AFFX")) {
            continue;
         }

         if (name.endsWith("_f_at")) { // gene family. We don't like.
            continue;
         }

         if (name.endsWith("_x_at")) {
   //         continue;
         }

         MTemp.add(data.getRow(i));
         rowNames.add(name);
         kept++;
      }

      DenseDoubleMatrix2DNamed returnval = new DenseDoubleMatrix2DNamed(MTemp.size(), numCols);
      for (int i = 0; i < MTemp.size(); i++) {
         for (int j = 0; j < numCols; j++) {
            returnval.set(i, j, ( (double[]) MTemp.get(i))[j]);
         }
      }
      returnval.setColumnNames(data.getColNames());
      returnval.setRowNames(rowNames);

      System.err.println(
          "There are " + kept + " rows left after filtering.");

      return (returnval);

   }
}
