/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.util;

import java.math.BigInteger;
import java.sql.Blob;
import java.sql.SQLException;

import ubic.basecode.io.ByteArrayConverter;

/**
 * @author pavlidis
 * 
 */
public class SQLUtils {

    private static ByteArrayConverter bac = new ByteArrayConverter();

    /**
     * Convert a java.sql.Blob array to a string
     * 
     * @param exonStarts
     * @return
     * @throws SQLException
     */
    public static String blobToString( Blob exonStarts ) throws SQLException {
        byte[] bytes = exonStarts.getBytes( 1L, ( int ) exonStarts.length() );
        return bac.byteArrayToAsciiString( bytes );
    }

    public static Long asId( Object o ) {
        if ( o == null ) return null;
        if ( o instanceof BigInteger ) {
            return ( ( BigInteger ) o ).longValue();
        } else if ( o instanceof Long ) {
            return ( Long ) o;
        }
        throw new IllegalArgumentException( "Cannot figure out how to turn object to an id: " + o.getClass().getName() );
    }
}
