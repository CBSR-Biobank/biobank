package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;

public class SiteClinicInfo implements ITableInfo {

    public ClinicWrapper clinicWrapper;

    public int studies;

    public Long patients;

    public int patientVisits;

    public String activityStatus;

    @Override
    public ModelWrapper<?> getDisplayedWrapper() {
        return clinicWrapper;
    }

}
