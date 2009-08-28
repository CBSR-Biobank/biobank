package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.model.ContainerType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ContainerTypeWrapper extends ModelWrapper<ContainerType> {

    public ContainerTypeWrapper(WritableApplicationService appService,
        ContainerType wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected void firePropertyChanges(ContainerType oldWrappedObject,
        ContainerType newWrappedObject) {
        // TODO Auto-generated method stub

    }

    @Override
    protected DatabaseResult persistChecks() throws ApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Class<ContainerType> getWrappedClass() {
        return ContainerType.class;
    }

}
