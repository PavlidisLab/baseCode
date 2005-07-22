/*
 * The baseCode project
 * 
 * Copyright (c) 2005 Columbia University
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class NetUtils {

    private static Log log = LogFactory.getLog( NetUtils.class.getName() );

    /**
     * Convenient method to get a FTP connection.
     * 
     * @param host
     * @param login
     * @param password
     * @param mode
     * @return
     * @throws SocketException
     * @throws IOException
     */
    public static FTPClient connect( int mode, String host, String loginName, String password ) throws SocketException,
            IOException {
        FTPClient f = new FTPClient();

        boolean success = false;
        f.connect( host );
        int reply = f.getReplyCode();
        if ( FTPReply.isPositiveCompletion( reply ) ) success = f.login( loginName, password );
        if ( !success ) {
            f.disconnect();
            throw new IOException( "Couldn't connect to " + host );
        }
        f.setFileType( mode );
        log.info( "Connected to " + host );
        return f;
    }

    /**
     * @param accession
     * @param outputFileName
     * @param outputFile
     * @param seekFile
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static boolean ftpDownloadFile( FTPClient f, String seekFile, String outputFileName, boolean force )
            throws IOException, FileNotFoundException {
        boolean success = false;
        File outputFile = new File( outputFileName );
        assert f != null && f.isConnected() : "No FTP connection is available";
        FTPFile[] allfilesInGroup = f.listFiles( seekFile );
        if ( allfilesInGroup.length == 0 ) {
            log.error( "File " + seekFile + " does not seem to exist on the remote host" );
            return false;
        }

        long expectedSize = allfilesInGroup[0].getSize();
        if ( outputFile.exists() && outputFile.length() == expectedSize && !force ) {
            log.warn( "Output file" + outputFileName + " already exists with correct size. Will not re-download" );
            return true;
        }

        OutputStream os = new FileOutputStream( outputFileName );

        log.info( "Seeking file " + seekFile + " with size " + expectedSize + " bytes" );
        success = f.retrieveFile( seekFile, os );
        os.close();
        if ( !success ) {
            log.error( "Failed to complete download of " + seekFile );
        }
        return success;
    }
}
