/*

This code taken from: Derby - Class org.apache.derbyTesting.unitTests.util.BitUtil

Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 */
package ubic.basecode.dataStructure;

/**
 * Based on code from: Derby - Class org.apache.derbyTesting.unitTests.util.BitUtil
 * 
 * @author paul
 * @version $Id$
 */
public class BitUtil {
    /**
     * Clear the bit at the specified position
     * 
     * @param bytes the byte array
     * @param position the bit to clear, starting from zero
     * @return the byte array with the cleared bit
     * @exception IndexOutOfBoundsException on bad position
     */
    public static byte[] clear( byte[] bytes, int position ) {
        if ( position >= 0 ) {
            int bytepos = position >> 3;
            if ( bytepos < bytes.length ) {
                int bitpos = 7 - position % 8;
                bytes[bytepos] &= ~( 1 << bitpos );
                return bytes;
            }
        }

        throw new IndexOutOfBoundsException( Integer.toString( position ) );
    }

    /**
     * Check to see if the specified bit is set
     * 
     * @param bytes the byte array
     * @param position the bit to check, starting from zero
     * @return true/false
     * @exception IndexOutOfBoundsException on bad position
     */
    public static boolean get( byte[] bytes, int position ) {
        if ( position >= 0 ) {
            int bytepos = position >> 3;
            if ( bytepos < bytes.length ) {
                int bitpos = 7 - position % 8;
                return ( bytes[bytepos] & 1 << bitpos ) != 0;
            }
        }
        throw new IndexOutOfBoundsException( Integer.toString( position ) );
    }

    /**
     * Set the bit at the specified position
     * 
     * @param bytes the byte array
     * @param position the bit to set, starting from zero
     * @return the byte array with the set bit
     * @exception IndexOutOfBoundsException on bad position
     */
    public static byte[] set( byte[] bytes, int position ) {
        if ( position >= 0 ) {
            int bytepos = position >> 3;
            if ( bytepos < bytes.length ) {
                int bitpos = 7 - position % 8;

                bytes[bytepos] |= 1 << bitpos;
                return bytes;
            }
        }
        throw new IndexOutOfBoundsException( Integer.toString( position ) );
    }

    /**
     * @param bytes
     * @return The number of '1' bits.
     */
    public static int count( byte[] bytes ) {
        int count = 0;
        int numbits = bytes.length * Byte.SIZE;
        for ( int position = 0; position < numbits; position++ ) {
            int bytepos = position >> 3;
            if ( bytepos < numbits ) {
                int bitpos = 7 - position % 8;
                if ( ( bytes[bytepos] & 1 << bitpos ) != 0 ) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * @param bytes
     * @return
     */
    public static String prettyPrint( byte[] bytes ) {
        StringBuilder buf = new StringBuilder();
        int numbits = bytes.length * Byte.SIZE;
        for ( int position = 0; position < numbits; position++ ) {
            int bytepos = position >> 3;
            if ( bytepos < numbits ) {
                int bitpos = 7 - position % 8;
                if ( ( bytes[bytepos] & 1 << bitpos ) != 0 ) {
                    buf.append( "1" );
                }
                buf.append( "0" );
            }
        }
        return buf.toString();

    }

}
