package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.Address;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class AddressWrapper extends ModelWrapper<Address> {

    public AddressWrapper(WritableApplicationService appService,
        Address wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected Class<Address> getWrappedClass() {
        return Address.class;
    }

    public String getStreet1() {
        return wrappedObject.getStreet1();
    }

    public void setStreet1(String street1) {
        String oldStreet1 = getStreet1();
        wrappedObject.setStreet1(street1);
        propertyChangeSupport.firePropertyChange("name", oldStreet1, street1);
    }

    public String getStreet2() {
        return wrappedObject.getStreet2();
    }

    public void setStreet2(String street2) {
        String oldStreet2 = getStreet2();
        wrappedObject.setStreet2(street2);
        propertyChangeSupport.firePropertyChange("name", oldStreet2, street2);
    }

    public String getCity() {
        return wrappedObject.getCity();
    }

    public void setCity(String city) {
        String oldCity = getCity();
        wrappedObject.setCity(city);
        propertyChangeSupport.firePropertyChange("name", oldCity, city);
    }

    public String getProvince() {
        return wrappedObject.getProvince();
    }

    public void setProvince(String province) {
        String oldProvince = getProvince();
        wrappedObject.setProvince(province);
        propertyChangeSupport.firePropertyChange("name", oldProvince, province);
    }

    public String getPostalCode() {
        return wrappedObject.getPostalCode();
    }

    public void setPostalCode(String postalCode) {
        String oldPostalCode = getPostalCode();
        wrappedObject.setPostalCode(postalCode);
        propertyChangeSupport.firePropertyChange("name", oldPostalCode,
            postalCode);
    }

    @Override
    protected void firePropertyChanges(Address oldWrappedObject,
        Address newWrappedObject) {
        String[] members = new String[] { "street1", "street2", "city",
            "province", "postalCode" };

        try {
            firePropertyChanges(members, oldWrappedObject, newWrappedObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
        // no checks required for address
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        // no checks required for address
    }

}
