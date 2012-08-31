package edu.ualberta.med.biobank.model.type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@Embeddable
public class Location {
    private String name;
    private String street;
    private String city;
    private String province;
    private String postalCode;
    private String pOBoxNumber;
    private String countryISOCode;

    @NotNull(message = "{Address.name.NotNull")
    @Length(min = 1, max = 100, message = "{Address.name.Length}")
    @Column(name = "NAME", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(max = 255, message = "{Address.street.Length}")
    @Column(name = "STREET", length = 255)
    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

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
