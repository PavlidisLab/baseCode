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
 * Reads Affymetrix Probe files.
 * <p>
 * Expected format is tabbed, NOT FASTA: 1494_f_at 1 325 359 1118 TCCCCATGAGTTTGGCCCGCAGAGT Antisense. A one-line header
 * starting with the word "Probe" is permitted.
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class AffyProbeReader implements ArrayReporterReader {

    protected static final Log log = LogFactory.getLog( AffyProbeReader.class );

    /**
     * @param fileName
     * @return Map containing probe set ids as keys, AffymetriProbeSets as values.
     * @throws IOException
     */
    public Map read( String fileName ) throws IOException {
        File file = new File( fileName );
        if ( !file.canRead() ) throw new IOException( "Can't read from file " + fileName );
        InputStream is = new FileInputStream( file );
        return read( is );
    }

    /**
     * @param is
     * @return Map containing probe set ids as keys, AffymetriProbeSets as values.
     * @throws IOException if the file format is not valid.
     */
    public Map read( InputStream is ) throws IOException {
        BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
        Map probeSets = new HashMap();

        String line = null;
        int count = 0;
        while ( ( line = br.readLine() ) != null ) {

            String[] sArray = line.split( "\t" );
            if ( sArray.length == 0 ) throw new IOException( "File format is not valid" );

            String probeSetId = sArray[0];
            if ( probeSetId.startsWith( "Probe" ) ) continue; // header.

          //  if ( sArray.length < 5 ) throw new IOException( "File format is not valid" );

            String xcoord = sArray[1];
            String ycoord = sArray[1];
            String sequence = sArray[4];
            int locationInTarget = Integer.parseInt( sArray[3] ); // unfortunately this depends on the file.
            try {
                Sequence ns = DNATools.createDNASequence( sequence, probeSetId + "_" + xcoord + "_" + ycoord );
                AffymetrixProbe ap = new AffymetrixProbe( probeSetId, xcoord, ns, locationInTarget );

                if ( !probeSets.containsKey( probeSetId ) )
                    probeSets.put( probeSetId, new AffymetrixProbeSet( probeSetId ) );

                ( ( AffymetrixProbeSet ) probeSets.get( probeSetId ) ).add( ap );

            } catch ( IllegalSymbolException e ) {
                throw new IOException( "a DNA sequence was not valid, or the file format is incorrect." );
            }
            count++;
            if ( count % 10000 == 0 ) {
                log.debug( "Read in " + count + " probes..." );
            }
        }

        return probeSets;

    }
}
