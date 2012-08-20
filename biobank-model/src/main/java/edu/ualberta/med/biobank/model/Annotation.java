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

import edu.ualberta.med.biobank.model.type.Decimal;

@MappedSuperclass
public abstract class Annotation
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    // TODO: validate, one column must be not-null (and correspond to the Type)

    private AnnotationType type;
    private String stringValue;
    private Decimal numberValue;
    private AnnotationOption optionId;

    @NotNull(message = "{Annotation.type.NotNull}")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ANNOTATION_TYPE_ID", nullable = false)
    public AnnotationType getType() {
        return type;
    }

    public void setType(AnnotationType type) {
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
    public Decimal getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(Decimal numberValue) {
        this.numberValue = numberValue;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANNOTATION_OPTION_ID")
    public AnnotationOption getOptionId() {
        return optionId;
    }

    public void setOptionId(AnnotationOption optionId) {
        this.optionId = optionId;
    }
}
