package edu.ualberta.med.biobank.model;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;

import edu.ualberta.med.biobank.model.type.Hash.MD5Hash;
import edu.ualberta.med.biobank.model.type.Hash.SHA1Hash;

@Immutable
@Entity
@Table(name = "FILE_DATA")
public class FileData
    extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private static final MimetypesFileTypeMap mimetypesFileTypeMap =
        new MimetypesFileTypeMap();

    private FileMetaData metaData;
    private byte[] compressedBytes;
    private Long compressedSize;

    @NotNull(message = "{edu.ualberta.med.biobank.model.FileData.metaData.NotNull}")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "FILE_META_DATA_ID", unique = true, nullable = false)
    public FileMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(FileMetaData metaData) {
        this.metaData = metaData;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.FileData.compressedBytes.NotNull}")
    @Lob
    @Column(name = "COMPRESSED_BYTES", nullable = false)
    public byte[] getCompressedBytes() {
        return compressedBytes;
    }

    public void setCompressedBytes(byte[] compressedBytes) {
        this.compressedBytes = compressedBytes;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.FileData.compressedSize.NotNull}")
    @Min(value = 0, message = "{edu.ualberta.med.biobank.model.FileData.size.Min}")
    @Column(name = "COMPRESSED_SIZE", nullable = false)
    public Long getCompressedSize() {
        return compressedSize;
    }

    public void setCompressedSize(Long compressedSize) {
        this.compressedSize = compressedSize;
    }

    /**
     * @return the decompressed file data.
     * @throws IOException if there was a problem decompressing the underlying
     *             compressed data.
     */
    @Transient
    public byte[] getBytes() throws IOException {
        InputStream in = null;
        ByteArrayOutputStream out = null;

        try {
            out = new ByteArrayOutputStream();

            in = new ByteArrayInputStream(compressedBytes);
            in = new GZIPInputStream(in);

            // transfer compressed bytes to the output file
            transfer(in, out);

            byte[] decompressed = out.toByteArray();
            return decompressed;
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }

    public static synchronized FileData fromFile(File file)
        throws NoSuchAlgorithmException, IOException {
        FileMetaData meta = new FileMetaData();

        meta.setName(file.getName());
        meta.setContentType(mimetypesFileTypeMap.getContentType(file));
        meta.setSize(file.length());
        meta.setCreatedAt(new Date());

        meta.setMd5Hash(MD5Hash.fromFile(file));
        meta.setSha1Hash(SHA1Hash.fromFile(file));

        byte[] compressedBytes = compress(file);

        FileData data = new FileData();
        data.setMetaData(meta);

        data.setCompressedBytes(compressedBytes);
        data.setCompressedSize((long) compressedBytes.length);

        return data;
    }

    private static byte[] compress(File file) throws IOException {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new BufferedInputStream(new FileInputStream(file));

            GZIPOutputStream zos;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            out = zos = new GZIPOutputStream(bos);

            // transfer bytes from file to the GZIP output stream
            transfer(in, out);

            // complete the GZIP file
            zos.finish();
            bos.flush();

            byte[] compressed = bos.toByteArray();
            return compressed;
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    private static void transfer(InputStream in, OutputStream out)
        throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
    }
}
