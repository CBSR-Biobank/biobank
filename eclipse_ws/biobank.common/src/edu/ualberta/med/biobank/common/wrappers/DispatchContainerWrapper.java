package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.internal.AbstractPositionWrapper;
import edu.ualberta.med.biobank.model.DispatchContainer;
import edu.ualberta.med.biobank.model.DispatchPosition;
import edu.ualberta.med.biobank.model.DispatchShipment;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchContainerWrapper extends
    AbstractContainerWrapper<DispatchContainer, DispatchPosition> {

    private DispatchShipmentWrapper shipment;

    public DispatchContainerWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchContainerWrapper(WritableApplicationService appService,
        DispatchContainer container) {
        super(appService, container);
    }

    @Override
    public Class<DispatchContainer> getWrappedClass() {
        return DispatchContainer.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        // FIXME test shipment not null
        // FIXME test sender not null
        // FIXME test receiver not null
        super.persistChecks();
    }

    @Override
    protected void deleteChecks() throws Exception {
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "shipment" };
    }

    @Override
    public int compareTo(ModelWrapper<DispatchContainer> o) {
        return 0;
    }

    public DispatchShipmentWrapper getShipment() {
        if (shipment == null) {
            DispatchShipment s = wrappedObject.getShipment();
            if (s == null)
                return null;
            shipment = new DispatchShipmentWrapper(appService, s);
        }
        return shipment;
    }

    public void setShipment(DispatchShipmentWrapper s) {
        this.shipment = s;
        DispatchShipment oldShipment = wrappedObject.getShipment();
        DispatchShipment newShipment = s.getWrappedObject();
        wrappedObject.setShipment(newShipment);
        propertyChangeSupport.firePropertyChange("shipment", oldShipment,
            newShipment);
    }

    @Override
    public SiteWrapper getSite() {
        // FIXME need to check this !
        return null;
    }

    @Override
    protected AbstractPositionWrapper<DispatchPosition> getSpecificPositionWrapper(
        boolean initIfNoPosition) {
        // TODO Auto-generated method stub
        return null;
    }

}
