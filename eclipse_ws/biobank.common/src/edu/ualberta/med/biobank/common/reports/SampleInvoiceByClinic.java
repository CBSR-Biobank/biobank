package edu.ualberta.med.biobank.common.reports;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.model.Sample;

public class SampleInvoiceByClinic extends QueryObject {
    private static String QUERY_STRING = "Select Alias.patientVisit.shipment.clinic.name, "
        + "Alias.patientVisit.patient.id, Alias.patientVisit.patient.pnumber, "
        + "Alias.linkDate, Alias.sampleType.name  from "
        + Sample.class.getName()
        + " as Alias where Alias.linkDate > ? and Alias.linkDate < ? and "
        + "Alias.patientVisit.patient.study.site.id {1} {0} ORDER BY "
        + "Alias.patientVisit.shipment.clinic.id, Alias.patientVisit.patient.id";

    public SampleInvoiceByClinic(String op, Integer siteId) {
        super(
            "Lists all samples linked in a particular date range, ordered by clinic.",
            MessageFormat.format(QUERY_STRING, siteId, op), new String[] {
                "Clinic", "Patient Id", "Patient Number", "Link Date",
                "Sample Type" });
        addOption("Start Date", Date.class, new Date(0));
        addOption("End Date", Date.class, new Date());
    }

    @Override
    public List<Object> postProcess(List<Object> results) {
        return results;
    }
}
