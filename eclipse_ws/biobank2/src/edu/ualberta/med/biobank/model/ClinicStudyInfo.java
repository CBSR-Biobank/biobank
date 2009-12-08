package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class ClinicStudyInfo implements ITableInfo {

    public StudyWrapper studyWrapper;

    public String studyShortName;

    public long patients;

    public long patientVisits;

    @Override
    public ModelWrapper<?> getDisplayedWrapper() {
        return studyWrapper;
    }

}
