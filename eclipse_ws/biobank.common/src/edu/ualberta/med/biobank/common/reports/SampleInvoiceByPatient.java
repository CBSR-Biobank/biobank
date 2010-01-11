package edu.ualberta.med.biobank.common.reports;

import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.model.Sample;

public class SampleInvoiceByPatient extends QueryObject {
    public SampleInvoiceByPatient(String op, Integer siteId) {
        super(
            "Lists all samples linked in a particular date range, ordered by patient.",
            "Select Alias.patientVisit.patient.id, Alias.patientVisit.shipment.clinic.id, Alias.patientVisit.patient.pnumber, Alias.linkDate, Alias.sampleType.name from "
                + Sample.class.getName()
                + " as Alias where Alias.linkDate > ? and Alias.linkDate < ? and Alias.patientVisit.patient.study.site "
                + op + siteId + " ORDER BY Alias.patientVisit.patient.id",
            new String[] { "Patient Id", "Clinic Id", "Patient Number",
                "Link Date", "Sample Type" });
        addOption("Start Date", Date.class, new Date(0));
        addOption("End Date", Date.class, new Date());
    }

    @Override
    public List<Object> postProcess(List<Object> results) {
        return results;
    }
}
