package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;

public class AliquotInvoiceByClinicImpl extends AbstractReport {

    private static String QUERY_STRING = "Select Alias.inventoryId, Alias.patientVisit.shipment.clinic.name, "
        + "Alias.patientVisit.patient.pnumber, "
        + "Alias.linkDate, Alias.sampleType.name  from "
        + Aliquot.class.getName()
        + " as Alias where Alias.aliquotPosition.id not in (from "
        + AliquotPosition.class.getName()
        + " a where a.container.label like '"
        + SENT_SAMPLES_FREEZER_NAME
        + "') and Alias.linkDate between ? and ? and "
        + "Alias.patientVisit.shipment.site.id "
        + SITE_OPERATOR
        + SITE_ID
        + " ORDER BY "
        + "Alias.patientVisit.shipment.clinic.id, Alias.patientVisit.patient.pnumber";

    public AliquotInvoiceByClinicImpl(BiobankReport report) {
        super(QUERY_STRING, report);
    }

}
