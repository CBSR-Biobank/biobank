package edu.ualberta.med.biobank.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Jonathan Ferland
 * 
 * @param <T> the compressed object type
 * @see http://theplateisbad.blogspot.ca/2011/04/java-in-memory-compression.html
 */
public class CompressedReference<T extends Serializable>
    implements Serializable {
    private static final long serialVersionUID = 7967994340450625830L;

    private final byte[] zip;

    public CompressedReference(T referent) {
        try {
            zip = compress(referent);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public int size() {
        return zip.length;
    }

    /**
     * Will decompress the compressed byte array every time this method is
     * called. No internal caching.
     * 
     * @return the decompressed object.
     * @throws IOException if an IO problem decompressing
     * @throws ClassNotFoundException if the resulting decompressed class
     *             definition cannot be found
     */
    public T get() throws IOException, ClassNotFoundException {
        return decompress();
    }

    @SuppressWarnings("resource")
    private byte[] compress(T referent) throws IOException {
        OutputStream out = null;

        try {
            ObjectOutputStream oos;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            out = new GZIPOutputStream(out);
            out = oos = new ObjectOutputStream(out);

            oos.writeObject(referent);
            oos.flush();

            byte[] compressed = bos.toByteArray();
            return compressed;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private T decompress() throws IOException, ClassNotFoundException {
        InputStream in = null;

        try {
            ObjectInputStream ois;

            in = new ByteArrayInputStream(zip);
            in = new GZIPInputStream(in);
            in = ois = new ObjectInputStream(in);

            @SuppressWarnings("unchecked")
            T decompressed = (T) ois.readObject();
            return decompressed;
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}