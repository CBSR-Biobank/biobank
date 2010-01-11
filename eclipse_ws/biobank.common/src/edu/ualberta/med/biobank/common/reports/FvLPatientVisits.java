package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.PatientVisit;

public class FvLPatientVisits extends QueryObject {

    public FvLPatientVisits(String op, Integer siteId) {
        super(
            "Compares the oldest and most recent activity by clinic.",
            "Select Alias.patient.study.name, Alias.shipment.clinic.name, MIN(Alias.shipment.dateReceived), MAX(Alias.shipment.dateReceived) from "
                + PatientVisit.class.getName()
                + " as Alias where Alias.patient.study.site "
                + op
                + siteId
                + " GROUP BY Alias.shipment.clinic", new String[] { "Study",
                "Clinic", "First Visit", "Last Visit" });
    }
}
