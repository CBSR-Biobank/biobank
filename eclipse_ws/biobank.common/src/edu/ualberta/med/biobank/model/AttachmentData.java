package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;

@Immutable
@Entity
@Table(name = "ATTACHMENT_DATA")
public class AttachmentData
    extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Attachment attachment;
    private byte[] bytes;

    @NotNull(message = "{edu.ualberta.med.biobank.model.AttachmentData.attachment.NotNull}")
    @OneToOne
    @JoinColumn(name = "ATTACHMENT_ID", unique = true, nullable = false)
    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    @NotNull(message = "{AttachmentData.bytes.NotNull}")
    @Lob
    @Column(name = "BYTES", nullable = false)
    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
