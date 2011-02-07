package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.peer.RequestPeer;
import edu.ualberta.med.biobank.common.util.RequestAliquotState;
import edu.ualberta.med.biobank.common.util.RequestState;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestAliquot;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class RequestWrapper extends ModelWrapper<Request> {

    private static final String NON_PROCESSED_ALIQUOTS_KEY = "nonProcessedRequestAliquotCollection";

    private static final String PROCESSED_ALIQUOTS_KEY = "processedRequestAliquots";

    private static final String UNAVAILABLE_ALIQUOTS_KEY = "unavailableRequestAliquots";

    private static final String ALL_ALIQUOTS_KEY = "requestAliquotCollection";

    public RequestWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public RequestWrapper(WritableApplicationService appService, Request request) {
        super(appService, request);
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return RequestPeer.PROP_NAMES;
    }

    @Override
    public Class<Request> getWrappedClass() {
        return Request.class;
    }

    public StudyWrapper getStudy() {
        return getWrappedProperty(RequestPeer.STUDY, StudyWrapper.class);
    }

    public void setStudy(StudyWrapper study) {
        setWrappedProperty(RequestPeer.STUDY, study);
    }

    public Date getDateCreated() {
        return getProperty(RequestPeer.CREATED);
    }

    public boolean isInLostState() {
        return RequestState.LOST.isEquals(getState());
    }

    public boolean isInApprovedState() {
        return RequestState.APPROVED.isEquals(getState());
    }

    public boolean isInShippedState() {
        return RequestState.SHIPPED.isEquals(getState());
    }

    public boolean isInClosedState() {
        return RequestState.CLOSED.isEquals(getState());
    }

    public void setInCloseState() {
        setState(RequestState.CLOSED);
    }

    private void setState(RequestState state) {
        setProperty(RequestPeer.STATE, state.getId());
    }

    public void setInLostState() {
        setState(RequestState.LOST);
    }

    public void setInApprovedState() {
        setState(RequestState.APPROVED);
    }

    public void setInShippedState() {
        setState(RequestState.SHIPPED);
    }

    public Date getSubmitted() {
        return getProperty(RequestPeer.SUBMITTED);
    }

    public void setSubmitted(Date submitted) {
        setProperty(RequestPeer.SUBMITTED, submitted);
    }

    public Date getAccepted() {
        return getProperty(RequestPeer.CREATED);
    }

    public Integer getState() {
        return getProperty(RequestPeer.STATE);
    }

    public void setState(Integer state) {
        setProperty(RequestPeer.STATE, state);
    }

    public Center getRequester() {
        return getProperty(RequestPeer.REQUESTER);
    }

    public void setRequester(CenterWrapper center) {
        setWrappedProperty(RequestPeer.REQUESTER, center);
    }

    public void receiveAliquots(List<AliquotWrapper> aliquots) throws Exception {
        List<RequestAliquotWrapper> flagged = new ArrayList<RequestAliquotWrapper>();
        List<RequestAliquotWrapper> ras = getNonProcessedRequestAliquotCollection();
        for (RequestAliquotWrapper r : ras)
            for (AliquotWrapper a : aliquots)
                if (r.getAliquot().getInventoryId().equals(a.getInventoryId())) {
                    flagged.add(r);
                }
        flagAliquots(flagged);
    }

    public void flagAliquots(List<RequestAliquotWrapper> scanned)
        throws Exception {
        for (RequestAliquotWrapper a : scanned) {
            a.setState(RequestAliquotState.PROCESSED_STATE.getId());
            a.persist();
        }
        propertiesMap.put(NON_PROCESSED_ALIQUOTS_KEY, null);
        propertiesMap.put(PROCESSED_ALIQUOTS_KEY, null);
    }

    public void receiveAliquot(String text) throws Exception {
        List<RequestAliquotWrapper> ras = getNonProcessedRequestAliquotCollection();
        for (RequestAliquotWrapper r : ras)
            if (r.getAliquot().getInventoryId().equals(text)) {
                flagAliquots(Arrays.asList(r));
                return;
            }
        throw new Exception("Aliquot " + text
            + " is not in the non-processed list.");

    }

    public List<RequestAliquotWrapper> getRequestAliquotCollection(boolean sort) {
        return getWrapperCollection(RequestPeer.REQUEST_ALIQUOT_COLLECTION,
            RequestAliquotWrapper.class, sort);
    }

    public void setRequestAliquotCollection(
        Collection<RequestAliquot> allAliquotObjects,
        List<RequestAliquotWrapper> allAliquotWrappers) {
        Collection<RequestAliquot> oldAliquots = wrappedObject
            .getRequestAliquotCollection();
        if (allAliquotObjects instanceof Set)
            wrappedObject.setRequestAliquotCollection(allAliquotObjects);
        else
            wrappedObject
                .setRequestAliquotCollection(new HashSet<RequestAliquot>(
                    allAliquotObjects));
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
                        if (state.getId().equals(dsa.getState())) {
                            hasState = true;
                            break;
                        }
                    }
                    if (hasState)
                        dsaCollection.add(dsa);
                }
                propertiesMap.put(mapKey, dsaCollection);
            }
            if ((dsaCollection != null) && sort)
                Collections.sort(dsaCollection);
        }
        return dsaCollection;
    }

    public List<RequestAliquotWrapper> getUnavailableRequestAliquotCollection() {
        return getRequestAliquotCollectionWithState(UNAVAILABLE_ALIQUOTS_KEY,
            true, RequestAliquotState.UNAVAILABLE_STATE);
    }

    public void resetStateLists() {
        propertiesMap.put(UNAVAILABLE_ALIQUOTS_KEY, null);
        propertiesMap.put(PROCESSED_ALIQUOTS_KEY, null);
        propertiesMap.put(NON_PROCESSED_ALIQUOTS_KEY, null);
    }

    public RequestAliquotWrapper getRequestAliquot(String inventoryId) {
        for (RequestAliquotWrapper dsa : getRequestAliquotCollection(false)) {
            if (dsa.getAliquot().getInventoryId().equals(inventoryId))
                return dsa;
        }
        return null;
    }

    public static List<RequestWrapper> getRequestByNumber(
        WritableApplicationService appService, String requestNumber)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Request.class.getName() + " where id = ?",
            Arrays.asList(new Object[] { Integer.parseInt(requestNumber) }));
        List<Request> shipments = appService.query(criteria);
        List<RequestWrapper> wrappers = new ArrayList<RequestWrapper>();
        for (Request s : shipments) {
            wrappers.add(new RequestWrapper(appService, s));
        }
        return wrappers;
    }

    public boolean isAllProcessed() {
        // using the collection was too slow
        List<Object> results = null;
        HQLCriteria c = new HQLCriteria("select count(*) from "
            + RequestAliquot.class.getName() + " ra where ra.state="
            + RequestAliquotState.NONPROCESSED_STATE.getId()
            + " and ra.request=" + getId());
        try {
            results = appService.query(c);
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0 == (Long) results.get(0);
    }
}
