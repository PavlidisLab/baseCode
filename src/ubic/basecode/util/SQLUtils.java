/*
 * The basecode project
 * 
 * Copyright (c) 2005 Columbia University
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package ubic.basecode.util;

import java.sql.Blob;
import java.sql.SQLException;

import ubic.basecode.io.ByteArrayConverter;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class SQLUtils {

    /**
     * Convert a java.sql.Blob array to a string
     * 
     * @param exonStarts
     * @return
     * @throws SQLException
     */
    public static String blobToString( Blob exonStarts ) throws SQLException {
        byte[] bytes = exonStarts.getBytes( 1L, ( int ) exonStarts.length() );
        ByteArrayConverter bac = new ByteArrayConverter();
        return bac.byteArrayToAsciiString( bytes );
    }
}
