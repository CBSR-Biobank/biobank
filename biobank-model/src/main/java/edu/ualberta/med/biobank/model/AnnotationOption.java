package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "ANNOTATION_OPTION",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "ANNOTATION_TYPE_ID", "VALUE" })
    })
public class AnnotationOption
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private AnnotationType type;
    private String value;

    @NotNull(message = "{AnnotationOption.type.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANNOTATION_TYPE_ID", nullable = false)
    public AnnotationType getType() {
        return type;
    }

    public void setType(AnnotationType type) {
        this.type = type;
    }

    @NotNull(message = "{AnnotationOption.value.NotNull}")
    @Size(max = 50, message = "{AnnotationOption.value.Size}")
    @Column(name = "VALUE", nullable = false, length = 50)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
