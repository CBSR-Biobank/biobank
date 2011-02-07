package edu.ualberta.med.biobank.common.wrappers;

import java.util.List;

import edu.ualberta.med.biobank.common.peer.ResearchGroupPeer;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ResearchGroupWrapper extends CenterWrapper<ResearchGroup> {

    public ResearchGroupWrapper(WritableApplicationService appService,
        ResearchGroup rg) {
        super(appService, rg);
    }

    public Study getStudy() {
        return getProperty(ResearchGroupPeer.STUDY);
    }

    public void setStudy(StudyWrapper study) {
        setWrappedProperty(ResearchGroupPeer.STUDY, study);
    }

    @Override
    public Class<ResearchGroup> getWrappedClass() {
        return ResearchGroup.class;
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return ResearchGroupPeer.PROP_NAMES;
    }

}