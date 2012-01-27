package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class Request extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Date submitted;
    private Date created;
    private Collection<Dispatch> dispatchCollection = new HashSet<Dispatch>();
    private Collection<RequestSpecimen> requestSpecimenCollection =
        new HashSet<RequestSpecimen>();
    private Address address;
    private ResearchGroup researchGroup;

    public java.util.Date getSubmitted() {
        return submitted;
    }

    public void setSubmitted(java.util.Date submitted) {
        this.submitted = submitted;
    }

    public java.util.Date getCreated() {
        return created;
    }

    public void setCreated(java.util.Date created) {
        this.created = created;
    }

    public Collection<Dispatch> getDispatchCollection() {
        return dispatchCollection;
    }

    public void setDispatchCollection(Collection<Dispatch> dispatchCollection) {
        this.dispatchCollection = dispatchCollection;
    }

    public Collection<RequestSpecimen> getRequestSpecimenCollection() {
        return requestSpecimenCollection;
    }

    public void setRequestSpecimenCollection(
        Collection<RequestSpecimen> requestSpecimenCollection) {
        this.requestSpecimenCollection = requestSpecimenCollection;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ResearchGroup getResearchGroup() {
        return researchGroup;
    }

    public void setResearchGroup(ResearchGroup researchGroup) {
        this.researchGroup = researchGroup;
    }
}
