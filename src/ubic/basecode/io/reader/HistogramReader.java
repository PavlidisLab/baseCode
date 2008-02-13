/*
 * The baseCode project
 * 
 * Copyright (c) 2008 University of British Columbia
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
package ubic.basecode.io.reader;

import hep.aida.ref.Histogram1D;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads histograms stored in flat files
 * 
 * @author raymond
 */
public class HistogramReader {
    protected BufferedReader in;
    private String title;

    public HistogramReader( Reader in, String title ) {
        this.in = new BufferedReader( in );
        this.title = title;
    }

    public HistogramReader( String fileName ) throws FileNotFoundException {
        this.in = new BufferedReader( new FileReader( fileName ) );
    }

    /**
     * @return
     * @throws IOException
     */
    public Histogram1D read1D() throws IOException {
        int numHeaderLines = 1; // ignore the column header
        Map<Double, Integer> binCountMap = new HashMap<Double, Integer>();
        Double min = new Double( Double.POSITIVE_INFINITY );
        Double max = new Double( Double.NEGATIVE_INFINITY );
        while ( in.ready() ) {
            String line = in.readLine();
            if ( line.startsWith( "#" ) || numHeaderLines-- > 0 ) continue;
            String fields[] = line.split( "\t" );
            Double bin = Double.valueOf( fields[0] );
            Integer count = Integer.valueOf( fields[1] );
            binCountMap.put( bin, count );
            if ( bin.compareTo( min ) < 0 ) min = bin;
            if ( bin.compareTo( max ) > 0 ) max = bin;
        }
        int numBins = binCountMap.keySet().size();

        Histogram1D hist = new Histogram1D( title, numBins, min.doubleValue(), max.doubleValue() );
        for ( Object element : binCountMap.entrySet() ) {
            Map.Entry entry = ( Map.Entry ) element;
            Double bin = ( Double ) entry.getKey();
            Integer count = ( Integer ) entry.getValue();
            hist.fill( bin.doubleValue(), count.doubleValue() );
        }
        return hist;
    }
}
