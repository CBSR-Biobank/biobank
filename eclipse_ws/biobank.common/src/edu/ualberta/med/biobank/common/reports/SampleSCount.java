package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Sample;

public class SampleSCount extends QueryObject {

    protected static final String NAME = "Sample Types by Study";

    public SampleSCount(String op, Integer siteId) {
        super(
            "Lists the total number of each sample type by study.",
            "Select Alias.patientVisit.patient.study.nameShort, Alias.sampleType.name, count(*) from "
                + Sample.class.getName()
                + " as Alias where Alias.patientVisit.patient.study.site "
                + op
                + siteId
                + " GROUP BY Alias.patientVisit.patient.study.nameShort, Alias.sampleType.name",
            new String[] { "Study", "Sample Type", "Total" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
