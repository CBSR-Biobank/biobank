package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
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
@NotUsed.List({
    @NotUsed(by = Container.class, property = "site", groups = PreDelete.class),
    @NotUsed(by = ContainerType.class, property = "site", groups = PreDelete.class)
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
}
