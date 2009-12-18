package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.PatientVisit;

public class FvLPatientVisitsQueryObject extends QueryObject {

    public FvLPatientVisitsQueryObject(String name, Integer siteId) {
        super("Compares the oldest and most recent activity by clinic.", name,
            "Select " + name + "Alias.patient.study.name, " + name
                + "Alias.shipment.clinic.name, MIN(" + name
                + "Alias.shipment.dateReceived), MAX(" + name
                + "Alias.shipment.dateReceived) from "
                + PatientVisit.class.getName() + " as " + name + "Alias where "
                + name + "Alias.patient.study.site = " + siteId + " GROUP BY "
                + name + "Alias.shipment.clinic", new String[] { "Study",
                "Clinic", "First Visit", "Last Visit" });
    }
}
