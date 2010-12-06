package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.OrderState;
import edu.ualberta.med.biobank.common.wrappers.internal.AddressWrapper;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.Order;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class OrderWrapper extends ModelWrapper<Order> {

    private boolean stateModified = false;
    private AddressWrapper address;
    private SiteWrapper site;

    public OrderWrapper(WritableApplicationService appService) {
        super(appService);
        this.address = new AddressWrapper(appService,
            wrappedObject.getAddress());
        this.site = new SiteWrapper(appService, wrappedObject.getSite());
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
    public List<AliquotWrapper> getAliquotCollection(boolean sort) {
        List<AliquotWrapper> aliquotCollection = (List<AliquotWrapper>) propertiesMap
            .get("aliquotCollection");
        if (aliquotCollection == null) {
            Collection<Aliquot> children = wrappedObject.getAliquotCollection();
            if (children != null) {
                aliquotCollection = new ArrayList<AliquotWrapper>();
                for (Aliquot aliquot : children) {
                    aliquotCollection.add(new AliquotWrapper(appService,
                        aliquot));
                }
                propertiesMap.put("aliquotCollection", aliquotCollection);
            }
        }
        if ((aliquotCollection != null) && sort)
            Collections.sort(aliquotCollection);
        return aliquotCollection;
    }

    private void setAliquotCollection(Collection<Aliquot> allAliquotObjects,
        List<AliquotWrapper> allAliquotWrappers) {
        Collection<Aliquot> oldAliquots = wrappedObject.getAliquotCollection();
        wrappedObject.setAliquotCollection(allAliquotObjects);
        propertyChangeSupport.firePropertyChange("alliquotCollection",
            oldAliquots, allAliquotObjects);
        propertiesMap.put("aliquotCollection", allAliquotWrappers);
    }

}
