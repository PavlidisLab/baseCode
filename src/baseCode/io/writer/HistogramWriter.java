package baseCode.io.writer;

import hep.aida.IHistogram1D;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import corejava.Format;

/**
 * Print an {@link hep.aidia.IHistogram1D}object to a text file.
 * <p>
 * Copyright (c) 2004
 * </p>
 * <p>
 * Institution: Columbia University
 * </p>
 * 
 * @see <a href="http://hoschek.home.cern.ch/hoschek/colt/V1.0.3/doc/hep/aida/IHistogram1D.html">hep.aida.IHistogram1D
 *      </a>
 * @author Paul Pavlidis
 * @version $Id$
 */
public class HistogramWriter {

   /**
    * Print out a IHistogram1D object.
    * 
    * @param h IHistogram1D to be printed.
    * @param s PrintStream to be printed to.
    * @throws IOException
    */
   public void write( IHistogram1D h, Writer s ) throws IOException {
      Format k = new Format( "%1.5g" );
      int total = h.entries();
      s.write( "Bin\tCount\tFraction\n" );
      for ( int i = 0; i < h.xAxis().bins(); i++ ) {
         s.write( k.form( h.xAxis().binLowerEdge( i ) ) + "\t"
               + h.binEntries( i ) + "\t"
               + k.form( ( double ) h.binEntries( i ) / ( double ) total )
               + "\n" );
      }
   }
   
   /**
    * Print to a stream.
    * @param h
    * @param s
    * @throws IOException
    */
   public void write (IHistogram1D h, OutputStream s ) throws IOException {
      this.write(h, new OutputStreamWriter(s) );
   }

}