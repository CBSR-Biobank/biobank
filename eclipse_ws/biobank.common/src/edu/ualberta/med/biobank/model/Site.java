package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.group.PreDelete;

/**
 * A storage location
 * 
 * ET: The laboratory hosting the storage facilities
 * 
 * caTissue Term - Site: A physical location involved in biospecimen collection,
 * storage, processing, or utilization.
 * 
 * NCI Term - Repository: A facility where things can be deposited for storage
 * or safekeeping.
 */
@Audited
@Entity
@DiscriminatorValue("Site")
@Empty.List({
    @Empty(property = "containers", groups = PreDelete.class),
    @Empty(property = "containerTypes", groups = PreDelete.class),
    @Empty(property = "processingEvents", groups = PreDelete.class)
})
public class Site extends Center {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Site",
        "Sites");

    private Set<Study> studies = new HashSet<Study>(0);
    private Set<ContainerType> containerTypes = new HashSet<ContainerType>(0);
    private Set<Container> containers = new HashSet<Container>(
        0);

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "SITE_STUDY",
        joinColumns = { @JoinColumn(name = "SITE_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "STUDY_ID", nullable = false, updatable = false) })
    public Set<Study> getStudies() {
        return this.studies;
    }

    public void setStudies(Set<Study> studies) {
        this.studies = studies;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "site")
    @NotAudited
    public Set<ContainerType> getContainerTypes() {
        return this.containerTypes;
    }

    public void setContainerTypes(Set<ContainerType> containerTypes) {
        this.containerTypes = containerTypes;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "site")
    public Set<Container> getContainers() {
        return this.containers;
    }

    public void setContainers(Set<Container> containers) {
        this.containers = containers;
    }

}
