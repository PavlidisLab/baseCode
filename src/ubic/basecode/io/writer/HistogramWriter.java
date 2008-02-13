/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.io.writer;

import hep.aida.IHistogram1D;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import corejava.Format;

/**
 * Print an {@link hep.aidia.IHistogram1D}object to a text file.
 * 
 * @see <a href="http://hoschek.home.cern.ch/hoschek/colt/V1.0.3/doc/hep/aida/IHistogram1D.html">hep.aida.IHistogram1D
 *      </a>
 * @author Paul Pavlidis
 * @version $Id$
 */
public class HistogramWriter {

    /**
     * Print to a stream.
     * 
     * @param h
     * @param s
     * @throws IOException
     */
    public void write( IHistogram1D h, OutputStream s ) throws IOException {
        this.write( h, new OutputStreamWriter( s ) );
    }

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
            s.write( k.format( h.xAxis().binLowerEdge( i ) ) + "\t" + h.binEntries( i ) + "\t"
                    + k.format( ( double ) h.binEntries( i ) / ( double ) total ) + "\n" );
        }
        s.flush();
    }

}