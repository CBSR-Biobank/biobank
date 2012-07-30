package edu.ualberta.med.biobank.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * caTissue Term - Aliquot: Pertaining to a portion of the whole; any one of two
 * or more samples of something, of the same volume or weight.
 * 
 * NCI Term - Specimen: A part of a thing, or of several things, taken to
 * demonstrate or to determine the character of the whole, e.g. a substance, or
 * portion of material obtained for use in testing, examination, or study;
 * particularly, a preparation of tissue or bodily fluid taken for examination
 * or diagnosis.
 */
@Audited
@Entity
@Table(name = "SPECIMEN")
@Unique(properties = "inventoryId", groups = PrePersist.class)
@NotUsed.List({
    @NotUsed(by = Specimen.class, property = "parentSpecimen", groups = PreDelete.class),
    @NotUsed(by = DispatchSpecimen.class, property = "specimen", groups = PreDelete.class),
    @NotUsed(by = RequestSpecimen.class, property = "specimen", groups = PreDelete.class)
})
public class Specimen extends AbstractModel
    implements HasComments {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Specimen",
        "Specimens");

    @SuppressWarnings("nls")
    public static class PropertyName {
        public static final LString INVENTORY_ID = bundle.trc(
            "model",
            "Inventory Id").format();
        public static final LString CHILD_SPECIMENS = bundle.trc(
            "model",
            "Child Specimens").format();
        public static final LString CURRENT_CENTER = bundle.trc(
            "model",
            "Current Center").format();
        public static final LString CREATED_AT = bundle.trc(
            "model",
            "Created At").format();
        public static final LString PARENT_SPECIMEN = bundle.trc(
            "model",
            "Parent Specimen").format();
        public static final LString QUANTITY = bundle.trc(
            "model",
            "Quantity").format();
        public static final LString TOP_SPECIMEN = bundle.trc(
            "model",
            "Top Specimen").format();
    }

    private String inventoryId;
    private BigDecimal quantity;
    private Date timeCreated;
    private Specimen topSpecimen = this;
    private Specimen parentSpecimen;
    private CollectionEvent collectionEvent;
    private Boolean sourceSpecimen;
    private StudyCenter originCenter;
    private StudyCenter currentCenter;
    private SpecimenType specimenType;
    private SpecimenPosition specimenPosition;
    private ProcessingEvent processingEvent;
    private Set<Comment> comments = new HashSet<Comment>(0);
    private Boolean usable;

    @NotEmpty(message = "{Specimen.inventoryId.NotEmpty}")
    @Column(name = "INVENTORY_ID", unique = true, nullable = false, length = 100)
    public String getInventoryId() {
        return this.inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    @Digits(integer = 10, fraction = 10, message = "{Specimen.quantity.Digits}")
    @Column(name = "QUANTITY", precision = 10, scale = 10)
    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @NotNull(message = "{Specimen.timeCreated.NotNull}")
    @Column(name = "TIME_CREATED")
    public Date getTimeCreated() {
        return this.timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOP_SPECIMEN_ID")
    public Specimen getTopSpecimen() {
        return this.topSpecimen;
    }

    public void setTopSpecimen(Specimen topSpecimen) {
        this.topSpecimen = topSpecimen;
    }

    @NotNull(message = "{Specimen.collectionEvent.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COLLECTION_EVENT_ID", nullable = false)
    public CollectionEvent getCollectionEvent() {
        return this.collectionEvent;
    }

    public void setCollectionEvent(CollectionEvent collectionEvent) {
        this.collectionEvent = collectionEvent;
    }

    @NotNull(message = "{Specimen.currentCenter.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENT_STUDY_CENTER_ID", nullable = false)
    public StudyCenter getCurrentCenter() {
        return this.currentCenter;
    }

    public void setCurrentCenter(StudyCenter currentCenter) {
        this.currentCenter = currentCenter;
    }

    @NotNull(message = "{Specimen.originCenter.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORIGIN_STUDY_CENTER_ID", nullable = false)
    public StudyCenter getOriginCenter() {
        return originCenter;
    }

    public void setOriginCenter(StudyCenter originCenter) {
        this.originCenter = originCenter;
    }

    @NotNull(message = "{Specimen.sourceSpecimen.NotNull}")
    @Column(name = "SOURCE_SPECIMEN")
    public Boolean isSourceSpecimen() {
        return sourceSpecimen;
    }

    public void setSourceSpecimen(Boolean sourceSpecimen) {
        this.sourceSpecimen = sourceSpecimen;
    }

    @NotNull(message = "{Specimen.specimenType.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_TYPE_ID", nullable = false)
    public SpecimenType getSpecimenType() {
        return this.specimenType;
    }

    public void setSpecimenType(SpecimenType specimenType) {
        this.specimenType = specimenType;
    }

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "specimen", orphanRemoval = true)
    public SpecimenPosition getSpecimenPosition() {
        return this.specimenPosition;
    }

    public void setSpecimenPosition(SpecimenPosition specimenPosition) {
        if (this.specimenPosition != null) {
            this.specimenPosition.setSpecimen(null);
        }
        this.specimenPosition = specimenPosition;
        if (specimenPosition != null) {
            specimenPosition.setSpecimen(this);
        }
    }

    @Override
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "SPECIMEN_COMMENT",
        joinColumns = { @JoinColumn(name = "SPECIMEN_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getComments() {
        return this.comments;
    }

    @Override
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @NotNull(message = "{Specimen.usable.NotNull}")
    @Column(name = "IS_USABLE")
    public Boolean isUsable() {
        return usable;
    }

    public void setUsable(Boolean usable) {
        this.usable = usable;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESSING_EVENT_ID")
    public ProcessingEvent getProcessingEvent() {
        return this.processingEvent;
    }

    public void setProcessingEvent(ProcessingEvent processingEvent) {
        this.processingEvent = processingEvent;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_SPECIMEN_ID")
    public Specimen getParentSpecimen() {
        return this.parentSpecimen;
    }

    public void setParentSpecimen(Specimen parentSpecimen) {
        this.parentSpecimen = parentSpecimen;
    }
}
