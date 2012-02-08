package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@Entity
@DiscriminatorValue("ResearchGroup")
@Empty(property = "requestCollection", groups = PreDelete.class)
public class ResearchGroup extends Center {
    private static final long serialVersionUID = 1L;

    private Study study;
    private Set<Request> requestCollection = new HashSet<Request>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", unique = true)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESEARCH_GROUP_ID", updatable = false)
    public Set<Request> getRequestCollection() {
        return this.requestCollection;
    }

    public void setRequestCollection(Set<Request> requestCollection) {
        this.requestCollection = requestCollection;
    }
}
