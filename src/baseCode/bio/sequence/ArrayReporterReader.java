package baseCode.bio.sequence;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


/**
 * 
 *
 * <hr>
 * <p>Copyright (c) 2004-2005 Columbia University
 * @author pavlidis
 * @version $Id$
 */
public interface ArrayReporterReader {

    /**
     * @param fileName
     * @return Map containing probe set ids as keys, AffymetriProbeSets as values.
     * @throws IOException
     */
    public Map read( String fileName ) throws IOException;

    /**
     * @param is
     * @return Map containing probe set ids as keys, AffymetriProbeSets as values.
     * @throws IOException if the file format is not valid.
     */
    public Map read( InputStream is ) throws IOException;

}