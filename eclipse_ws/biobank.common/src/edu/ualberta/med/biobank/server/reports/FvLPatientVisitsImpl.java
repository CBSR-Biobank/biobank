package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.PatientVisit;

public class FvLPatientVisitsImpl extends AbstractReport {

    private static final String QUERY = "Select Alias.patient.study.nameShort,"
        + " Alias.shipment.clinic.nameShort, MIN(Alias.shipment.dateReceived),"
        + " MAX(Alias.shipment.dateReceived) from "
        + PatientVisit.class.getName()
        + " as Alias where Alias.shipment.site "
        + SITE_OPERATOR
        + SITE_ID
        + " GROUP BY Alias.patient.study.nameShort, Alias.shipment.clinic.nameShort";

    public FvLPatientVisitsImpl(BiobankReport report) {
        super(QUERY, report);
    }

}
