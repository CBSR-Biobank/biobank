package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.model.Address;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class AddressWrapper extends ModelWrapper<Address> {

    public AddressWrapper(WritableApplicationService appService,
        Address wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public boolean checkIntegrity() {
        return false;
    }

    @Override
    protected void firePropertyChanges(Address oldWrappedObject,
        Address newWrappedObject) {
    }

    @Override
    protected Class<Address> getWrappedClass() {
        return null;
    }

    @Override
    protected DatabaseResult persistChecks() throws ApplicationException {
        return null;
    }

    public String getStreet1() {
        return wrappedObject.getStreet1();
    }

    public String getStreet2() {
        return wrappedObject.getStreet2();
    }

    public String getCity() {
        return wrappedObject.getCity();
    }

    public String getProvince() {
        return wrappedObject.getProvince();
    }

    public String getPostalCode() {
        return wrappedObject.getPostalCode();
    }

}
