package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.OrderAliquotState;
import edu.ualberta.med.biobank.common.util.OrderState;
import edu.ualberta.med.biobank.common.wrappers.internal.AddressWrapper;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Order;
import edu.ualberta.med.biobank.model.OrderAliquot;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class OrderWrapper extends ModelWrapper<Order> {

    private static final String NON_PROCESSED_ALIQUOTS_KEY = "nonProcessedDispatchAliquotCollection";

    private static final String PROCESSED_ALIQUOTS_KEY = "receivedDispatchAliquots";

    private static final String MISSING_ALIQUOTS_KEY = "missingDispatchAliquots";

    private static final String ALL_ALIQUOTS_KEY = "aliquotCollection";

    private boolean stateModified = false;
    private AddressWrapper address;

    public OrderWrapper(WritableApplicationService appService) {
        super(appService);
        this.address = new AddressWrapper(appService,
            wrappedObject.getAddress());
    }

    public OrderWrapper(WritableApplicationService appService, Order order) {
        super(appService, order);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "id", "submitted", "accepted", "shipped",
            "waybill", "state" };
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

    public void setStudy(StudyWrapper study) {
        propertiesMap.put("study", study);
        Study oldStudyRaw = wrappedObject.getStudy();
        Study newStudyRaw = null;
        if (study != null) {
            newStudyRaw = study.wrappedObject;
        }
        wrappedObject.setStudy(newStudyRaw);
        propertyChangeSupport.firePropertyChange("study", oldStudyRaw,
            newStudyRaw);
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

    public void setInApprovedState() {
        setState(OrderState.APPROVED);
    }

    public void setInNewState() {
        setState(OrderState.NEW);
    }

    public void setInAcceptedState() {
        setState(OrderState.ACCEPTED);
    }

    public Date getSubmitted() {
        return wrappedObject.getSubmitted();
    }

    public void setSubmitted(Date submitted) {
        wrappedObject.setSubmitted(submitted);
    }

    public Date getAccepted() {
        return wrappedObject.getAccepted();
    }

    public void setAccepted(Date accepted) {
        wrappedObject.setAccepted(accepted);
    }

    public Date getShipped() {
        return wrappedObject.getShipped();
    }

    public void setShipped(Date shipped) {
        wrappedObject.setShipped(shipped);
    }

    public String getWaybill() {
        return wrappedObject.getWaybill();
    }

    public void setWaybill(String waybill) {
        wrappedObject.setWaybill(waybill);
    }

    public Integer getState() {
        return wrappedObject.getState();
    }

    public void setState(Integer state) {
        wrappedObject.setState(state);
    }

    private AddressWrapper getAddress() {
        if (address == null) {
            Address a = wrappedObject.getAddress();
            if (a == null)
                return null;
            address = new AddressWrapper(appService, a);
        }
        return address;
    }

    private void setAddress(Address address) {
        if (address == null)
            this.address = null;
        else
            this.address = new AddressWrapper(appService, address);
        Address oldAddress = wrappedObject.getAddress();
        wrappedObject.setAddress(address);
        propertyChangeSupport
            .firePropertyChange("address", oldAddress, address);
    }

    public SiteWrapper getSite() {
        SiteWrapper site = (SiteWrapper) propertiesMap.get("site");
        if (site == null) {
            Site s = wrappedObject.getSite();
            if (s == null)
                return null;
            site = new SiteWrapper(appService, s);
            propertiesMap.put("site", site);
        }
        return site;
    }

    public void setSite(SiteWrapper site) {
        propertiesMap.put("site", site);
        Site oldSite = wrappedObject.getSite();
        Site newSite = site.getWrappedObject();
        wrappedObject.setSite(newSite);
        propertyChangeSupport.firePropertyChange("site", oldSite, newSite);
    }

    @SuppressWarnings("unchecked")
    public List<OrderAliquotWrapper> getOrderAliquotCollection(boolean sort) {
        List<OrderAliquotWrapper> orderAliquotCollection = (List<OrderAliquotWrapper>) propertiesMap
            .get("orderAliquotCollection");
        if (orderAliquotCollection == null) {
            Collection<OrderAliquot> children = wrappedObject
                .getOrderAliquotCollection();
            if (children != null) {
                orderAliquotCollection = new ArrayList<OrderAliquotWrapper>();
                for (OrderAliquot aliquot : children) {
                    orderAliquotCollection.add(new OrderAliquotWrapper(
                        appService, aliquot));
                }
                propertiesMap.put("orderAliquotCollection",
                    orderAliquotCollection);
            }
            if ((orderAliquotCollection != null) && sort)
                Collections.sort(orderAliquotCollection);
        }
        return orderAliquotCollection;
    }

    private void setOrderAliquotCollection(
        Collection<OrderAliquot> allAliquotObjects,
        List<OrderAliquotWrapper> allAliquotWrappers) {
        Collection<OrderAliquot> oldAliquots = wrappedObject
            .getOrderAliquotCollection();
        wrappedObject.setOrderAliquotCollection(allAliquotObjects);
        propertyChangeSupport.firePropertyChange("orderAliquotCollection",
            oldAliquots, allAliquotObjects);
        propertiesMap.put("orderAliquotCollection", allAliquotWrappers);
    }

    public List<OrderAliquotWrapper> getNonProcessedOrderAliquotCollection() {
        return getOrderAliquotCollectionWithState(NON_PROCESSED_ALIQUOTS_KEY,
            true, OrderAliquotState.NONPROCESSED_STATE);
    }

    public List<OrderAliquotWrapper> getProcessedOrderAliquotCollection() {
        return getOrderAliquotCollectionWithState(PROCESSED_ALIQUOTS_KEY, true,
            OrderAliquotState.PROCESSED_STATE);
    }

    @SuppressWarnings("unchecked")
    private List<OrderAliquotWrapper> getOrderAliquotCollectionWithState(
        String mapKey, boolean sort, OrderAliquotState... states) {
        List<OrderAliquotWrapper> dsaCollection = (List<OrderAliquotWrapper>) propertiesMap
            .get(mapKey);
        if (dsaCollection == null) {
            Collection<OrderAliquotWrapper> children = getOrderAliquotCollection(sort);
            if (children != null) {
                dsaCollection = new ArrayList<OrderAliquotWrapper>();
                for (OrderAliquotWrapper dsa : children) {
                    boolean hasState = false;
                    for (OrderAliquotState state : states) {
                        if (state.isEquals(dsa.getState())) {
                            hasState = true;
                            break;
                        }
                    }
                    if (hasState)
                        dsaCollection.add(dsa);
                }
                propertiesMap.put(mapKey, dsaCollection);
            }
        }
        if ((dsaCollection != null) && sort)
            Collections.sort(dsaCollection);
        return dsaCollection;
    }

}
