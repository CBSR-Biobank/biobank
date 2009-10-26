package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;

public class SiteClinicInfo {

    public ClinicWrapper clinicWrapper;

    public int studies;

    public int patients;

    public int patientVisits;

    public void performDoubleClick() {
        SessionManager.getInstance().openViewForm(clinicWrapper);
    }

}
