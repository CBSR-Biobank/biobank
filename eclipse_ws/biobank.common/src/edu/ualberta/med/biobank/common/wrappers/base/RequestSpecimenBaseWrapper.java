/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.RequestSpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.RequestBaseWrapper;
import java.util.Arrays;

public class RequestSpecimenBaseWrapper extends ModelWrapper<RequestSpecimen> {

    public RequestSpecimenBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public RequestSpecimenBaseWrapper(WritableApplicationService appService,
        RequestSpecimen wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<RequestSpecimen> getWrappedClass() {
        return RequestSpecimen.class;
    }

    @Override
    public Property<Integer, ? super RequestSpecimen> getIdProperty() {
        return RequestSpecimenPeer.ID;
    }

    @Override
    protected List<Property<?, ? super RequestSpecimen>> getProperties() {
        return RequestSpecimenPeer.PROPERTIES;
    }

    public RequestSpecimenState getState() {
        return getProperty(RequestSpecimenPeer.STATE);
    }

    public void setState(RequestSpecimenState state) {
        setProperty(RequestSpecimenPeer.STATE, state);
    }

    public String getClaimedBy() {
        return getProperty(RequestSpecimenPeer.CLAIMED_BY);
    }

    public void setClaimedBy(String claimedBy) {
        String trimmed = claimedBy == null ? null : claimedBy.trim();
        setProperty(RequestSpecimenPeer.CLAIMED_BY, trimmed);
    }

    public SpecimenWrapper getSpecimen() {
        boolean notCached = !isPropertyCached(RequestSpecimenPeer.SPECIMEN);
        SpecimenWrapper specimen = getWrappedProperty(RequestSpecimenPeer.SPECIMEN, SpecimenWrapper.class);
        if (specimen != null && notCached) ((SpecimenBaseWrapper) specimen).addToRequestSpecimenCollectionInternal(Arrays.asList(this));
        return specimen;
    }

    public void setSpecimen(SpecimenBaseWrapper specimen) {
        if (isInitialized(RequestSpecimenPeer.SPECIMEN)) {
            SpecimenBaseWrapper oldSpecimen = getSpecimen();
            if (oldSpecimen != null) oldSpecimen.removeFromRequestSpecimenCollectionInternal(Arrays.asList(this));
        }
        if (specimen != null) specimen.addToRequestSpecimenCollectionInternal(Arrays.asList(this));
        setWrappedProperty(RequestSpecimenPeer.SPECIMEN, specimen);
    }

    void setSpecimenInternal(SpecimenBaseWrapper specimen) {
        setWrappedProperty(RequestSpecimenPeer.SPECIMEN, specimen);
    }

    public RequestWrapper getRequest() {
        boolean notCached = !isPropertyCached(RequestSpecimenPeer.REQUEST);
        RequestWrapper request = getWrappedProperty(RequestSpecimenPeer.REQUEST, RequestWrapper.class);
        if (request != null && notCached) ((RequestBaseWrapper) request).addToRequestSpecimenCollectionInternal(Arrays.asList(this));
        return request;
    }

    public void setRequest(RequestBaseWrapper request) {
        if (isInitialized(RequestSpecimenPeer.REQUEST)) {
            RequestBaseWrapper oldRequest = getRequest();
            if (oldRequest != null) oldRequest.removeFromRequestSpecimenCollectionInternal(Arrays.asList(this));
        }
        if (request != null) request.addToRequestSpecimenCollectionInternal(Arrays.asList(this));
        setWrappedProperty(RequestSpecimenPeer.REQUEST, request);
    }

    void setRequestInternal(RequestBaseWrapper request) {
        setWrappedProperty(RequestSpecimenPeer.REQUEST, request);
    }

}
