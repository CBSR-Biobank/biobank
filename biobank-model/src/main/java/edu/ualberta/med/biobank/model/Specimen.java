package edu.ualberta.med.biobank.model;

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
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

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
    @NotUsed(by = DispatchSpecimen.class, property = "specimen", groups = PreDelete.class),
    @NotUsed(by = RequestSpecimen.class, property = "specimen", groups = PreDelete.class)
})
public class Specimen extends AbstractModel
    implements HasComments {
    private static final long serialVersionUID = 1L;

    private String inventoryId;
    private Vessel vessel;
    private Date timeCreated;
    private SpecimenGroup group;
    private Amount amount;
    private Boolean sourceSpecimen;
    private StudyCenter originCenter;
    private StudyCenter currentCenter;
    private Boolean usable;
    private Set<Comment> comments = new HashSet<Comment>(0);

    @NotEmpty(message = "{Specimen.inventoryId.NotEmpty}")
    @Column(name = "INVENTORY_ID", unique = true, nullable = false, length = 100)
    public String getInventoryId() {
        return this.inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    @Valid
    @NotNull(message = "{Specimen.amount.NotNull}")
    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    @NotNull(message = "{Specimen.timeCreated.NotNull}")
    @Column(name = "TIME_CREATED")
    public Date getTimeCreated() {
        return this.timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
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

    @NotNull(message = "{Specimen.usable.NotNull}")
    @Column(name = "IS_USABLE")
    public Boolean isUsable() {
        return usable;
    }

    public void setUsable(Boolean usable) {
        this.usable = usable;
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
}
