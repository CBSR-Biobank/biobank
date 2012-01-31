package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.Email;
import org.hibernate.validator.NotEmpty;

@Entity
@Table(name = "CONTACT")
public class Contact extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String title;
    private String mobileNumber;
    private String faxNumber;
    private String emailAddress;
    private String pagerNumber;
    private String officeNumber;
    private Set<Study> studyCollection = new HashSet<Study>(0);
    private Clinic clinic;

    @NotEmpty
    @Column(name = "NAME", length = 100)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "TITLE", length = 100)
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "MOBILE_NUMBER", length = 50)
    public String getMobileNumber() {
        return this.mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Column(name = "FAX_NUMBER", length = 50)
    public String getFaxNumber() {
        return this.faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    // TODO: write an email check that allows null @Email
    @Column(name = "EMAIL_ADDRESS", length = 50)
    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Column(name = "PAGER_NUMBER", length = 50)
    public String getPagerNumber() {
        return this.pagerNumber;
    }

    public void setPagerNumber(String pagerNumber) {
        this.pagerNumber = pagerNumber;
    }

    @Column(name = "OFFICE_NUMBER", length = 50)
    public String getOfficeNumber() {
        return this.officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "contactCollection")
    public Set<Study> getStudyCollection() {
        return this.studyCollection;
    }

    public void setStudyCollection(Set<Study> studyCollection) {
        this.studyCollection = studyCollection;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLINIC_ID", nullable = false)
    public Clinic getClinic() {
        return this.clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }
}
