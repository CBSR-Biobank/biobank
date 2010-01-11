package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Sample;

public class FreezerCSamples extends QueryObject {

    public FreezerCSamples(String op, Integer siteId) {
        super(
            "Displays the total number of freezer samples per study per clinic.",
            "Select Alias.patientVisit.patient.study.name,  Alias.patientVisit.shipment.clinic.name, count (*) from "
                + Sample.class.getName()
                + " as Alias where Alias.patientVisit.patient.study.site "
                + op
                + siteId
                + " GROUP BY Alias.patientVisit.patient.study, Alias.patientVisit.shipment.clinic",
            new String[] { "Study", "Clinic", "Total" });
    }
}
