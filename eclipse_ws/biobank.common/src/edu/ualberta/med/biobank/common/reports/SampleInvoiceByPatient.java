package edu.ualberta.med.biobank.common.reports;

import java.util.Date;

import edu.ualberta.med.biobank.model.Sample;

public class SampleInvoiceByPatient extends QueryObject {

    protected static final String NAME = "Samples per Patient by Date";

    public SampleInvoiceByPatient(String op, Integer siteId) {
        super(
            "Lists all samples linked in a particular date range, ordered by patient.",
            "Select Alias.patientVisit.patient.pnumber, Alias.patientVisit.shipment.clinic.name,  Alias.linkDate, Alias.sampleType.name from "
                + Sample.class.getName()
                + " as Alias where Alias.linkDate > ? and Alias.linkDate < ? and Alias.patientVisit.patient.study.site "
                + op + siteId + " ORDER BY Alias.patientVisit.patient.pnumber",
            new String[] { "Patient Number", "Clinic", "Link Date",
                "Sample Type" });
        addOption("Start Date", Date.class, new Date(0));
        addOption("End Date", Date.class, new Date());
    }

    @Override
    public String getName() {
        return NAME;
    }
}
