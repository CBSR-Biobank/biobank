package edu.ualberta.med.biobank.common.wrappers;

import java.util.List;

import edu.ualberta.med.biobank.common.peer.DispatchSourceVesselPeer;
import edu.ualberta.med.biobank.model.DispatchSourceVessel;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchSourceVesselWrapper extends
    DispatchItemWrapper<DispatchSourceVessel> {

    public DispatchSourceVesselWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchSourceVesselWrapper(WritableApplicationService appService,
        DispatchSourceVessel dispatchSourceVessel) {
        super(appService, dispatchSourceVessel);
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return DispatchSourceVesselPeer.PROP_NAMES;
    }

    @Override
    public Class<DispatchSourceVessel> getWrappedClass() {
        return DispatchSourceVessel.class;
    }

    public SourceVesselWrapper getSourceVessel() {
        return getWrappedProperty(DispatchSourceVesselPeer.SOURCE_VESSEL,
            SourceVesselWrapper.class);
    }

    public void setSourceVessel(SourceVesselWrapper sourceVessel) {
        setWrappedProperty(DispatchSourceVesselPeer.SOURCE_VESSEL, sourceVessel);
    }

    public DispatchWrapper getDispatch() {
        return getWrappedProperty(DispatchSourceVesselPeer.DISPATCH,
            DispatchWrapper.class);
    }

    public void setDispatch(DispatchWrapper dispatch) {
        setWrappedProperty(DispatchSourceVesselPeer.DISPATCH, dispatch);
    }

    @Override
    public int compareTo(ModelWrapper<DispatchSourceVessel> object) {
        if (object instanceof DispatchSourceVesselWrapper) {
            DispatchSourceVesselWrapper dsv = (DispatchSourceVesselWrapper) object;
            return getSourceVessel().compareTo(dsv.getSourceVessel());
        }
        return super.compareTo(object);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DispatchSourceVesselWrapper && object != null) {
            DispatchSourceVesselWrapper dsv = (DispatchSourceVesselWrapper) object;
            if (isNew() && dsv.isNew()) {
                return getSourceVessel() != null
                    && dsv.getSourceVessel() != null
                    && getSourceVessel().equals(dsv.getSourceVessel())
                    && getDispatch() != null && dsv.getDispatch() != null
                    && getDispatch().equals(dsv.getDispatch());
            }
        }
        return super.equals(object);
    }
}
