package edu.ualberta.med.biobank.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

import edu.ualberta.med.biobank.model.type.Hash.MD5Hash;
import edu.ualberta.med.biobank.model.type.Hash.SHA1Hash;

@Audited
@Entity
@Table(name = "ATTACHMENT")
public class Attachment
    extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String fileName;
    private Date timeInserted;
    private Date timeUpdated;
    private String contentType;
    private AttachmentData data;
    private Long size;
    private SHA1Hash sha1Hash;
    private MD5Hash md5Hash;

    @NotNull(message = "{Attachment.fileName.NotNull}")
    @Length(max = 255, message = "{Attachment.fileName.Length}")
    @Column(name = "FILE_NAME", nullable = false, length = 255)
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @NotNull(message = "{Attachment.timeInserted.NotNull}")
    @Column(name = "TIME_INSERTED", nullable = false)
    public Date getTimeInserted() {
        return timeInserted;
    }

    public void setTimeInserted(Date timeInserted) {
        this.timeInserted = timeInserted;
    }

    @NotNull(message = "{Attachment.timeUpdated.NotNull}")
    @Column(name = "TIME_UPDATED", nullable = false)
    public Date getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(Date timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    /**
     * @return the content type of the file (i.e.
     *         "&lt;type&gt;/&lt;subtype&gt;").
     * @see http://tools.ietf.org/html/rfc4288#section-4.2
     */
    @NotNull(message = "{Attachment.contentType.NotNull}")
    @Length(max = 255, message = "{Attachment.contentType.Length}")
    @Column(name = "CONTENT_TYPE", nullable = false, length = 255)
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Embedded
    @Basic(fetch = FetchType.LAZY)
    public AttachmentData getData() {
        return data;
    }

    public void setData(AttachmentData data) {
        this.data = data;
    }

    @NotNull(message = "{Attachment.size.NotNull}")
    @Min(value = 0, message = "{Attachment.size.Min}")
    @Column(name = "SIZE", nullable = false)
    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @NotNull(message = "{Attachment.sha1Hash.NotNull}")
    @Embedded
    public SHA1Hash getSha1Hash() {
        return sha1Hash;
    }

    public void setSha1Hash(SHA1Hash sha1Hash) {
        this.sha1Hash = sha1Hash;
    }

    @NotNull(message = "{Attachment.md5Hash.NotNull}")
    @Embedded
    public MD5Hash getMd5Hash() {
        return md5Hash;
    }

    public void setMd5Hash(MD5Hash md5Hash) {
        this.md5Hash = md5Hash;
    }

    @Embeddable
    public static class AttachmentData
        implements Serializable {
        private static final long serialVersionUID = 1L;

        private byte[] bytes;

        @NotNull(message = "{AttachmentData.bytes.NotNull}")
        @Lob
        @Column(name = "DATA", nullable = false)
        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }
    }
}
