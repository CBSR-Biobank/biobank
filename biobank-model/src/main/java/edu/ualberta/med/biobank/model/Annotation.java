package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.type.Decimal;

/**
 * Holds the annotation value for a
 * {@link edu.ualberta.med.biobank.model.AnnotationType type of annotation}.
 * Derived classes determine what the annotation is on, such as a
 * {@link edu.ualberta.med.biobank.model.study.SpecimenLink specimen link}, a
 * {@link edu.ualberta.med.biobank.model.study.Patient patient}, or a
 * {@link edu.ualberta.med.biobank.model.study.CollectionEvent collection event}
 * .
 * 
 * @author Jonathan Ferland
 * 
 * @param <T> the specific {@link edu.ualberta.med.biobank.model.AnnotationType
 *            type of annotation} this annotation is a value for
 */
@Audited
@MappedSuperclass
public abstract class Annotation<T extends AnnotationType>
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    // TODO: validate, one column must be not-null (and correspond to the Type)
    // TODO: validate multiValue options

    private T type;
    private String stringValue;
    private Decimal numberValue;
    private AnnotationOption selectedValue;

    /**
     * @return the type of annotation this is, which determines its name or
     *         label.
     */
    @NotNull(message = "{Annotation.type.NotNull}")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ANNOTATION_TYPE_ID", nullable = false)
    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
    }

    /**
     * @return the string value of this annotation, or null if this annotation
     *         is not a
     *         {@link edu.ualberta.med.biobank.model.type.AnnotationValueType#STRING
     *         string} type.
     */
    @Size(max = 100, message = "{Annotation.stringValue.Size}")
    @Column(name = "STRING_VALUE", length = 100)
    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    /**
     * @return the number value or date (in seconds) of this annotation, or null
     *         if this annotation is neither a
     *         {@link edu.ualberta.med.biobank.model.type.AnnotationValueType#NUMBER
     *         number} nor
     *         {@link edu.ualberta.med.biobank.model.type.AnnotationValueType#DATE
     *         date} type.
     */
    @Valid
    @Embedded
    public Decimal getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(Decimal numberValue) {
        this.numberValue = numberValue;
    }

    /**
     * @return the selected value of this annotation, or null if this annotation
     *         is not a
     *         {@link edu.ualberta.med.biobank.model.type.AnnotationValueType#SELECT
     *         select} type
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SELECTED_VALUE")
    public AnnotationOption getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(AnnotationOption selectedValue) {
        this.selectedValue = selectedValue;
    }
}
