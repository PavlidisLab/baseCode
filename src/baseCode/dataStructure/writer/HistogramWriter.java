package baseCode.dataStructure.writer;

import java.io.PrintStream;

import corejava.Format;
import hep.aida.IHistogram1D;

/**
 * Print an IHistogram1D object to a text file.
 * <p> Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @see <a href="http://hoschek.home.cern.ch/hoschek/colt/V1.0.3/doc/hep/aida/IHistogram1D.html">hep.aida.IHistogram1D</a>
 * @author Paul Pavlidis
 * @version $Id$
 */
public class HistogramWriter {

   /**
    * Print out a IHistogram1D object.
    * @param h IHistogram1D to be printed.
    * @param s PrintStream to be printed to.
    */
   public void write( IHistogram1D h, PrintStream s ) {
      //   NumberFormat k = new DecimalFormat();
      Format k = new Format( "%1.5g" );
      int total = h.entries();
      s.print( "Bin\tCount\tFraction\n" );
      for ( int i = 0; i < h.xAxis().bins(); i++ ) {
         s.print( k.form( h.xAxis().binLowerEdge( i ) ) + "\t" + h.binEntries( i ) + "\t" +
                  k.form( ( double ) h.binEntries( i ) / ( double ) total ) + "\n" );
      }
   }

}
