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

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
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
}
