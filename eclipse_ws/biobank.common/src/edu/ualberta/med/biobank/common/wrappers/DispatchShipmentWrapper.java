package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.model.DispatchShipment;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchShipmentWrapper extends
    AbstractShipmentWrapper<DispatchShipment> {

    public DispatchShipmentWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchShipmentWrapper(WritableApplicationService appService,
        DispatchShipment ship) {
        super(appService, ship);
        // TODO Auto-generated constructor stub
    }

    @Override
    public int compareTo(ModelWrapper<DispatchShipment> o) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected String[] getPropertyChangeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<DispatchShipment> getWrappedClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void deleteChecks() throws Exception {
        // TODO Auto-generated method stub

    }

}
