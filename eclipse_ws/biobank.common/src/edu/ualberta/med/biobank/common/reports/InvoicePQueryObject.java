package edu.ualberta.med.biobank.common.reports;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.model.Sample;

public class InvoicePQueryObject extends QueryObject {

    private static String QUERY_STRING = "Select {0}Alias.patientVisit.patient.id, {0}Alias.patientVisit.patient.number, "
        + "{0}Alias.patientVisit.shipment.clinic.name, {0}Alias.linkDate, {0}Alias.sampleType.name"
        + " from "
        + Sample.class.getName()
        + " as {0}Alias where {0}Alias.linkDate > ? and {0}Alias.linkDate < ? and "
        + "{0}Alias.patientVisit.patient.study.site.id = {1} ORDER BY {0}Alias.patientVisit.patient.id";

    public InvoicePQueryObject(Integer siteId) {
        super(
            "Lists all samples for a particular date range, grouped by patient.",
            "Sample Invoice By Patient", MessageFormat.format(QUERY_STRING,
                "SIBP", siteId), new String[] { "Patient Id", "Patient Number",
                "Clinic", "Link Date", "Sample Type" });
        addOption("Start Date", Date.class, new Date(0));
        addOption("End Date", Date.class, new Date());
    }

    @Override
    public List<Object> postProcess(List<Object> results) {
        return results;
    }
}
