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

/**
 * Defines a single option for the
 * {@link edu.ualberta.med.biobank.model.type.AnnotationValueType#SELECT select}
 * types of {@link edu.ualberta.med.biobank.model.AnnotationType annotation
 * type}s. For example, this would be used to determine values a user chooses
 * from a drop-down, such as, "small", "medium", and "large".
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "ANNOTATION_OPTION",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "ANNOTATION_TYPE_ID", "VALUE" })
    })
public class AnnotationOption
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    public static final int VALUE_MAX_LENGTH = 50;

    private AnnotationType type;
    private String value;

    /**
     * @return the type of annotation (i.e the name or label) that this is an
     *         option for.
     */
    @NotNull(message = "{AnnotationOption.type.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANNOTATION_TYPE_ID", nullable = false)
    public AnnotationType getType() {
        return type;
    }

    public void setType(AnnotationType type) {
        this.type = type;
    }

    /**
     * @return the value that can be selected (think drop-down, combo-box, or
     *         radio selection option).
     */
    @NotNull(message = "{AnnotationOption.value.NotNull}")
    @Size(max = VALUE_MAX_LENGTH, message = "{AnnotationOption.value.Size}")
    @Column(name = "VALUE", nullable = false, length = VALUE_MAX_LENGTH)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
