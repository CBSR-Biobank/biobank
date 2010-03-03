package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Aliquot;

public class SampleCount extends QueryObject {

    protected static final String NAME = "Aliquot Type Totals";

    public SampleCount(String op, Integer siteId) {
        super("Lists the total number of each sample type.",
            "Select Alias.sampleType.name, count(*) from "
                + Aliquot.class.getName()
                + " as Alias where Alias.patientVisit.patient.study.site " + op
                + siteId + " GROUP BY Alias.sampleType.name", new String[] {
                "Aliquot Type", "Total" }, new int[] { 200, 150 });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
