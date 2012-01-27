package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

public class Contact extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String title;
    private String mobileNumber;
    private String faxNumber;
    private String emailAddress;
    private String pagerNumber;
    private String officeNumber;
    private Collection<Study> studyCollection = new HashSet<Study>();
    private Clinic clinic;

    @NotEmpty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPagerNumber() {
        return pagerNumber;
    }

    public void setPagerNumber(String pagerNumber) {
        this.pagerNumber = pagerNumber;
    }

    public String getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    public Collection<Study> getStudyCollection() {
        return studyCollection;
    }

    public void setStudyCollection(Collection<Study> studyCollection) {
        this.studyCollection = studyCollection;
    }

    @NotNull
    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }
}
