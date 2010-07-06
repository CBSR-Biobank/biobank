package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.util.ReportOption;
import edu.ualberta.med.biobank.model.PatientVisit;

public class FvLPatientVisitsImpl extends AbstractReport {

    private static final String QUERY = "Select Alias.patient.study.nameShort,"
        + " Alias.shipment.clinic.nameShort, MIN(Alias.shipment.dateReceived),"
        + " MAX(Alias.shipment.dateReceived) from "
        + PatientVisit.class.getName()
        + " as Alias where Alias.patient.study.site "
        + siteOperatorString
        + siteIdString
        + " GROUP BY Alias.patient.study.nameShort, Alias.shipment.clinic.nameShort";

    public FvLPatientVisitsImpl(List<Object> parameters, List<ReportOption> options) {
        super(QUERY, parameters, options);
    }

}
