package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.RequestAliquotPeer;
import edu.ualberta.med.biobank.common.peer.RequestPeer;
import edu.ualberta.med.biobank.common.util.RequestAliquotState;
import edu.ualberta.med.biobank.common.wrappers.base.RequestBaseWrapper;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestAliquot;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class RequestWrapper extends RequestBaseWrapper {

    private static final String NON_PROCESSED_ALIQUOTS_KEY = "nonProcessedRequestAliquotCollection";

    private static final String PROCESSED_ALIQUOTS_KEY = "processedRequestAliquots";

    private static final String UNAVAILABLE_ALIQUOTS_KEY = "unavailableRequestAliquots";

    public RequestWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public RequestWrapper(WritableApplicationService appService, Request request) {
        super(appService, request);
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

    private static final String REQUEST_BY_NUMBER_QRY = "from "
        + Request.class.getName() + " where " + RequestPeer.ID.getName() + "=?";

    public static List<RequestWrapper> getRequestByNumber(
        WritableApplicationService appService, String requestNumber)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(REQUEST_BY_NUMBER_QRY,
            Arrays.asList(new Object[] { Integer.parseInt(requestNumber) }));
        List<Request> shipments = appService.query(criteria);
        List<RequestWrapper> wrappers = new ArrayList<RequestWrapper>();
        for (Request s : shipments) {
            wrappers.add(new RequestWrapper(appService, s));
        }
        return wrappers;
    }

    private static final String IS_ALL_PROCESSED_QRY = "select count(*) from "
        + RequestAliquot.class.getName() + " as ra where ra."
        + RequestAliquotPeer.STATE.getName() + "=?" + " and ra."
        + Property.concatNames(RequestAliquotPeer.REQUEST, RequestPeer.ID)
        + "=?";

    public boolean isAllProcessed() {
        // using the collection was too slow
        List<Object> results = null;
        HQLCriteria c = new HQLCriteria(IS_ALL_PROCESSED_QRY,
            Arrays.asList(new Object[] {
                RequestAliquotState.NONPROCESSED_STATE.getId(), getId() }));
        try {
            results = appService.query(c);
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0 == (Long) results.get(0);
    }
}
