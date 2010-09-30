package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.PatientVisit;

public class FvLPatientVisitsImpl extends AbstractReport {

    private static final String QUERY =
        "Select Alias.clinicShipmentPatient.patient.study.nameShort,"
            + " Alias.clinicShipmentPatient.clinicShipment.clinic.nameShort, MIN(Alias.clinicShipmentPatient.clinicShipment.dateReceived),"
            + " MAX(Alias.clinicShipmentPatient.clinicShipment.dateReceived) from "
            + PatientVisit.class.getName()
            + " as Alias where Alias.clinicShipmentPatient.clinicShipment.site "
            + SITE_OPERATOR
            + SITE_ID
            + " GROUP BY Alias.clinicShipmentPatient.patient.study.nameShort, Alias.clinicShipmentPatient.clinicShipment.clinic.nameShort";

    public FvLPatientVisitsImpl(BiobankReport report) {
        super(QUERY, report);
    }

}
