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
package ubic.basecode.io.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Reads a tab-delimited file with keys in first column, values in second.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class MapReader {

    /**
     * @param stream InputStream
     * @return
     * @throws IOException
     */
    public Map<String, String> read( InputStream stream ) throws IOException {
        return this.read( stream, false );
    }

    /**
     * @param stream InputStream
     * @param hasHeader boolean if a one-line header is present.
     * @return
     * @throws IOException
     */
    public Map<String, String> read( InputStream stream, boolean hasHeader ) throws IOException {
        Map<String, String> result = new HashMap<String, String>();

        BufferedReader dis = new BufferedReader( new InputStreamReader( stream ) );
        if ( hasHeader ) {
            dis.readLine();
        }

        String row;
        while ( ( row = dis.readLine() ) != null ) {
            StringTokenizer st = new StringTokenizer( row, "\t" );
            String key = st.nextToken();

            String value = st.nextToken();

            result.put( key, value );

        }
        dis.close();

        return result;
    }

    /**
     * @param filename String
     * @throws IOException
     * @return Map
     */
    public Map read( String filename ) throws IOException {
        return this.read( filename, false );
    }

    /**
     * @param filename name of the tab-delimited file
     * @param hasHeader boolean if a one-line header is present.
     * @return Map from the file.
     * @throws IOException
     */
    public Map read( String filename, boolean hasHeader ) throws IOException {
        File infile = new File( filename );
        if ( !infile.exists() || !infile.canRead() ) {
            throw new IllegalArgumentException( "Could not read from " + filename );
        }
        FileInputStream stream = new FileInputStream( infile );
        return read( stream, hasHeader );

    }
}
