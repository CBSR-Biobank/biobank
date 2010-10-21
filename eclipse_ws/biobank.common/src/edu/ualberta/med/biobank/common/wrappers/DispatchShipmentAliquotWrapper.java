package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.DispatchAliquotState;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.DispatchShipment;
import edu.ualberta.med.biobank.model.DispatchShipmentAliquot;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchShipmentAliquotWrapper extends
    ModelWrapper<DispatchShipmentAliquot> {

    public DispatchShipmentAliquotWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchShipmentAliquotWrapper(
        WritableApplicationService appService, DispatchShipmentAliquot dsa) {
        super(appService, dsa);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "aliquot", "shipment", "state", "comment" };
    }

    @Override
    public Class<DispatchShipmentAliquot> getWrappedClass() {
        return DispatchShipmentAliquot.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {

    }

    @Override
    protected void deleteChecks() throws Exception {

    }

    public AliquotWrapper getAliquot() {
        AliquotWrapper aliquot = (AliquotWrapper) propertiesMap.get("aliquot");
        if (aliquot == null) {
            Aliquot a = wrappedObject.getAliquot();
            if (a != null) {
                aliquot = new AliquotWrapper(appService, a);
            }
        }
        return aliquot;
    }

    public void setAliquot(AliquotWrapper aliquot) {
        propertiesMap.put("aliquot", aliquot);
        Aliquot oldAliquot = wrappedObject.getAliquot();
        Aliquot newAliquot = null;
        if (aliquot != null) {
            newAliquot = aliquot.getWrappedObject();
        }
        wrappedObject.setAliquot(newAliquot);
        propertyChangeSupport.firePropertyChange("aliquot", oldAliquot,
            newAliquot);
    }

    public DispatchShipmentWrapper getShipment() {
        DispatchShipmentWrapper ship = (DispatchShipmentWrapper) propertiesMap
            .get("shipment");
        if (ship == null) {
            DispatchShipment s = wrappedObject.getDispatchShipment();
            if (s != null) {
                ship = new DispatchShipmentWrapper(appService, s);
            }
        }
        return ship;
    }

    public void setShipment(DispatchShipmentWrapper ship) {
        propertiesMap.put("shipment", ship);
        DispatchShipment oldShip = wrappedObject.getDispatchShipment();
        DispatchShipment newShip = null;
        if (ship != null) {
            newShip = ship.getWrappedObject();
        }
        wrappedObject.setDispatchShipment(newShip);
        propertyChangeSupport.firePropertyChange("shipment", oldShip, newShip);
    }

    public Integer getState() {
        return wrappedObject.getState();
    }

    public void setState(Integer newState) {
        Integer oldState = wrappedObject.getState();
        wrappedObject.setState(newState);
        propertyChangeSupport.firePropertyChange("state", oldState, newState);
    }

    public String getComment() {
        return wrappedObject.getComment();
    }

    public void setComment(String newComment) {
        String oldComment = wrappedObject.getComment();
        wrappedObject.setComment(newComment);
        propertyChangeSupport.firePropertyChange("comment", oldComment,
            newComment);
    }

    @Override
    public int compareTo(ModelWrapper<DispatchShipmentAliquot> object) {
        if (object instanceof DispatchShipmentAliquotWrapper) {
            DispatchShipmentAliquotWrapper dsa = (DispatchShipmentAliquotWrapper) object;
            return getAliquot().compareTo(dsa.getAliquot());
        }
        return super.compareTo(object);
    }

    public String getStateDescription() {
        return DispatchAliquotState.getState(getState()).getLabel();
    }
}
