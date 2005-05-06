package baseCode.bio.geneset;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TestGeneAnnotations extends TestCase {

    InputStream is;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {

        is = TestGeneAnnotations.class.getResourceAsStream( "/data/HG-U133_Plus_2_annot_sample.csv" );
        if ( is == null ) throw new IllegalStateException();
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public final void testReadAffyCsv() throws Exception {
        GeneAnnotations ga = new GeneAnnotations();
        ga.readAffyCsv( is, null );
        ga.setUp( null );
        List geneSets = new ArrayList( ga.getGeneSetToGeneMap().keySet() );
        Collections.sort( geneSets );
        assertTrue( geneSets.size() > 0 );
       
    }
}
