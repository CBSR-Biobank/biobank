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

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.group.PreDelete;

/**
 *
 * Code Changes -
 * 		1> Convert ManyToOne relation to ManyToMany for getting & setting Studies associated with a Research Group
 * 		2> Utilize new link table RESEARCHGROUP_STUDY (similar to SITE_STUDY with same columns name in case in future we want to use the same table)
 * 		3> Change the class variable to hold a set of Studies associated to a Research Group
 *
 * @author OHSDEV
 *
 */
@Entity
@DiscriminatorValue("ResearchGroup")
@Empty(property = "requests", groups = PreDelete.class)
public class ResearchGroup extends Center {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Research Group",
        "Research Groups");

    private Set<Study> studies = new HashSet<Study>(0);		//OHSDEV
    private Set<Request> requests = new HashSet<Request>(0);

    //OHSDEV -->
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "RESEARCHGROUP_STUDY",
        joinColumns = { @JoinColumn(name = "SITE_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "STUDY_ID", nullable = false, updatable = false) })
    public Set<Study> getStudies() {
        return this.studies;
    }

    public void setStudies(Set<Study> studies) {
        this.studies = studies;
    }
    // <-- OHSDEV

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESEARCH_GROUP_ID", updatable = false)
    public Set<Request> getRequests() {
        return this.requests;
    }

    public void setRequests(Set<Request> requests) {
        this.requests = requests;
    }
}