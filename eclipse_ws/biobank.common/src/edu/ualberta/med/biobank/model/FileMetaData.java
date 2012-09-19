package edu.ualberta.med.biobank.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import edu.ualberta.med.biobank.model.type.Hash.MD5Hash;
import edu.ualberta.med.biobank.model.type.Hash.SHA1Hash;

@Immutable
@Entity
@Table(name = "FILE_META_DATA")
public class FileMetaData
    extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    // TODO: randomly generated file ids so people can't just read anyone's
    // file. OR, require the hash with the file.

    private String name;
    private String description;
    private Date createdAt;
    private String contentType;
    private Long size;
    private SHA1Hash sha1Hash;
    private MD5Hash md5Hash;

    @NotNull(message = "{edu.ualberta.med.biobank.model.FileMetaData.name.NotNull}")
    @Length(max = 255, message = "{FileMetaData.name.Length}")
    @Column(name = "NAME", nullable = false, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(max = 255, message = "{edu.ualberta.med.biobank.model.FileMetaData.description.Length}")
    @Column(name = "DESCRIPTION", length = 255)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.FileMetaData.createdAt.NotNull}")
    @Column(name = "CREATED_AT", nullable = false)
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the content type of the file (i.e.
     *         "&lt;type&gt;/&lt;subtype&gt;").
     * @see http://tools.ietf.org/html/rfc4288#section-4.2
     */
    @NotNull(message = "{edu.ualberta.med.biobank.model.FileMetaData.contentType.NotNull}")
    @Length(max = 255, message = "{FileMetaData.contentType.Length}")
    @Column(name = "CONTENT_TYPE", nullable = false, length = 255)
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.FileMetaData.size.NotNull}")
    @Min(value = 0, message = "{FileMetaData.size.Min}")
    @Column(name = "SIZE", nullable = false)
    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.FileMetaData.sha1Hash.NotNull}")
    @Embedded
    public SHA1Hash getSha1Hash() {
        return sha1Hash;
    }

    public void setSha1Hash(SHA1Hash sha1Hash) {
        this.sha1Hash = sha1Hash;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.FileMetaData.md5Hash.NotNull}")
    @Embedded
    public MD5Hash getMd5Hash() {
        return md5Hash;
    }

    public void setMd5Hash(MD5Hash md5Hash) {
        this.md5Hash = md5Hash;
    }
}
