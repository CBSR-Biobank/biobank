package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.reports2.ReportOption;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;

public class AliquotInvoiceByClinicImpl extends AbstractReport {

    private static String QUERY_STRING = "Select Alias.patientVisit.shipment.clinic.name, "
        + "Alias.patientVisit.patient.pnumber, "
        + "Alias.linkDate, Alias.sampleType.name  from "
        + Aliquot.class.getName()
        + " as Alias where Alias.aliquotPosition not in (from "
        + AliquotPosition.class.getName()
        + " a where a.container.label like '"
        + SENT_SAMPLES_FREEZER_NAME
        + "') and Alias.linkDate > ? and Alias.linkDate < ? and "
        + "Alias.patientVisit.patient.study.site.id "
        + siteOperatorString
        + siteIdString
        + " ORDER BY Alias.patientVisit.shipment.clinic.id, Alias.patientVisit.patient.pnumber";

    public AliquotInvoiceByClinicImpl(List<Object> parameters,
        List<ReportOption> options) {
        super(QUERY_STRING, parameters, options);
    }

}
