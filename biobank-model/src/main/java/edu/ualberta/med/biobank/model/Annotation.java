package edu.ualberta.med.biobank.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.type.Decimal;

@Audited
@Entity
@Table(name = "ANNOTATION")
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Annotation
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    // TODO: validate, one column must be not-null (and correspond to the Type)
    // TODO: validate multiValue options

    private AnnotationType type;
    private String stringValue;
    private Decimal numberValue;
    private Date dateValue;
    private Set<AnnotationOption> options = new HashSet<AnnotationOption>(0);

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

    @Column(name = "DATE_VALUE")
    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "ANNOTATION_ANNOTATION_OPTION",
        joinColumns = { @JoinColumn(name = "ANNOTATION_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "ANNOTATION_OPTION_ID", unique = false, nullable = false, updatable = false) })
    public Set<AnnotationOption> getOptions() {
        return options;
    }

    public void setOptions(Set<AnnotationOption> options) {
        this.options = options;
    }
}
