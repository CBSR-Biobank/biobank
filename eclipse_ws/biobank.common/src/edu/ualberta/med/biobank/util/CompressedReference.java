package edu.ualberta.med.biobank.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Taken from
 * http://theplateisbad.blogspot.ca/2011/04/java-in-memory-compression.html.
 * 
 * @author loyola
 * 
 * @param <T>
 */
public class CompressedReference<T extends Serializable> implements
    Serializable {
    private static final long serialVersionUID = 7967994340450625830L;

    private byte[] theCompressedReferent = null;

    public CompressedReference(T referent) {
        try {
            compress(referent);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int size() {
        return theCompressedReferent.length;
    }

    public T get() {
        try {
            return decompress();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void compress(T referent) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream zos = new GZIPOutputStream(bos);
        ObjectOutputStream ous = new ObjectOutputStream(zos);

        ous.writeObject(referent);

        zos.finish();
        bos.flush();
        theCompressedReferent = bos.toByteArray();
        bos.close();
    }

    @SuppressWarnings("unchecked")
    private T decompress() throws IOException, ClassNotFoundException {
        T tmpObject = null;
        ByteArrayInputStream bis =
            new ByteArrayInputStream(theCompressedReferent);
        GZIPInputStream zis = new GZIPInputStream(bis);
        ObjectInputStream ois = new ObjectInputStream(zis);
        tmpObject = (T) ois.readObject();

        ois.close();

        return tmpObject;
    }
}