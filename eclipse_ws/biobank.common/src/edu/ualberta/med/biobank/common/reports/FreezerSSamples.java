package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Sample;

public class FreezerSSamples extends QueryObject {

    public FreezerSSamples(String op, Integer siteId) {
        super("Displays the total number of freezer samples per study.",
            "Select Alias.patientVisit.patient.study.name, count (*) from "
                + Sample.class.getName()
                + " as Alias where Alias.patientVisit.patient.study.site " + op
                + siteId + " GROUP BY Alias.patientVisit.patient.study",
            new String[] { "Study", "Total" });
    }
}
