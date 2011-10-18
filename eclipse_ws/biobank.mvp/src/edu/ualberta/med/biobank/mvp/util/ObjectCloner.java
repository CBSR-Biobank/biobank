package edu.ualberta.med.biobank.mvp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectCloner {
    // so that nobody can accidentally create an ObjectCloner object
    private ObjectCloner() {
    }

    // returns a deep copy of an object
    public static <E> E deepCopy(E oldObj) {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(); // A
            oos = new ObjectOutputStream(bos); // B
            // serialize and pass the object
            oos.writeObject(oldObj); // C
            oos.flush(); // D
            ByteArrayInputStream bin =
                new ByteArrayInputStream(bos.toByteArray()); // E
            ois = new ObjectInputStream(bin); // F
            // return the new object
            @SuppressWarnings("unchecked")
            E readObject = (E) ois.readObject();
            return readObject; // G
        }
        catch (Exception e) {
        }
        finally {
            try {
                oos.close();
                ois.close();
            }
            catch (IOException e) {
            }
        }
        return null;
    }

}
