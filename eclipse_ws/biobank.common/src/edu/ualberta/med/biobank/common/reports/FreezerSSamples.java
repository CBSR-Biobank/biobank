package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Sample;

public class FreezerSSamples extends QueryObject {

    public FreezerSSamples(String name, Integer siteId) {
        super("Displays the total number of freezer samples per study.", name,
            "Select " + name
                + "Alias.patientVisit.patient.study.name, count (*) from "
                + Sample.class.getName() + " as " + name + "Alias where "
                + name + "Alias.patientVisit.patient.study.site = " + siteId
                + " GROUP BY " + name + "Alias.patientVisit.patient.study",
            new String[] { "Study", "Total" });
    }
}
