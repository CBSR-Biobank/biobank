package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.ShippingCompany;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ShippingCompanyWrapper extends ModelWrapper<ShippingCompany> {

    public ShippingCompanyWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ShippingCompanyWrapper(WritableApplicationService appService,
        ShippingCompany sc) {
        super(appService, sc);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        // TODO Auto-generated method stub

    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "name" };
    }

    @Override
    public Class<ShippingCompany> getWrappedClass() {
        return ShippingCompany.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        // TODO Auto-generated method stub

    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String name) {
        String old = getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange("name", old, name);
    }

    @Override
    public int compareTo(ModelWrapper<ShippingCompany> o) {
        return getName().compareTo(o.getWrappedObject().getName());
    }
}
