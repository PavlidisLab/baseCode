package baseCode.bio.sequence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.IllegalSymbolException;

/**
 * Parse an Illumina "manifest.txt" file (tab-delimited). A one-line header is permitted.
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class IlluminaProbeReader implements ArrayReporterReader {

    protected static final Log log = LogFactory.getLog( IlluminaProbeReader.class );

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.bio.sequence.ArrayReporterReader#read(java.lang.String)
     */
    public Map read( String fileName ) throws IOException {
        File file = new File( fileName );
        if ( !file.canRead() ) throw new IOException( "Can't read from file " + fileName );
        InputStream is = new FileInputStream( file );
        return read( is );
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.bio.sequence.ArrayReporterReader#read(java.io.InputStream)
     */
    public Map read( InputStream is ) throws IOException {
        BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
        Map probes = new HashMap();

        String line = null;

        while ( ( line = br.readLine() ) != null ) {

            String[] sArray = line.split( "\t" );
            if ( sArray.length == 0 ) throw new IOException( "File format is not valid" );

            String probeSetId = sArray[0];
            if ( probeSetId.startsWith( "Search" ) ) continue; // header.

            if ( sArray.length < 10 ) throw new IOException( "File format is not valid" );

            String sequence = sArray[9];

            try {
                Sequence ns = DNATools.createDNASequence( sequence, probeSetId );
                Probe ap = new SimpleProbe( probeSetId, ns );

                probes.put( probeSetId, ap );

            } catch ( IllegalSymbolException e ) {
                throw new IOException( "a DNA sequence was not valid, or the file format is incorrect." );
            }
        }

        return probes;

    }

}
