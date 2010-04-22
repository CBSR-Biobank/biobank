package edu.ualberta.med.biobank.common.reports;

import java.util.Date;

import edu.ualberta.med.biobank.model.Aliquot;

public class AliquotInvoiceByPatient extends QueryObject {

    protected static final String NAME = "Aliquots per Patient by Date";

    public AliquotInvoiceByPatient(String op, Integer siteId) {
        super(
            "Lists all aliquots linked in a particular date range, ordered by patient.",
            "Select Alias.patientVisit.patient.pnumber, Alias.patientVisit.shipment.clinic.name,  Alias.linkDate, Alias.sampleType.name from "
                + Aliquot.class.getName()
                + " as Alias where Alias.linkDate > ? and Alias.linkDate < ? and Alias.patientVisit.patient.study.site "
                + op + siteId + " ORDER BY Alias.patientVisit.patient.pnumber",
            new String[] { "Patient Number", "Clinic", "Link Date",
                "Sample Type" });
        addOption("Start Date (Linked)", Date.class, new Date(0));
        addOption("End Date (Linked)", Date.class, new Date());
    }

    @Override
    public String getName() {
        return NAME;
    }
}
