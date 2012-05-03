package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.RequestPeer;
import edu.ualberta.med.biobank.common.wrappers.base.RequestBaseWrapper;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class RequestWrapper extends RequestBaseWrapper {

    private static final String NON_PROCESSED_SPECIMENS_CACHE_KEY =
        "nonProcessedRequestSpecimenCollection"; //$NON-NLS-1$

    private static final String PROCESSED_SPECIMENS_CACHE_KEY =
        "processedRequestSpecimens"; //$NON-NLS-1$

    private static final String UNAVAILABLE_SPECIMENS_KEY =
        "unavailableRequestSpecimens"; //$NON-NLS-1$

    public RequestWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public RequestWrapper(WritableApplicationService appService, Request request) {
        super(appService, request);
    }

    public void receiveSpecimens(List<SpecimenWrapper> specimens)
        throws Exception {
        List<RequestSpecimenWrapper> flagged =
            new ArrayList<RequestSpecimenWrapper>();
        List<RequestSpecimenWrapper> ras =
            getNonProcessedRequestSpecimenCollection();
        for (RequestSpecimenWrapper r : ras)
            for (SpecimenWrapper a : specimens)
                if (r.getSpecimen().getInventoryId().equals(a.getInventoryId())) {
                    flagged.add(r);
                }
        flagSpecimens(flagged);
    }

    @Deprecated
    public void flagSpecimens(List<RequestSpecimenWrapper> scanned)
        throws Exception {
        for (RequestSpecimenWrapper a : scanned) {
            a.setState(RequestSpecimenState.PULLED_STATE);
            a.persist();
        }
        cache.put(NON_PROCESSED_SPECIMENS_CACHE_KEY, null);
        cache.put(PROCESSED_SPECIMENS_CACHE_KEY, null);
    }

    public List<RequestSpecimenWrapper> getNonProcessedRequestSpecimenCollection() {
        return getRequestSpecimenCollectionWithState(
            NON_PROCESSED_SPECIMENS_CACHE_KEY, true,
            RequestSpecimenState.AVAILABLE_STATE);
    }

    public List<RequestSpecimenWrapper> getProcessedRequestSpecimenCollection() {
        return getRequestSpecimenCollectionWithState(
            PROCESSED_SPECIMENS_CACHE_KEY, true,
            RequestSpecimenState.PULLED_STATE);
    }

    @SuppressWarnings("unchecked")
    private List<RequestSpecimenWrapper> getRequestSpecimenCollectionWithState(
        String mapKey, boolean sort, RequestSpecimenState... states) {
        List<RequestSpecimenWrapper> dsaCollection =
            (List<RequestSpecimenWrapper>) cache
                .get(mapKey);
        if (dsaCollection == null) {
            Collection<RequestSpecimenWrapper> children =
                getRequestSpecimenCollection(sort);
            if (children != null) {
                dsaCollection = new ArrayList<RequestSpecimenWrapper>();
                for (RequestSpecimenWrapper dsa : children) {
                    boolean hasState = false;
                    for (RequestSpecimenState state : states) {
                        if (state.getId().equals(dsa.getState())) {
                            hasState = true;
                            break;
                        }
                    }
                    if (hasState)
                        dsaCollection.add(dsa);
                }
                cache.put(mapKey, dsaCollection);
            }
            if ((dsaCollection != null) && sort)
                Collections.sort(dsaCollection);
        }
        return dsaCollection;
    }

    public List<RequestSpecimenWrapper> getUnavailableRequestSpecimenCollection() {
        return getRequestSpecimenCollectionWithState(UNAVAILABLE_SPECIMENS_KEY,
            true, RequestSpecimenState.UNAVAILABLE_STATE);
    }

    public void resetStateLists() {
        cache.put(UNAVAILABLE_SPECIMENS_KEY, null);
        cache.put(PROCESSED_SPECIMENS_CACHE_KEY, null);
        cache.put(NON_PROCESSED_SPECIMENS_CACHE_KEY, null);
    }

    public RequestSpecimenWrapper getRequestSpecimen(String inventoryId) {
        for (RequestSpecimenWrapper dsa : getRequestSpecimenCollection(false)) {
            if (dsa.getSpecimen().getInventoryId().equals(inventoryId))
                return dsa;
        }
        return null;
    }

    private static final String REQUEST_BY_NUMBER_QRY = "from " //$NON-NLS-1$
        + Request.class.getName() + " where " + RequestPeer.ID.getName() + "=?"; //$NON-NLS-1$ //$NON-NLS-2$

    public static List<RequestWrapper> getRequestByNumber(
        WritableApplicationService appService, Integer requestNumber)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(REQUEST_BY_NUMBER_QRY,
            Arrays.asList(new Object[] { requestNumber }));
        List<Request> shipments = appService.query(criteria);
        List<RequestWrapper> wrappers = new ArrayList<RequestWrapper>();
        for (Request s : shipments) {
            wrappers.add(new RequestWrapper(appService, s));
        }
        return wrappers;
    }
}
