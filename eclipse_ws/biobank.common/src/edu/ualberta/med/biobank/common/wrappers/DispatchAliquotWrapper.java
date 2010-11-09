package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.DispatchAliquotState;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchAliquot;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchAliquotWrapper extends ModelWrapper<DispatchAliquot> {

    public DispatchAliquotWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchAliquotWrapper(WritableApplicationService appService,
        DispatchAliquot dsa) {
        super(appService, dsa);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "aliquot", "dispatch", "state", "comment" };
    }

    @Override
    public Class<DispatchAliquot> getWrappedClass() {
        return DispatchAliquot.class;
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

    public DispatchWrapper getDispatch() {
        DispatchWrapper ship = (DispatchWrapper) propertiesMap.get("dispatch");
        if (ship == null) {
            Dispatch s = wrappedObject.getDispatch();
            if (s != null) {
                ship = new DispatchWrapper(appService, s);
            }
        }
        return ship;
    }

    public void setDispatch(DispatchWrapper ship) {
        propertiesMap.put("dispatch", ship);
        Dispatch oldShip = wrappedObject.getDispatch();
        Dispatch newShip = null;
        if (ship != null) {
            newShip = ship.getWrappedObject();
        }
        wrappedObject.setDispatch(newShip);
        propertyChangeSupport.firePropertyChange("dispatch", oldShip, newShip);
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
    public int compareTo(ModelWrapper<DispatchAliquot> object) {
        if (object instanceof DispatchAliquotWrapper) {
            DispatchAliquotWrapper dsa = (DispatchAliquotWrapper) object;
            return getAliquot().compareTo(dsa.getAliquot());
        }
        return super.compareTo(object);
    }

    public String getStateDescription() {
        return DispatchAliquotState.getState(getState()).getLabel();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DispatchAliquotWrapper && object != null) {
            DispatchAliquotWrapper dsa = (DispatchAliquotWrapper) object;
            if (isNew() && dsa.isNew()) {
                return getAliquot() != null && dsa.getAliquot() != null
                    && getAliquot().equals(dsa.getAliquot())
                    && getDispatch() != null && dsa.getDispatch() != null
                    && getDispatch().equals(dsa.getDispatch());
            }
        }
        return super.equals(object);
    }
}
