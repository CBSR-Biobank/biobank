package edu.ualberta.med.biobank.common.wrappers;

import java.util.Collection;

import edu.ualberta.med.biobank.common.peer.SourcePeer;
import edu.ualberta.med.biobank.model.Source;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SourceWrapper extends AbstractShipmentWrapper<Source> {
    public SourceWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public SourceWrapper(WritableApplicationService appService, Source source) {
        super(appService, source);
    }

    @Override
    public Class<Source> getWrappedClass() {
        return Source.class;
    }

    public CenterWrapper getSourceCenter() {
        return getWrappedProperty(SourcePeer.SOURCE_CENTER, CenterWrapper.class);
    }

    public void setSourceCenter(CenterWrapper center) {
        setWrappedProperty(SourcePeer.SOURCE_CENTER, center);
    }

    public Collection<SourceVesselWrapper> getSourceVesselCollection() {
        return getWrapperCollection(SourcePeer.SOURCE_VESSEL_COLLECTION,
            SourceVesselWrapper.class, false);
    }

    public void setSourceVesselCollection(
        Collection<SourceVesselWrapper> sourceVesselCollection) {
        setWrapperCollection(SourcePeer.SOURCE_VESSEL_COLLECTION,
            sourceVesselCollection);
    }
}
