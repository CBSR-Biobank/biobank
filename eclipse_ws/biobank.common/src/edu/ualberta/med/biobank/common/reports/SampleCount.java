package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Sample;

public class SampleCount extends QueryObject {

    protected static final String NAME = "Sample Type Totals";

    public SampleCount(String op, Integer siteId) {
        super("Lists the total number of each sample type.",
            "Select Alias.sampleType.name, count(*) from "
                + Sample.class.getName()
                + " as Alias where Alias.patientVisit.patient.study.site " + op
                + siteId + " GROUP BY Alias.sampleType.name", new String[] {
                "Sample Type", "Total" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
