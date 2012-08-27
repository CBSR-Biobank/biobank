package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.model.type.DecimalRange;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Allow a {@link Study} to collect custom named and defined pieces of data on
 * various entities, such as {@link Specimen}s, {@link Patient}s, and
 * {@link CollectionEvent}s.
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
@NotUsed.List({
    @NotUsed(by = Annotation.class, property = "type", groups = PreDelete.class)
})
public abstract class AnnotationType
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private Study study;
    private String name;
    private String description;

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

    @DiscriminatorValue("STR")
    public static class StringAnnotationType
        extends AnnotationType {
        private static final long serialVersionUID = 1L;
    }

    @DiscriminatorValue("NUM")
    public static class NumberAnnotationType
        extends AnnotationType {
        private static final long serialVersionUID = 1L;

        private DecimalRange range;

        @Valid
        @Embedded
        public DecimalRange getRange() {
            return range;
        }

        public void setRange(DecimalRange range) {
            this.range = range;
        }
    }

    @DiscriminatorValue("DAT")
    public static class DateAnnotationType
        extends AnnotationType {
        private static final long serialVersionUID = 1L;
    }

    @DiscriminatorValue("SEL")
    public static class SelectionAnnotationType
        extends AnnotationType {
        private static final long serialVersionUID = 1L;

        private Boolean multiValue;

        /**
         * @return true if multiple options can be selected, otherwise false.
         */
        @NotNull(message = "{AnnotationType.multiValue.NotNull}")
        @Column(name = "IS_MULTI_VALUE", nullable = false)
        public Boolean isMultiValue() {
            return multiValue;
        }

        public void setMultiValue(Boolean multiValue) {
            this.multiValue = multiValue;
        }
    }
}
