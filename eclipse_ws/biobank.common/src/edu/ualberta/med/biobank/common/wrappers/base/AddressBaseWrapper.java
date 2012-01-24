/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.AddressPeer;

public class AddressBaseWrapper extends ModelWrapper<Address> {

    public AddressBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public AddressBaseWrapper(WritableApplicationService appService,
        Address wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Address> getWrappedClass() {
        return Address.class;
    }

    @Override
    public Property<Integer, ? super Address> getIdProperty() {
        return AddressPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Address>> getProperties() {
        return AddressPeer.PROPERTIES;
    }

    public String getStreet2() {
        return getProperty(AddressPeer.STREET2);
    }

    public void setStreet2(String street2) {
        String trimmed = street2 == null ? null : street2.trim();
        setProperty(AddressPeer.STREET2, trimmed);
    }

    public String getStreet1() {
        return getProperty(AddressPeer.STREET1);
    }

    public void setStreet1(String street1) {
        String trimmed = street1 == null ? null : street1.trim();
        setProperty(AddressPeer.STREET1, trimmed);
    }

    public String getFaxNumber() {
        return getProperty(AddressPeer.FAX_NUMBER);
    }

    public void setFaxNumber(String faxNumber) {
        String trimmed = faxNumber == null ? null : faxNumber.trim();
        setProperty(AddressPeer.FAX_NUMBER, trimmed);
    }

    public String getPostalCode() {
        return getProperty(AddressPeer.POSTAL_CODE);
    }

    public void setPostalCode(String postalCode) {
        String trimmed = postalCode == null ? null : postalCode.trim();
        setProperty(AddressPeer.POSTAL_CODE, trimmed);
    }

    public String getPhoneNumber() {
        return getProperty(AddressPeer.PHONE_NUMBER);
    }

    public void setPhoneNumber(String phoneNumber) {
        String trimmed = phoneNumber == null ? null : phoneNumber.trim();
        setProperty(AddressPeer.PHONE_NUMBER, trimmed);
    }

    public String getName() {
        return getProperty(AddressPeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(AddressPeer.NAME, trimmed);
    }

    public String getProvince() {
        return getProperty(AddressPeer.PROVINCE);
    }

    public void setProvince(String province) {
        String trimmed = province == null ? null : province.trim();
        setProperty(AddressPeer.PROVINCE, trimmed);
    }

    public String getEmailAddress() {
        return getProperty(AddressPeer.EMAIL_ADDRESS);
    }

    public void setEmailAddress(String emailAddress) {
        String trimmed = emailAddress == null ? null : emailAddress.trim();
        setProperty(AddressPeer.EMAIL_ADDRESS, trimmed);
    }

    public String getCountry() {
        return getProperty(AddressPeer.COUNTRY);
    }

    public void setCountry(String country) {
        String trimmed = country == null ? null : country.trim();
        setProperty(AddressPeer.COUNTRY, trimmed);
    }

    public String getCity() {
        return getProperty(AddressPeer.CITY);
    }

    public void setCity(String city) {
        String trimmed = city == null ? null : city.trim();
        setProperty(AddressPeer.CITY, trimmed);
    }

}
