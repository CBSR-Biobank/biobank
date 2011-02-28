package edu.ualberta.med.biobank.common.wrappers;

import gov.nih.nci.system.applicationservice.WritableApplicationService;

@SuppressWarnings("unused")
@Deprecated
public class StudySourceVesselWrapper {

    private static class StudySourceVessel {

    }

    public StudySourceVesselWrapper(WritableApplicationService appService,
        StudySourceVessel wrappedObject) {
    }

    public StudySourceVesselWrapper(WritableApplicationService appService) {
    }

    public int compareTo(ModelWrapper<StudySourceVessel> o) {
        return 0;
    }

    public SourceVesselWrapper getSourceVessel() {
        return null;
    }

    @Deprecated
    public void setSourceVessel(SourceVesselWrapper sourceVessel) {
    }

}
