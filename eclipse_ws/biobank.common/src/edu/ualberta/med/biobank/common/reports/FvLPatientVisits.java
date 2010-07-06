package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.PatientVisit;

@Deprecated
public class FvLPatientVisits extends QueryObject {

    protected static final String NAME = "Clinic Activity by Study";

    public FvLPatientVisits(String op, Integer siteId) {
        super(
            "Reports the date of the first and last patient visit for each clinic in each study.",
            "Select Alias.patient.study.nameShort, Alias.shipment.clinic.nameShort, MIN(Alias.shipment.dateReceived), MAX(Alias.shipment.dateReceived) from "
                + PatientVisit.class.getName()
                + " as Alias where Alias.patient.study.site "
                + op
                + siteId
                + " GROUP BY Alias.patient.study.nameShort, Alias.shipment.clinic.nameShort",
            new String[] { "Study", "Clinic", "First Visit", "Last Visit" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
