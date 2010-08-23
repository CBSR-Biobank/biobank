package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.model.DispatchContainer;
import edu.ualberta.med.biobank.model.DispatchShipment;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchContainerWrapper extends
    AbstractContainerWrapper<DispatchContainer> {

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
        if (getShipment() == null) {
            throw new BiobankCheckException("Shipment cannot be null");
        }
    }

    @Override
    protected void deleteChecks() throws Exception {
    }

    @Override
    protected String[] getPropertyChangeNames() {
        String[] names = super.getPropertyChangeNames();
        List<String> namesList = new ArrayList<String>(Arrays.asList(names));
        namesList.addAll(Arrays.asList("shipment"));
        return namesList.toArray(new String[namesList.size()]);
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
        if (getShipment() != null) {
            getShipment().getSender();
        }
        return null;
    }

}
