package edu.ualberta.med.biobank.treeview.order;

import java.util.Date;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.OrderState;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.WrapperException;
import edu.ualberta.med.biobank.model.Order;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class OrderWrapper extends ModelWrapper<Order> {

    @SuppressWarnings("unused")
    private boolean stateModified = false;

    public OrderWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public OrderWrapper(WritableApplicationService appService, Order order) {
        super(appService, order);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Order> getWrappedClass() {
        return Order.class;
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

    public StudyWrapper getStudy() {
        StudyWrapper study = (StudyWrapper) propertiesMap.get("study");
        if (study == null) {
            Study s = wrappedObject.getStudy();
            if (s == null)
                return null;
            study = new StudyWrapper(appService, s);
            propertiesMap.put("study", study);
        }
        return study;
    }

    public Date getDateCreated() {
        return wrappedObject.getSubmitted();
    }

    public boolean isInCreationState() {
        return true;
    }

    public boolean isInNewState() {
        return true;
    }

    public boolean isInProcessingState() {
        return true;
    }

    public boolean isInFilledState() {
        return true;
    }

    public boolean isInShippedState() {
        return true;
    }

    public boolean isInClosedState() {
        return true;
    }

    public void setInCloseState() {
        setState(OrderState.CLOSED);
    }

    private void setState(OrderState state) {
        Integer oldState = wrappedObject.getState();
        wrappedObject.setState(state.getId());
        stateModified = oldState == null || state == null
            || !oldState.equals(state);
    }

    public void setInLostState() {
        setState(OrderState.LOST);
    }

    public void setInReceivedState() {
        setState(OrderState.RECEIVED);
    }

    public void setInCreationState() {
        setState(OrderState.CREATION);
    }

    public void setInProcessingState() {
        setState(OrderState.PROCESSING);
    }

}
