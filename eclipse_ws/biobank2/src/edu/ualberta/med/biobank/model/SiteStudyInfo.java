package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class SiteStudyInfo implements ITableInfo {

    public StudyWrapper studyWrapper;

    public long patientVisits;

    @Override
    public ModelWrapper<?> getDisplayedWrapper() {
        return studyWrapper;
    }

}
