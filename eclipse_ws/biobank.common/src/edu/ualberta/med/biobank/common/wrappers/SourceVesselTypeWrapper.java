package edu.ualberta.med.biobank.common.wrappers;

import java.util.List;

import edu.ualberta.med.biobank.common.peer.SourceVesselTypePeer;
import edu.ualberta.med.biobank.model.SourceVesselType;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SourceVesselTypeWrapper extends ModelWrapper<SourceVesselType> {

    public SourceVesselTypeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return SourceVesselTypePeer.PROP_NAMES;
    }

    @Override
    public Class<SourceVesselType> getWrappedClass() {
        return SourceVesselType.class;
    }

    public String getName() {
        return getProperty(SourceVesselTypePeer.NAME);
    }

    public void setName(String name) {
        setProperty(SourceVesselTypePeer.NAME, name);
    }

}
