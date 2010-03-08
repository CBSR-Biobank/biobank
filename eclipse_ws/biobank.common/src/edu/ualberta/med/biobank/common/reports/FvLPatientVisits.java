package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.PatientVisit;

public class FvLPatientVisits extends QueryObject {

    protected static final String NAME = "Clinic Activity";

    public FvLPatientVisits(String op, Integer siteId) {
        super(
            "Reports the date of the first and last patient visit for each clinic.",
            "Select Alias.shipment.clinic.name, MIN(Alias.shipment.dateReceived), MAX(Alias.shipment.dateReceived) from "
                + PatientVisit.class.getName()
                + " as Alias where Alias.patient.study.site "
                + op
                + siteId
                + " GROUP BY Alias.shipment.clinic ORDER By Alias.shipment.clinic.name",
            new String[] { "Clinic", "First Visit", "Last Visit" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
