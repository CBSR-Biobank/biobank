package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.RequestAliquotState;
import edu.ualberta.med.biobank.common.util.RequestState;
import edu.ualberta.med.biobank.common.wrappers.internal.AddressWrapper;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestAliquot;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class RequestWrapper extends ModelWrapper<Request> {

    private static final String NON_PROCESSED_ALIQUOTS_KEY = "nonProcessedDispatchAliquotCollection";

    private static final String PROCESSED_ALIQUOTS_KEY = "receivedDispatchAliquots";

    private static final String MISSING_ALIQUOTS_KEY = "missingDispatchAliquots";

    private static final String ALL_ALIQUOTS_KEY = "aliquotCollection";

    private boolean stateModified = false;
    private AddressWrapper address;

    public RequestWrapper(WritableApplicationService appService) {
        super(appService);
        this.address = new AddressWrapper(appService,
            wrappedObject.getAddress());
    }

    public RequestWrapper(WritableApplicationService appService, Request request) {
        super(appService, request);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "id", "submitted", "accepted", "shipped",
            "waybill", "state" };
    }

    @Override
    public Class<Request> getWrappedClass() {
        return Request.class;
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
        setState(RequestState.CLOSED);
    }

    private void setState(RequestState state) {
        Integer oldState = wrappedObject.getState();
        wrappedObject.setState(state.getId());
        stateModified = oldState == null || state == null
            || !oldState.equals(state);
    }

    public void setInLostState() {
        setState(RequestState.LOST);
    }

    public void setInApprovedState() {
        setState(RequestState.APPROVED);
    }

    public void setInNewState() {
        setState(RequestState.NEW);
    }

    public void setInAcceptedState() {
        setState(RequestState.ACCEPTED);
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

    public AddressWrapper getAddress() {
        if (address == null) {
            Address a = wrappedObject.getAddress();
            if (a == null)
                return null;
            address = new AddressWrapper(appService, a);
        }
        return address;
    }

    public void setAddress(Address address) {
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
    public List<RequestAliquotWrapper> getRequestAliquotCollection(boolean sort) {
        List<RequestAliquotWrapper> requestAliquotCollection = (List<RequestAliquotWrapper>) propertiesMap
            .get("requestAliquotCollection");
        if (requestAliquotCollection == null) {
            Collection<RequestAliquot> children = wrappedObject
                .getRequestAliquotCollection();
            if (children != null) {
                requestAliquotCollection = new ArrayList<RequestAliquotWrapper>();
                for (RequestAliquot aliquot : children) {
                    requestAliquotCollection.add(new RequestAliquotWrapper(
                        appService, aliquot));
                }
                propertiesMap.put("requestAliquotCollection",
                    requestAliquotCollection);
            }
            if ((requestAliquotCollection != null) && sort)
                Collections.sort(requestAliquotCollection);
        }
        return requestAliquotCollection;
    }

    public void setRequestAliquotCollection(
        Collection<RequestAliquot> allAliquotObjects,
        List<RequestAliquotWrapper> allAliquotWrappers) {
        Collection<RequestAliquot> oldAliquots = wrappedObject
            .getRequestAliquotCollection();
        wrappedObject.setRequestAliquotCollection(allAliquotObjects);
        propertyChangeSupport.firePropertyChange("requestAliquotCollection",
            oldAliquots, allAliquotObjects);
        propertiesMap.put("requestAliquotCollection", allAliquotWrappers);
    }

    public List<RequestAliquotWrapper> getNonProcessedRequestAliquotCollection() {
        return getRequestAliquotCollectionWithState(NON_PROCESSED_ALIQUOTS_KEY,
            true, RequestAliquotState.NONPROCESSED_STATE);
    }

    public List<RequestAliquotWrapper> getProcessedRequestAliquotCollection() {
        return getRequestAliquotCollectionWithState(PROCESSED_ALIQUOTS_KEY,
            true, RequestAliquotState.PROCESSED_STATE);
    }

    @SuppressWarnings("unchecked")
    private List<RequestAliquotWrapper> getRequestAliquotCollectionWithState(
        String mapKey, boolean sort, RequestAliquotState... states) {
        List<RequestAliquotWrapper> dsaCollection = (List<RequestAliquotWrapper>) propertiesMap
            .get(mapKey);
        if (dsaCollection == null) {
            Collection<RequestAliquotWrapper> children = getRequestAliquotCollection(sort);
            if (children != null) {
                dsaCollection = new ArrayList<RequestAliquotWrapper>();
                for (RequestAliquotWrapper dsa : children) {
                    boolean hasState = false;
                    for (RequestAliquotState state : states) {
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
