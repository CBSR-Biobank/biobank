package edu.ualberta.med.biobank.common.reports;

import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.model.Sample;

public class SampleInvoiceByPatient extends QueryObject {
    public SampleInvoiceByPatient(String name, Integer siteId) {
        super(
            "Lists all samples linked in a particular date range, ordered by patient.",
            name, "Select " + name + "Alias.patientVisit.patient.id, " + name
                + "Alias.patientVisit.shipment.clinic.id, " + name
                + "Alias.patientVisit.patient.number, " + name
                + "Alias.linkDate, " + name + "Alias.sampleType.name"
                + " from " + Sample.class.getName() + " as " + name
                + "Alias where " + name + "Alias.linkDate > ? and " + name
                + "Alias.linkDate < ? and " + name
                + "Alias.patientVisit.patient.study.site = " + siteId
                + " ORDER BY " + name + "Alias.patientVisit.patient.id",
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
