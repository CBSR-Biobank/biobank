package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Sample;

public class SampleSCount extends QueryObject {

    public SampleSCount(String name, Integer siteId) {
        super("Lists the total number of each sample type by study.", name,
            "Select " + name + "Alias.patientVisit.patient.study.name, " + name
                + "Alias.sampleType.name, count(*) from "
                + Sample.class.getName() + " as " + name + "Alias where "
                + name + "Alias.patientVisit.patient.study.site = " + siteId
                + "GROUP BY " + name + "Alias.patientVisit.patient.study",
            new String[] { "Study", "Sample Type", "Total" });
    }
}
