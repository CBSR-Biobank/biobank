package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "ADDRESS")
public class Address extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String street1;
    private String street2;
    private String city;
    private String province;
    private String postalCode;
    private String emailAddress;
    private String phoneNumber;
    private String faxNumber;
    private String country;

    @Column(name = "STREET1")
    public String getStreet1() {
        return this.street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    @Column(name = "STREET2")
    public String getStreet2() {
        return this.street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    @NotEmpty(message = "edu.ualberta.med.biobank.model.Address.city.NotEmpty")
    @Column(name = "CITY", length = 50)
    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "PROVINCE", length = 50)
    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Column(name = "POSTAL_CODE", length = 50)
    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Column(name = "EMAIL_ADDRESS", length = 100)
    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Column(name = "PHONE_NUMBER", length = 50)
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Column(name = "FAX_NUMBER", length = 50)
    public String getFaxNumber() {
        return this.faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    @Column(name = "COUNTRY", length = 50)
    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
