package ubic.basecode.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Provides a method to deep copy objects that are serializable
 */
public class DeepCopy {
    public static Object deepCopy( Object oldObj ) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( bos );
        oos.writeObject( oldObj );
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream( bos.toByteArray() ) );
        return ois.readObject();
    }
}
