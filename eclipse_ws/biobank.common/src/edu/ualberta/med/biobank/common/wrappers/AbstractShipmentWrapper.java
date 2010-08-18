package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.model.AbstractShipment;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class AbstractShipmentWrapper extends ModelWrapper<AbstractShipment> {

    public AbstractShipmentWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public AbstractShipmentWrapper(WritableApplicationService appService,
        AbstractShipment ship) {
        super(appService, ship);
    }

    @Override
    public int compareTo(ModelWrapper<AbstractShipment> o) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected String[] getPropertyChangeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<AbstractShipment> getWrappedClass() {
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
