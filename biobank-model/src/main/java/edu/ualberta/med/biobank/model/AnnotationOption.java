package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "ANNOTATION_OPTION",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "ANNOTATION_TYPE", "VALUE" })
    })
public class AnnotationOption
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private String value;

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
