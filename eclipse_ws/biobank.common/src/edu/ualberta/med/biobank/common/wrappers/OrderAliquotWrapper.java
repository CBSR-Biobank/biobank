package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.OrderAliquotState;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.Order;
import edu.ualberta.med.biobank.model.OrderAliquot;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class OrderAliquotWrapper extends ModelWrapper<OrderAliquot> {

    public OrderAliquotWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public OrderAliquotWrapper(WritableApplicationService appService,
        OrderAliquot dsa) {
        super(appService, dsa);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "order", "aliquot", "state", "claimedBy" };
    }

    @Override
    public Class<OrderAliquot> getWrappedClass() {
        return OrderAliquot.class;
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

    public OrderWrapper getOrder() {
        OrderWrapper ship = (OrderWrapper) propertiesMap.get("order");
        if (ship == null) {
            Order o = wrappedObject.getOrder();
            if (o != null) {
                ship = new OrderWrapper(appService, o);
            }
        }
        return ship;
    }

    public void setOrder(OrderWrapper ship) {
        propertiesMap.put("order", ship);
        Order oldShip = wrappedObject.getOrder();
        Order newShip = null;
        if (ship != null) {
            newShip = ship.getWrappedObject();
        }
        wrappedObject.setOrder(newShip);
        propertyChangeSupport.firePropertyChange("order", oldShip, newShip);
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
    public int compareTo(ModelWrapper<OrderAliquot> object) {
        if (object instanceof OrderAliquotWrapper) {
            OrderAliquotWrapper dsa = (OrderAliquotWrapper) object;
            return getAliquot().compareTo(dsa.getAliquot());
        }
        return super.compareTo(object);
    }

    public String getStateDescription() {
        return OrderAliquotState.getState(getState()).getLabel();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof OrderAliquotWrapper && object != null) {
            OrderAliquotWrapper dsa = (OrderAliquotWrapper) object;
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
