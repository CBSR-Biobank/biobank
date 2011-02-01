package edu.ualberta.med.biobank.common.wrappers;

import java.util.List;

import edu.ualberta.med.biobank.common.peer.RequestAliquotPeer;
import edu.ualberta.med.biobank.common.util.RequestAliquotState;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestAliquot;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class RequestAliquotWrapper extends ModelWrapper<RequestAliquot> {

    public RequestAliquotWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public RequestAliquotWrapper(WritableApplicationService appService,
        RequestAliquot dsa) {
        super(appService, dsa);
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return RequestAliquotPeer.PROP_NAMES;
    }

    @Override
    public Class<RequestAliquot> getWrappedClass() {
        return RequestAliquot.class;
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

    public RequestWrapper getRequest() {
        RequestWrapper ship = (RequestWrapper) propertiesMap.get("request");
        if (ship == null) {
            Request o = wrappedObject.getRequest();
            if (o != null) {
                ship = new RequestWrapper(appService, o);
            }
        }
        return ship;
    }

    public void setRequest(RequestWrapper ship) {
        propertiesMap.put("request", ship);
        Request oldShip = wrappedObject.getRequest();
        Request newShip = null;
        if (ship != null) {
            newShip = ship.getWrappedObject();
        }
        wrappedObject.setRequest(newShip);
        propertyChangeSupport.firePropertyChange("request", oldShip, newShip);
    }

    public Integer getState() {
        return wrappedObject.getState();
    }

    public void setState(Integer newState) {
        Integer oldState = wrappedObject.getState();
        wrappedObject.setState(newState);
        propertyChangeSupport.firePropertyChange("state", oldState, newState);
    }

    public String getClaimedBy() {
        return wrappedObject.getClaimedBy();
    }

    public void setClaimedBy(String newClaimedBy) {
        String oldClaimedBy = wrappedObject.getClaimedBy();
        wrappedObject.setClaimedBy(newClaimedBy);
        propertyChangeSupport.firePropertyChange("claimedBy", oldClaimedBy,
            newClaimedBy);
    }

    @Override
    public int compareTo(ModelWrapper<RequestAliquot> object) {
        if (object instanceof RequestAliquotWrapper) {
            RequestAliquotWrapper dsa = (RequestAliquotWrapper) object;
            return getAliquot().compareTo(dsa.getAliquot());
        }
        return super.compareTo(object);
    }

    public String getStateDescription() {
        return RequestAliquotState.getState(getState()).getLabel();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof RequestAliquotWrapper && object != null) {
            RequestAliquotWrapper dsa = (RequestAliquotWrapper) object;
            if (isNew() && dsa.isNew()) {
                return getAliquot() != null && dsa.getAliquot() != null
                    && getAliquot().equals(dsa.getAliquot())
                    && getAliquot() != null && dsa.getAliquot() != null
                    && getAliquot().equals(dsa.getAliquot());
            }
        }
        return super.equals(object);
    }
}
