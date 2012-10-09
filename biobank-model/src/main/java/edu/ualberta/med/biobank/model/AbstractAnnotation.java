package edu.ualberta.med.biobank.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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

@Audited
@MappedSuperclass
public abstract class AbstractAnnotation<T extends AbstractAnnotationType>
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    // TODO: validate, one column must be not-null (and correspond to the Type)
    // TODO: validate multiValue options

    private T type;
    private String stringValue;
    private Decimal numberValue;
    private Date dateValue;
    private AnnotationOption selectedValue;

    @NotNull(message = "{Annotation.type.NotNull}")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ANNOTATION_TYPE_ID", nullable = false)
    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
    }

    @Size(max = 100, message = "{Annotation.stringValue.Size}")
    @Column(name = "STRING_VALUE", length = 100)
    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @Valid
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "NUMBER_VALUE")),
        @AttributeOverride(name = "scale", column = @Column(name = "NUMBER_VALUE_SCALE"))
    })
    public Decimal getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(Decimal numberValue) {
        this.numberValue = numberValue;
    }

    @Column(name = "DATE_VALUE")
    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SELECTED_VALUE")
    public AnnotationOption getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(AnnotationOption selectedValue) {
        this.selectedValue = selectedValue;
    }
}
