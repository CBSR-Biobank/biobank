/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.RequestPeer;
import java.util.Date;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ResearchGroupBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.AddressWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.AddressBaseWrapper;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.DispatchBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.RequestSpecimenBaseWrapper;
import java.util.Arrays;

public class RequestBaseWrapper extends ModelWrapper<Request> {

    public RequestBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public RequestBaseWrapper(WritableApplicationService appService,
        Request wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Request> getWrappedClass() {
        return Request.class;
    }

    @Override
    public Property<Integer, ? super Request> getIdProperty() {
        return RequestPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Request>> getProperties() {
        return RequestPeer.PROPERTIES;
    }

    public Date getCreated() {
        return getProperty(RequestPeer.CREATED);
    }

    public void setCreated(Date created) {
        setProperty(RequestPeer.CREATED, created);
    }

    public Date getSubmitted() {
        return getProperty(RequestPeer.SUBMITTED);
    }

    public void setSubmitted(Date submitted) {
        setProperty(RequestPeer.SUBMITTED, submitted);
    }

    public ResearchGroupWrapper getResearchGroup() {
        boolean notCached = !isPropertyCached(RequestPeer.RESEARCH_GROUP);
        ResearchGroupWrapper researchGroup = getWrappedProperty(RequestPeer.RESEARCH_GROUP, ResearchGroupWrapper.class);
        if (researchGroup != null && notCached) ((ResearchGroupBaseWrapper) researchGroup).addToRequestCollectionInternal(Arrays.asList(this));
        return researchGroup;
    }

    public void setResearchGroup(ResearchGroupBaseWrapper researchGroup) {
        if (isInitialized(RequestPeer.RESEARCH_GROUP)) {
            ResearchGroupBaseWrapper oldResearchGroup = getResearchGroup();
            if (oldResearchGroup != null) oldResearchGroup.removeFromRequestCollectionInternal(Arrays.asList(this));
        }
        if (researchGroup != null) researchGroup.addToRequestCollectionInternal(Arrays.asList(this));
        setWrappedProperty(RequestPeer.RESEARCH_GROUP, researchGroup);
    }

    void setResearchGroupInternal(ResearchGroupBaseWrapper researchGroup) {
        setWrappedProperty(RequestPeer.RESEARCH_GROUP, researchGroup);
    }

    public AddressWrapper getAddress() {
        AddressWrapper address = getWrappedProperty(RequestPeer.ADDRESS, AddressWrapper.class);
        return address;
    }

    public void setAddress(AddressBaseWrapper address) {
        setWrappedProperty(RequestPeer.ADDRESS, address);
    }

    void setAddressInternal(AddressBaseWrapper address) {
        setWrappedProperty(RequestPeer.ADDRESS, address);
    }

    public List<DispatchWrapper> getDispatchCollection(boolean sort) {
        List<DispatchWrapper> dispatchCollection = getWrapperCollection(RequestPeer.DISPATCHES, DispatchWrapper.class, sort);
        return dispatchCollection;
    }

    public void addToDispatchCollection(List<? extends DispatchBaseWrapper> dispatchCollection) {
        addToWrapperCollection(RequestPeer.DISPATCHES, dispatchCollection);
    }

    void addToDispatchCollectionInternal(List<? extends DispatchBaseWrapper> dispatchCollection) {
        if (isInitialized(RequestPeer.DISPATCHES)) {
            addToWrapperCollection(RequestPeer.DISPATCHES, dispatchCollection);
        } else {
            getElementQueue().add(RequestPeer.DISPATCHES, dispatchCollection);
        }
    }

    public void removeFromDispatchCollection(List<? extends DispatchBaseWrapper> dispatchCollection) {
        removeFromWrapperCollection(RequestPeer.DISPATCHES, dispatchCollection);
    }

    void removeFromDispatchCollectionInternal(List<? extends DispatchBaseWrapper> dispatchCollection) {
        if (isPropertyCached(RequestPeer.DISPATCHES)) {
            removeFromWrapperCollection(RequestPeer.DISPATCHES, dispatchCollection);
        } else {
            getElementQueue().remove(RequestPeer.DISPATCHES, dispatchCollection);
        }
    }

    public void removeFromDispatchCollectionWithCheck(List<? extends DispatchBaseWrapper> dispatchCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(RequestPeer.DISPATCHES, dispatchCollection);
    }

    void removeFromDispatchCollectionWithCheckInternal(List<? extends DispatchBaseWrapper> dispatchCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(RequestPeer.DISPATCHES, dispatchCollection);
    }

    public List<RequestSpecimenWrapper> getRequestSpecimenCollection(boolean sort) {
        boolean notCached = !isPropertyCached(RequestPeer.REQUEST_SPECIMENS);
        List<RequestSpecimenWrapper> requestSpecimenCollection = getWrapperCollection(RequestPeer.REQUEST_SPECIMENS, RequestSpecimenWrapper.class, sort);
        if (notCached) {
            for (RequestSpecimenBaseWrapper e : requestSpecimenCollection) {
                e.setRequestInternal(this);
            }
        }
        return requestSpecimenCollection;
    }

    public void addToRequestSpecimenCollection(List<? extends RequestSpecimenBaseWrapper> requestSpecimenCollection) {
        addToWrapperCollection(RequestPeer.REQUEST_SPECIMENS, requestSpecimenCollection);
        for (RequestSpecimenBaseWrapper e : requestSpecimenCollection) {
            e.setRequestInternal(this);
        }
    }

    void addToRequestSpecimenCollectionInternal(List<? extends RequestSpecimenBaseWrapper> requestSpecimenCollection) {
        if (isInitialized(RequestPeer.REQUEST_SPECIMENS)) {
            addToWrapperCollection(RequestPeer.REQUEST_SPECIMENS, requestSpecimenCollection);
        } else {
            getElementQueue().add(RequestPeer.REQUEST_SPECIMENS, requestSpecimenCollection);
        }
    }

    public void removeFromRequestSpecimenCollection(List<? extends RequestSpecimenBaseWrapper> requestSpecimenCollection) {
        removeFromWrapperCollection(RequestPeer.REQUEST_SPECIMENS, requestSpecimenCollection);
        for (RequestSpecimenBaseWrapper e : requestSpecimenCollection) {
            e.setRequestInternal(null);
        }
    }

    void removeFromRequestSpecimenCollectionInternal(List<? extends RequestSpecimenBaseWrapper> requestSpecimenCollection) {
        if (isPropertyCached(RequestPeer.REQUEST_SPECIMENS)) {
            removeFromWrapperCollection(RequestPeer.REQUEST_SPECIMENS, requestSpecimenCollection);
        } else {
            getElementQueue().remove(RequestPeer.REQUEST_SPECIMENS, requestSpecimenCollection);
        }
    }

    public void removeFromRequestSpecimenCollectionWithCheck(List<? extends RequestSpecimenBaseWrapper> requestSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(RequestPeer.REQUEST_SPECIMENS, requestSpecimenCollection);
        for (RequestSpecimenBaseWrapper e : requestSpecimenCollection) {
            e.setRequestInternal(null);
        }
    }

    void removeFromRequestSpecimenCollectionWithCheckInternal(List<? extends RequestSpecimenBaseWrapper> requestSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(RequestPeer.REQUEST_SPECIMENS, requestSpecimenCollection);
    }

}
