package edu.ualberta.med.biobank.client.reports;


public class FvLPatientVisits extends AbstractReport {

    protected static final String NAME = "Clinic Activity by Study";

    public FvLPatientVisits() {
        super(
            "Reports the date of the first and last patient visit for each clinic in each study.",
            new String[] { "Study", "Clinic", "First Visit", "Last Visit" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
