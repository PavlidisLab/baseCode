package baseCode.dataStructure.writer;

import java.io.PrintStream;
import corejava.Format;
import hep.aida.IHistogram1D;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */
public class HistogramWriter {

    /**
     * Print out a IHistogram1D object.
     * @param h IHistogram1D to be printed.
     * @param s PrintStream to be printed to.
     */
    public void write(IHistogram1D h, PrintStream s) {
      //   NumberFormat k = new DecimalFormat();
      Format k = new Format("%1.5g");
      int total = h.entries();
      s.print("Bin\tCount\tFraction\n");
      for (int i = 0; i < h.xAxis().bins(); i++) {
         s.print(k.form(h.xAxis().binLowerEdge(i)) + "\t" + h.binEntries(i) + "\t" +
                 k.form( (double) h.binEntries(i) / (double) total) + "\n");
      }
   }

}
