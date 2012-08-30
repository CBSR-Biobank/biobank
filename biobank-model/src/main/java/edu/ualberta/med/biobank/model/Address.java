package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Embeddable
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String streetAddress;
    private String city;
    private String province;
    private String postalCode;
    private String pOBoxNumber;
    // TODO: move this out of address and into a contact class
    private String emailAddress;
    private String phoneNumber;
    private String faxNumber;
    private String countryISOCode;

    @NotEmpty(message = "{Address.name.NotEmpty}")
    @Length(max = 100, message = "{Address.name.Length}")
    @Column(name = "NAME", length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(max = 255, message = "{Address.streetAddress.Length}")
    @Column(name = "STREET_ADDRESS", length = 255)
    public String getStreetAddress() {
        return this.streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    @NotEmpty(message = "{Address.city.NotEmpty}")
    @Length(max = 150, message = "{Address.city.Length}")
    @Column(name = "CITY", length = 150)
    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Length(max = 50, message = "{Address.province.Length}")
    @Column(name = "PROVINCE", length = 50)
    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Length(max = 20, message = "{Address.postalCode.Length}")
    @Column(name = "POSTAL_CODE", length = 20)
    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Length(max = 50, message = "{Address.pOBoxNumber.Length}")
    @Column(name = "PO_BOX_NUMBER", length = 50)
    public String getPOBoxNumber() {
        return pOBoxNumber;
    }

    public void setPOBoxNumber(String pOBoxNumber) {
        this.pOBoxNumber = pOBoxNumber;
    }

    @Length(max = 100, message = "{Address.emailAddress.Length}")
    @Column(name = "EMAIL_ADDRESS", length = 100)
    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Length(max = 50, message = "{Address.phoneNumber.Length}")
    @Column(name = "PHONE_NUMBER", length = 50)
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Length(max = 50, message = "{Address.faxNumber.Length}")
    @Column(name = "FAX_NUMBER", length = 50)
    public String getFaxNumber() {
        return this.faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    /**
     * @return the 3-character ISO country code.
     */
    @Length(min = 3, max = 3, message = "{Location.GLN.Length}")
    @Column(name = "COUNTRY_ISO_CODE", length = 3)
    public String getCountryISOCode() {
        return this.countryISOCode;
    }

    public void setCountryISOCode(String countryISOCode) {
        this.countryISOCode = countryISOCode;
    }
}
