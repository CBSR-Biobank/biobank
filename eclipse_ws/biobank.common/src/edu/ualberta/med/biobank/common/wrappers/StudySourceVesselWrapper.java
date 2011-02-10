package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.StudySourceVesselBaseWrapper;
import edu.ualberta.med.biobank.model.StudySourceVessel;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class StudySourceVesselWrapper extends StudySourceVesselBaseWrapper {

    public StudySourceVesselWrapper(WritableApplicationService appService,
        StudySourceVessel wrappedObject) {
        super(appService, wrappedObject);
    }

    public StudySourceVesselWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    public int compareTo(ModelWrapper<StudySourceVessel> o) {
        if (o instanceof StudySourceVesselWrapper) {
            return getSourceVesselType().compareTo(
                ((StudySourceVesselWrapper) o).getSourceVesselType());
        }
        return 0;
    }

}
