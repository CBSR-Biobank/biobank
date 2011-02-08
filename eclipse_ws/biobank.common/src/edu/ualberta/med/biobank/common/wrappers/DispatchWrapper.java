package edu.ualberta.med.biobank.common.wrappers;

import java.util.List;

import edu.ualberta.med.biobank.common.peer.DispatchPeer;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.model.Dispatch;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * @see DispatchState
 */
public class DispatchWrapper extends AbstractShipmentWrapper<Dispatch> {
    public DispatchWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchWrapper(WritableApplicationService appService,
        Dispatch dispatch) {
        super(appService, dispatch);
    }

    @Override
    public Class<Dispatch> getWrappedClass() {
        return Dispatch.class;
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return DispatchPeer.PROP_NAMES;
    }

    public CenterWrapper<?> getSender() {
        return getWrappedProperty(DispatchPeer.SENDER, null);
    }

    public void setSender(CenterWrapper<?> sender) {
        setWrappedProperty(DispatchPeer.SENDER, sender);
    }

    public CenterWrapper<?> getReceiver() {
        return getWrappedProperty(DispatchPeer.RECEIVER, null);
    }

    public void setReceiver(CenterWrapper<?> center) {
        setWrappedProperty(DispatchPeer.RECEIVER, center);
    }

    public DispatchState getState() {
        return DispatchState.getState(getProperty(DispatchPeer.STATE));
    }

    public void setState(DispatchState state) {
        setProperty(DispatchPeer.STATE, state.getId());
    }

    public String getStateDescription() {
        DispatchState state = DispatchState
            .getState(getProperty(DispatchPeer.STATE));
        if (state == null)
            return "";
        return state.getLabel();
    }

    public List<DispatchSourceVesselWrapper> getDispatchSourceVesselCollection(
        boolean sort) {
        return getWrapperCollection(
            DispatchPeer.DISPATCH_SOURCE_VESSEL_COLLECTION,
            DispatchSourceVesselWrapper.class, sort);
    }

    public List<DispatchSourceVesselWrapper> getDispatchSourceVesselCollection() {
        return getDispatchSourceVesselCollection(true);
    }

    public List<DispatchAliquotWrapper> getDispatchAliquotCollection(
        boolean sort) {
        return getWrapperCollection(DispatchPeer.DISPATCH_ALIQUOT_COLLECTION,
            DispatchAliquotWrapper.class, sort);
    }

    public List<DispatchAliquotWrapper> getDispatchAliquotCollection() {
        return getDispatchAliquotCollection(true);
    }

    public boolean hasErrors() {
        // TODO: return true if any dispatch items are missing or extra?
        return false;
    }
}
