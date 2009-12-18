package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Sample;

public class SampleCountQueryObject extends QueryObject {

    public SampleCountQueryObject(String name, Integer siteId) {
        super("Lists the total number of each sample type.", name, "Select "
            + name + "Alias.sampleType.name, count(*) from "
            + Sample.class.getName() + " as " + name + "Alias where " + name
            + "Alias.patientVisit.patient.study.site = " + siteId,
        // + " ORDER BY " + name + "Alias.patientVisit.patient.id"
            new String[] { "Sample Type", "Total" });
    }
}
