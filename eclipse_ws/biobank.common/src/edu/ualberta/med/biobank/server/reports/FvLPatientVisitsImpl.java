package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.PatientVisit;

public class FvLPatientVisitsImpl extends AbstractReport {

    private static final String QUERY = "Select Alias.shipmentPatient.patient.study.nameShort,"
        + " Alias.shipmentPatient.shipment.clinic.nameShort, MIN(Alias.shipmentPatient.shipment.dateReceived),"
        + " MAX(Alias.shipmentPatient.shipment.dateReceived) from "
        + PatientVisit.class.getName()
        + " as Alias"
        + " GROUP BY Alias.shipmentPatient.patient.study.nameShort, Alias.shipmentPatient.shipment.clinic.nameShort";

    public FvLPatientVisitsImpl(BiobankReport report) {
        super(QUERY, report);
    }

}
