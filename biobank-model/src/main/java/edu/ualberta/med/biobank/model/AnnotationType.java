package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.model.study.CollectionEvent;
import edu.ualberta.med.biobank.model.study.Patient;
import edu.ualberta.med.biobank.model.study.SpecimenLink;
import edu.ualberta.med.biobank.model.study.Study;
import edu.ualberta.med.biobank.model.type.AnnotationValueType;
import edu.ualberta.med.biobank.model.util.CustomEnumType;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Allow a {@link Study} to collect custom named and defined pieces of data on
 * various entities, such as {@link SpecimenLink}s, {@link Patient}s, and
 * {@link CollectionEvent}s. Instances of this class defines a name and
 * description of the type of information that should be collected and determine
 * whether one or more value can be collected (see {@link #isMultiValue()}).
 * <p>
 * {@link AnnotationType} derivatives should exist in the same table, i.e.
 * {@link InheritanceType#TABLE_PER_CLASS} is <em>not</em> used ...
 * <ol>
 * <li>to prevent duplicate ids between subclasses, so only one
 * {@link AnnotationOption} table is needed, not one for every type, as
 * {@link AnnotationOption#getType()} will have a unique id across all
 * {@link AnnotationType} subclasses</li>
 * <li>because there are a relatively small number of these compared to
 * {@link Annotation}s</li>
 * </ol>
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "ANNOTATION_TYPE",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "STUDY_ID", "NAME" })
    })
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Unique(properties = { "study", "name" }, groups = PrePersist.class)
public abstract class AnnotationType
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private Study study;
    private String name;
    private String description;
    private Boolean multiValue;
    private AnnotationValueType valueType;

    /**
     * @return the {@link Study} that this {@link AnnotationType} belongs to.
     */
    @NotNull(message = "{AnnotationType.study.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", nullable = false)
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    /**
     * @return a short identifying name. The name is unique within the
     *         {@link AnnotationType}'s {@link #getStudy()}.
     */
    @Size(max = 50, message = "{AnnotationType.name.Size}")
    @NotEmpty(message = "{AnnotationType.name.NotEmpty}")
    @Column(name = "NAME", length = 50, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return an optional description.
     */
    @Size(max = 5000, message = "{AnnotationType.description.Size}")
    @Column(name = "NAME", length = 5000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return true if there can be more than one value of this type per owner,
     *         otherwise false for at most one value.
     */
    @NotNull(message = "{AnnotationType.multiValue.NotNull}")
    @Column(name = "IS_MULTI_VALUE", nullable = false)
    public Boolean isMultiValue() {
        return multiValue;
    }

    public void setMultiValue(Boolean multiValue) {
        this.multiValue = multiValue;
    }

    /**
     * @return what types of values (e.g. string, number, date, etc.) this type
     *         of annotation expects.
     */
    @NotNull(message = "{AnnotationType.valueType.NotNull}")
    @Type(
        type = "edu.ualberta.med.biobank.model.util.CustomEnumType",
        parameters = {
            @Parameter(
                name = CustomEnumType.ENUM_CLASS_NAME_PARAM,
                value = "edu.ualberta.med.biobank.model.type.AnnotationValueType"
            )
        })
    @Column(name = "VALUE_TYPE", nullable = false)
    public AnnotationValueType getValueType() {
        return valueType;
    }

    public void setValueType(AnnotationValueType valueType) {
        this.valueType = valueType;
    }
}
