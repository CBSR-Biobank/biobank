package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

public class ResearchGroup extends Center {
    private static final long serialVersionUID = 1L;

    private Study study;
    private Collection<Request> requestCollection = new HashSet<Request>();

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public Collection<Request> getRequestCollection() {
        return requestCollection;
    }

    public void setRequestCollection(Collection<Request> requestCollection) {
        this.requestCollection = requestCollection;
    }
}