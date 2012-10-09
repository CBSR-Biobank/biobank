package edu.ualberta.med.biobank.model.type;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public abstract class Hash
    implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final byte[] EMPTY_HASH = new byte[0];

    private byte[] bytes = EMPTY_HASH;

    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public void setBytes(byte[] bytes) {
        this.bytes = Arrays.copyOf(bytes, bytes.length);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(bytes);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Hash other = (Hash) obj;
        if (!Arrays.equals(bytes, other.bytes)) return false;
        return true;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{bytes=" + bytesToHex(getBytes()) + "}";
    }

    protected static <T extends Hash> T setBytes(T hash,
        MessageDigest algorithm, File file)
        throws IOException {
        InputStream is = null;
        try {
            DigestInputStream dis;
            is = new FileInputStream(file);
            is = new BufferedInputStream(is);
            is = dis = new DigestInputStream(is, algorithm);

            while (dis.read() != -1)
                ;

            byte[] bytes = algorithm.digest();
            hash.setBytes(bytes);
            return hash;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    protected static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'
        };
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Embeddable
    public static class MD5Hash
        extends Hash {
        private static final long serialVersionUID = 1L;

        @Override
        @NotNull(message = "{MD5Hash.bytes.NotNull}")
        @Size(min = 16, max = 16, message = "{MD5Hash.bytes.Length}")
        @Column(name = "MD5_HASH", nullable = false, columnDefinition = "BINARY(16)")
        public byte[] getBytes() {
            return super.getBytes();
        }

        public static MD5Hash fromFile(File file)
            throws NoSuchAlgorithmException, IOException {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            MD5Hash hash = setBytes(new MD5Hash(), md5, file);
            return hash;
        }
    }

    @Embeddable
    public static class SHA1Hash
        extends Hash {
        private static final long serialVersionUID = 1L;

        @Override
        @NotNull(message = "{SHA1Hash.bytes.NotNull}")
        @Size(min = 20, max = 20, message = "{SHA1Hash.bytes.Length}")
        @Column(name = "SHA1_HASH", nullable = false, columnDefinition = "BINARY(20)")
        public byte[] getBytes() {
            return super.getBytes();
        }

        public static SHA1Hash fromFile(File file)
            throws NoSuchAlgorithmException, IOException {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            SHA1Hash hash = setBytes(new SHA1Hash(), sha1, file);
            return hash;
        }
    }
}
