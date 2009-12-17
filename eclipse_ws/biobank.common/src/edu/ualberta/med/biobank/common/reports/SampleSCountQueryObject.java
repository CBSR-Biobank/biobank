package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Sample;

public class SampleSCountQueryObject extends QueryObject {

    public SampleSCountQueryObject(String name, Integer siteId) {
        super("Lists the total number of each sample type by study.", name,
            "Select " + name + "Alias.sampleType.name, " + name
                + "Alias.patientVisit.patient.study.name, count(*) from "
                + Sample.class.getName() + " as " + name + "Alias where "
                + name + "Alias.patientVisit.patient.study.site = " + siteId
                + "GROUP BY " + name + "Alias.patientVisit.patient.study",
            new String[] { "Sample Type", "Study", "Total" });
    }
}
