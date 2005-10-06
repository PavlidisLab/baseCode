package baseCode.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

//import org.apache.commons.lang.time.StopWatch;

/**
 * <p>
 * Class to convert byte arrays (e.g., Blobs) to and from other types of arrays.
 * <hr>
 * 
 * @author Kiran Keshav
 * @author Paul Pavlidis
 * @version $Id$
 */
public class ByteArrayConverter {
    private static final int CHAR_SIZE = 2;

    private static final int DOUBLE_SIZE = 8;
    private static final int INT_SIZE = 4;
    private static final int LONG_SIZE = 8;

    /**
     * Convert a byte array with one-byte-per-character ASCII encoding (aka ISO-8859-1).
     * 
     * @param barray
     * @return
     */
    public String byteArrayToAsciiString( byte[] barray ) {
        try {
            return new String( barray, "ISO-8859-1" );
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * @param barray
     * @return char[]
     */
    public char[] byteArrayToChars( byte[] barray ) {
        ByteArrayInputStream bis = new ByteArrayInputStream( barray );
        DataInputStream dis = new DataInputStream( bis );
        char[] carray = new char[barray.length / CHAR_SIZE];

        int i = 0;
        try {
            while ( true ) {
                carray[i] = dis.readChar();
                i++;
            }
        } catch ( IOException e ) {
            // do nothing.
        }

        try {
            dis.close();
            bis.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        return carray;
    }

    /**
     * @param barray
     * @return double[]
     */
    public double[] byteArrayToDoubles( byte[] barray ) {
        ByteArrayInputStream bis = new ByteArrayInputStream( barray );
        DataInputStream dis = new DataInputStream( bis );

        double[] darray = new double[barray.length / DOUBLE_SIZE];
        int i = 0;
        try {
            while ( true ) {
                darray[i] = dis.readDouble();
                i++;
            }
        } catch ( IOException e ) {
            // do nothing.
        }

        try {
            bis.close();
        } catch ( IOException e1 ) {
            e1.printStackTrace();
        }
        return darray;
    }

    /**
     * @param barray
     * @return int[]
     */
    public int[] byteArrayToInts( byte[] barray ) {
        ByteArrayInputStream bis = new ByteArrayInputStream( barray );
        DataInputStream dis = new DataInputStream( bis );
        int[] iarray = new int[barray.length / INT_SIZE];
        int i = 0;

        try {
            while ( true ) {
                iarray[i] = dis.readInt();
                i++;
            }
        } catch ( IOException e ) {
            // do nothing.
        }

        try {
            dis.close();
            bis.close();
        } catch ( IOException e1 ) {
            e1.printStackTrace();
        }

        return iarray;
    }

    /**
     * @param barray
     * @return long[] resulting from parse of the bytes.
     */
    public long[] byteArrayToLongs( byte[] barray ) {
        ByteArrayInputStream bis = new ByteArrayInputStream( barray );
        DataInputStream dis = new DataInputStream( bis );
        long[] iarray = new long[barray.length / LONG_SIZE];
        int i = 0;

        try {
            while ( true ) {
                iarray[i] = dis.readLong();
                i++;
            }
        } catch ( IOException e ) {
            // do nothing.
        }

        try {
            dis.close();
            bis.close();
        } catch ( IOException e1 ) {
            e1.printStackTrace();
        }

        return iarray;
    }

    /**
     * @param carray
     * @return byte[]
     */
    public byte[] charArrayToBytes( char[] carray ) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream( bos );

        try {
            for ( int i = 0; i < carray.length; i++ ) {
                dos.writeChar( carray[i] );
            }
            dos.close();
            bos.close();

        } catch ( IOException e ) {
            // do nothing.
        }

        return bos.toByteArray();
    }

    /**
     * @param darray
     * @return byte[]
     */
    public byte[] doubleArrayToBytes( double[] darray ) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream( bos );
        try {
            for ( int i = 0; i < darray.length; i++ ) {
                dos.writeDouble( darray[i] );
            }
        } catch ( IOException e ) {
            // do nothing
        }
        return bos.toByteArray();
    }

    /**
     * @param boolarray
     * @return byte[]
     */
    public byte[] booleanArrayToBytes( boolean[] boolarray ) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream( bos );
        try {
            for ( int i = 0; i < boolarray.length; i++ ) {
                dos.writeBoolean( boolarray[i] );
            }
        } catch ( IOException e ) {
            // do nothing
        }
        return bos.toByteArray();
    }

    /**
     * @param iarray
     * @return byte[]
     */
    public byte[] intArrayToBytes( int[] iarray ) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream( bos );
        try {
            for ( int i = 0; i < iarray.length; i++ ) {
                dos.writeInt( iarray[i] );
            }
            dos.close();
            bos.close();
        } catch ( IOException e ) {
            // do nothing
        }
        return bos.toByteArray();
    }

    /**
     * @param array
     * @return
     */
    public byte[] toBytes( Object[] array ) {
        if ( array == null ) return null;
        if ( array.length == 0 ) return new byte[] {};

        // sanity check, catches obvious errors.
        if ( array[0] == null ) throw new IllegalArgumentException( "Null values cannot be converted" );

        if ( array[0] instanceof Boolean ) {
            boolean[] toConvert = new boolean[array.length];
            for ( int i = 0; i < array.length; i++ ) {
                boolean object = ( ( Boolean ) array[i] ).booleanValue();
                toConvert[i] = object;
            }
            return booleanArrayToBytes( toConvert );
        } else if ( array[0] instanceof Double ) {
            double[] toConvert = new double[array.length];
            for ( int i = 0; i < array.length; i++ ) {
                double object = ( ( Double ) array[i] ).doubleValue();
                toConvert[i] = object;
            }
            return doubleArrayToBytes( toConvert );
        } else if ( array[0] instanceof String ) {
            StringBuffer buf = new StringBuffer();
            for ( int i = 0; i < array.length; i++ ) {
                buf.append( array[i] );
            }
            return charArrayToBytes( buf.toString().toCharArray() );

        } else if ( array[0] instanceof Integer ) {
            int[] toConvert = new int[array.length];
            for ( int i = 0; i < array.length; i++ ) {
                int object = ( ( Integer ) array[i] ).intValue();
                toConvert[i] = object;
            }
            return intArrayToBytes( toConvert );
        } else {
            throw new UnsupportedOperationException( "Can't convert " + array[0].getClass() + " to bytes" );
        }

    }

}