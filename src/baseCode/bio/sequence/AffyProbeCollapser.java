package baseCode.bio.sequence;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import org.biojava.bio.seq.Sequence;

/**
 * Given an Affymetrix array design, "collapse" the probes into sequences that include all probe sequence.
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class AffyProbeCollapser {

    public AffyProbeCollapser() {
    };

    public void collapse( InputStream is, Writer writer ) throws IOException {

        AffyProbeReader apr = new AffyProbeReader();

        Map probeSets = apr.read( is );

        for ( Iterator iter = probeSets.keySet().iterator(); iter.hasNext(); ) {
            String probeSetname = ( String ) iter.next();
            AffymetrixProbeSet apset = ( AffymetrixProbeSet ) probeSets.get( probeSetname );

            Sequence m = apset.collapse();
            writer.write( ">" + probeSetname + "\n" + m.seqString() + "\n");
        }

    }

    public static void main( String[] args ) throws IOException {
        String filename = args[0];
        File f = new File( filename );
        if ( !f.canRead() ) throw new IOException();

        String outputFileName = args[1];
        File o = new File( outputFileName );
  //      if ( !o.canWrite() ) throw new IOException( "Can't write " + outputFileName );

        AffyProbeCollapser apc = new AffyProbeCollapser();
        apc.collapse( new FileInputStream( f ), new BufferedWriter( new FileWriter( o ) ) );

    }
}
