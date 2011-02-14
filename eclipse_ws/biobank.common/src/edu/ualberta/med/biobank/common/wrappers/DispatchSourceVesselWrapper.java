package edu.ualberta.med.biobank.common.wrappers;

import java.util.List;

import edu.ualberta.med.biobank.common.peer.DispatchSourceVesselPeer;
import edu.ualberta.med.biobank.common.wrappers.base.DispatchSourceVesselBaseWrapper;
import edu.ualberta.med.biobank.model.DispatchSourceVessel;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchSourceVesselWrapper extends
    DispatchSourceVesselBaseWrapper {

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

    @Override
    public Object getItem() {
        return getSourceVessel();
    }

    @Override
    public void setItem(Object item) {
        if (item instanceof SourceVesselWrapper) {
            setSourceVessel((SourceVesselWrapper) item);
        }
    }
}
