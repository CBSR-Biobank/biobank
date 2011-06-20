package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;

public class AliquotInvoiceByPatientImpl extends AbstractReport {

    // private static String QUERY =
    // "Select Alias.inventoryId, Alias.patientVisit.shipmentPatient.patient.pnumber, Alias.patientVisit.shipmentPatient.shipment.clinic.name,  Alias.linkDate, Alias.sampleType.name from "
    // + Aliquot.class.getName()
    // +
    // " as Alias left join Alias.aliquotPosition p where (p is null or p not in (from "
    // + AliquotPosition.class.getName()
    // + " a where a.container.label like '"
    // + SENT_SAMPLES_FREEZER_NAME
    // + "')) and Alias.linkDate between ? and ?"
    // +
    // " ORDER BY Alias.patientVisit.shipmentPatient.patient.pnumber, Alias.inventoryId";

    public AliquotInvoiceByPatientImpl(BiobankReport report) {
        // super(QUERY, report);
        super("", report); //$NON-NLS-1$
    }

}
