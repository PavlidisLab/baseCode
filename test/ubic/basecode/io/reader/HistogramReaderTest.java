/*
 * The baseCode project
 * 
 * Copyright (c) 2013 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.basecode.io.reader;

import static org.junit.Assert.assertEquals;
import hep.aida.ref.Histogram1D;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import ubic.basecode.util.FileTools;

/**
 * @author Paul
 * @version $Id$
 */
public class HistogramReaderTest {

    @Test
    public void testHistogramReaderString() throws Exception {
        String f = FileTools.resourceToPath( "/data/0240991490.degreeDist.txt" );
        Histogram1D hist = new HistogramReader( f ).read1D();
        assertEquals( 1, hist.binHeight( 4 ), 0.000000 );
    }

    @Test
    public void testHistogramReaderIs() throws Exception {
        InputStream f = this.getClass().getResourceAsStream( "/data/0240991490.degreeDist.txt" );
        Histogram1D hist = new HistogramReader( new InputStreamReader( f ), "title" ).read1D();
        assertEquals( 1, hist.binHeight( 4 ), 0.000000 );
    }

}
