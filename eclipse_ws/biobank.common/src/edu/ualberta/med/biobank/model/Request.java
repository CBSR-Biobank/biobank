package edu.ualberta.med.biobank.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import edu.ualberta.med.biobank.validator.group.PreDelete;

/**
 * caTissue Term - Specimen Distribution: An event that results in transfer of a
 * specimen from a Repository to a Laboratory
 * 
 */
@Entity
@Table(name = "REQUEST")
public class Request extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Date submitted;
    private Date created;
    private Set<Dispatch> dispatches = new HashSet<Dispatch>(0);
    private Set<RequestSpecimen> requestSpecimens =
        new HashSet<RequestSpecimen>(0);
    private Address address;
    private ResearchGroup researchGroup;

    @Null(groups = PreDelete.class, message = "{edu.ualberta.med.biobank.model.Request.submitted.Null}")
    @Column(name = "SUBMITTED")
    public Date getSubmitted() {
        return this.submitted;
    }

    public void setSubmitted(Date submitted) {
        this.submitted = submitted;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Request.created.NotNull}")
    @Column(name = "CREATED", nullable = false)
    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID", updatable = false)
    public Set<Dispatch> getDispatches() {
        return this.dispatches;
    }

    public void setDispatches(Set<Dispatch> dispatches) {
        this.dispatches = dispatches;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "request")
    public Set<RequestSpecimen> getRequestSpecimens() {
        return this.requestSpecimens;
    }

    public void setRequestSpecimens(Set<RequestSpecimen> requestSpecimens) {
        this.requestSpecimens = requestSpecimens;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Request.address.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_ID", nullable = false)
    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Request.researchGroup.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESEARCH_GROUP_ID", nullable = false)
    public ResearchGroup getResearchGroup() {
        return this.researchGroup;
    }

    public void setResearchGroup(ResearchGroup researchGroup) {
        this.researchGroup = researchGroup;
    }
}
