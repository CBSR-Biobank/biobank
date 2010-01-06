package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Sample;

public class FreezerCSamplesQueryObject extends QueryObject {

    public FreezerCSamplesQueryObject(String name, Integer siteId) {
        super(
            "Displays the total number of freezer samples per study per clinic.",
            name, "Select " + name + "Alias.patientVisit.patient.study.name, "
                + name
                + "Alias.patientVisit.shipment.clinic.name, count (*) from "
                + Sample.class.getName() + " as " + name + "Alias where "
                + name + "Alias.patientVisit.patient.study.site = " + siteId
                + " GROUP BY " + name + "Alias.patientVisit.patient.study, "
                + name + "Alias.patientVisit.shipment.clinic", new String[] {
                "Study", "Clinic", "Total" });
    }
}
