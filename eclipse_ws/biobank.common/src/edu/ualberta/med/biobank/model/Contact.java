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

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.group.PreDelete;

/**
 * A contact person associated with a collecting location. This person should be
 * the point of contact for any inquiries.
 * 
 * ET: The name of the person the Techs will contact when there is an incident
 * 
 * JM: Person that CBSR is to contact in case of an incident
 * 
 * NCI Term - Contact Person: A person acting as a channel for communication
 * between groups or on behalf of a group.
 * 
 */
@Audited
@Entity
@Table(name = "CONTACT")
@Empty(property = "studies", groups = PreDelete.class)
public class Contact extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Contact",
        "Contacts");

    @SuppressWarnings("nls")
    public static class PropertyName {
        public static final LString EMAIL_ADDRESS = bundle.trc(
            "model",
            "Email Address").format();
        public static final LString FAX_NUMBER = bundle.trc(
            "model",
            "Fax Number").format();
        public static final LString TITLE = bundle.trc(
            "model",
            "Title").format();
        public static final LString MOBILE_NUMBER = bundle.trc(
            "model",
            "Mobile Number").format();
        public static final LString NAME = bundle.trc(
            "model",
            "Contact Name").format();
        public static final LString OFFICE_NUMBER = bundle.trc(
            "model",
            "Office Number").format();
        public static final LString PAGER_NUMBER = bundle.trc(
            "model",
            "Pager Number").format();
    }

    private String name;
    private String title;
    private String mobileNumber;
    private String faxNumber;
    private String emailAddress;
    private String pagerNumber;
    private String officeNumber;
    private Set<Study> studies = new HashSet<Study>(0);
    private Clinic clinic;

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.Contact.name.NotNull}")
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

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "contacts")
    public Set<Study> getStudies() {
        return this.studies;
    }

    public void setStudies(Set<Study> studies) {
        this.studies = studies;
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
