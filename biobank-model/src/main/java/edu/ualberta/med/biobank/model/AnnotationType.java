package edu.ualberta.med.biobank.model;

import javax.lang.model.element.AnnotationValue;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
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

/**
 * Allows a {@link Study} to collect custom named and defined pieces of data on
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
@DiscriminatorColumn(name = "DISCRIMINATOR", columnDefinition = "CHAR(4)", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AnnotationType
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Integer maxValueCount;
    private AnnotationValueType valueType;

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
     * @return the maximum number of values, per owner, that this
     *         {@link AnnotationValue} type can have, or zero if unlimited.
     */
    @NotNull(message = "{AnnotationType.maxValueCount.NotNull}")
    @Min(value = 0, message = "{AnnotationType.maxValueCount.Min}")
    @Column(name = "MAX_VALUE_COUNT", nullable = false)
    public Integer getMaxValueCount() {
        return maxValueCount;
    }

    public void setMaxValueCount(Integer maxValueCount) {
        this.maxValueCount = maxValueCount;
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
