package edu.ualberta.med.biobank.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import edu.ualberta.med.biobank.model.type.Hash.MD5Hash;
import edu.ualberta.med.biobank.model.type.Hash.SHA1Hash;

@Entity
@Table(name = "ATTACHMENT")
public class Attachment
    extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    // TODO: randomly generated file ids so people can't just read anyone's
    // file. OR, require the hash with the file.

    private String fileName;
    private User insertedBy;
    private String description;
    private Date timeInserted;
    private String contentType;
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

    @NotNull(message = "{Attachment.insertedBy.NotNull}")
    @Column(name = "INSERTED_BY_USER_ID", nullable = false)
    public User getInsertedBy() {
        return insertedBy;
    }

    public void setInsertedBy(User insertedBy) {
        this.insertedBy = insertedBy;
    }

    @Length(max = 255, message = "{Attachment.description.Length}")
    @Column(name = "DESCRIPTION", length = 255)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull(message = "{Attachment.timeInserted.NotNull}")
    @Column(name = "TIME_INSERTED", nullable = false)
    public Date getTimeInserted() {
        return timeInserted;
    }

    public void setTimeInserted(Date timeInserted) {
        this.timeInserted = timeInserted;
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
}
