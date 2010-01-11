package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Sample;

public class SampleCount extends QueryObject {

    public SampleCount(String op, Integer siteId) {
        super("Lists the total number of each sample type.",
            "Select Alias.sampleType.name, count(*) from "
                + Sample.class.getName()
                + " as Alias where Alias.patientVisit.patient.study.site " + op
                + siteId,
            // + " ORDER BY " + name + "Alias.patientVisit.patient.id"
            new String[] { "Sample Type", "Total" });
    }
}
