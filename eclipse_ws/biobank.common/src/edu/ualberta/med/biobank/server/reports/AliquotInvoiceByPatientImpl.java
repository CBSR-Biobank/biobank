package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.util.ReportOption;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;

public class AliquotInvoiceByPatientImpl extends AbstractReport {

    private static String QUERY = "Select Alias.patientVisit.patient.pnumber, Alias.patientVisit.shipment.clinic.name,  Alias.linkDate, Alias.sampleType.name from "
        + Aliquot.class.getName()
        + " as Alias where Alias.aliquotPosition not in (from "
        + AliquotPosition.class.getName()
        + " a where a.container.label like '"
        + SENT_SAMPLES_FREEZER_NAME
        + "') and Alias.linkDate > ? and Alias.linkDate < ? and Alias.patientVisit.patient.study.site "
        + SITE_OPERATOR
        + SITE_ID
        + " ORDER BY Alias.patientVisit.patient.pnumber";

    public AliquotInvoiceByPatientImpl(List<Object> parameters,
        List<ReportOption> options) {
        super(QUERY, parameters, options);
    }

}
