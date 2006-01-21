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
     * @param f
     * @param seekFile
     * @param outputFile
     * @param force
     * @return boolean indicating success or failure.
     * @throws IOException
     */
    public static boolean ftpDownloadFile( FTPClient f, String seekFile, File outputFile, boolean force )
            throws IOException {
        boolean success = false;

        assert f != null && f.isConnected() : "No FTP connection is available";
        long expectedSize = checkForFile( f, seekFile );

        if ( outputFile.exists() && outputFile.length() == expectedSize && !force ) {
            log.warn( "Output file " + outputFile + " already exists with correct size. Will not re-download" );
            return true;
        }

        OutputStream os = new FileOutputStream( outputFile );

        log.info( "Seeking file " + seekFile + " with size " + expectedSize + " bytes" );
        success = f.retrieveFile( seekFile, os );
        os.close();
        if ( !success ) {
            throw new IOException( "Failed to complete download of " + seekFile );
        }
        return success;
    }

    /**
     * determine if a file exists on the remote server.
     * 
     * @param f
     * @param seekFile
     * @return the size of the file
     * @throws FileNotFoundException if the file does not exist.
     * @throws IOException on other IO errors.
     */
    public static long checkForFile( FTPClient f, String seekFile ) throws IOException {
        FTPFile[] allfilesInGroup = f.listFiles( seekFile );
        if ( allfilesInGroup == null || allfilesInGroup.length == 0 ) {
            throw new FileNotFoundException( "File " + seekFile + " does not seem to exist on the remote host" );
        }
        return allfilesInGroup[0].getSize();
    }

    /**
     * @param accession
     * @param outputFileName
     * @param outputFile
     * @param seekFile
     * @return boolean indicating success or failure.
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static boolean ftpDownloadFile( FTPClient f, String seekFile, String outputFileName, boolean force )
            throws IOException, FileNotFoundException {
        return ftpDownloadFile( f, seekFile, new File( outputFileName ), force );
    }
}
