/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package baseCode.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import junit.framework.TestCase;

/**
 * @author pavlidis
 * @version $Id$
 */
public class NetUtilsTest extends TestCase {

    private static Log log = LogFactory.getLog( NetUtilsTest.class.getName() );

    final public void testFtpFileSize() throws Exception {
        FTPClient f;
        try {
            f = NetUtils.connect( FTP.BINARY_FILE_TYPE, "ftp.ncbi.nlm.nih.gov", "anonymous", "paul@ubic.ca" );
        } catch ( IOException ignore ) {
            log.warn( "Could not connect to ftp.ncbi.nlm.nih.gov, skipping test" );
            return;
        }
        long actualValue = NetUtils.ftpFileSize( f, "genomes/Pan_troglodytes/WGS_12Dec2003/WIBR.seq007.fa" );
        long expectedValue = 131446617;
        assertEquals( expectedValue, actualValue, 100000 ); // don't really care if they change the file size....
    }

    final public void testFtpFileSizeDoesntExist() throws Exception {
        FTPClient f;
        try {
            f = NetUtils.connect( FTP.BINARY_FILE_TYPE, "ftp.ncbi.nlm.nih.gov", "anonymous", "paul@ubic.ca" );
        } catch ( IOException ignore ) {
            log.warn( "Could not connect to ftp.ncbi.nlm.nih.gov, skipping test" );
            return;
        }
        try {
            NetUtils.ftpFileSize( f, "genomes/Pan_trogl_____odytes/WGS_12Dec2003/WIBR.seq007.fa" );
            fail( "Should have gotten a FileNotFoundException" );
        } catch ( FileNotFoundException e ) {
            // ok
        }

    }

}
