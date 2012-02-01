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

@Entity
@Table(name = "REQUEST")
public class Request extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Date submitted;
    private Date created;
    private Set<Dispatch> dispatchCollection = new HashSet<Dispatch>(0);
    private Set<RequestSpecimen> requestSpecimenCollection =
        new HashSet<RequestSpecimen>(0);
    private Address address;
    private ResearchGroup researchGroup;

    @Column(name = "SUBMITTED")
    public Date getSubmitted() {
        return this.submitted;
    }

    public void setSubmitted(Date submitted) {
        this.submitted = submitted;
    }

    @NotNull
    @Column(name = "CREATED", nullable = false)
    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID", updatable = false)
    public Set<Dispatch> getDispatchCollection() {
        return this.dispatchCollection;
    }

    public void setDispatchCollection(Set<Dispatch> dispatchCollection) {
        this.dispatchCollection = dispatchCollection;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "request")
    public Set<RequestSpecimen> getRequestSpecimenCollection() {
        return this.requestSpecimenCollection;
    }

    public void setRequestSpecimenCollection(
        Set<RequestSpecimen> requestSpecimenCollection) {
        this.requestSpecimenCollection = requestSpecimenCollection;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_ID", nullable = false)
    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESEARCH_GROUP_ID", nullable = false)
    public ResearchGroup getResearchGroup() {
        return this.researchGroup;
    }

    public void setResearchGroup(ResearchGroup researchGroup) {
        this.researchGroup = researchGroup;
    }
}
