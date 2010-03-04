package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.PatientVisit;

public class FvLPatientVisits extends QueryObject {

    protected static final String NAME = "Clinic Activity";

    public FvLPatientVisits(String op, Integer siteId) {
        super(
            "Reports the date of the first and last patient visit for each clinic.",
            "Select Alias.patient.study.nameShort, Alias.shipment.clinic.name, MIN(Alias.shipment.dateReceived), MAX(Alias.shipment.dateReceived) from "
                + PatientVisit.class.getName()
                + " as Alias where Alias.patient.study.site "
                + op
                + siteId
                + " GROUP BY Alias.shipment.clinic ORDER By Alias.patient.study.nameShort, Alias.shipment.clinic.name",
            new String[] { "Study", "Clinic", "First Visit", "Last Visit" },
            new int[] { 100, 200, 100, 100 });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
