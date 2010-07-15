package edu.ualberta.med.biobank.client.reports;

public class PatientWBC extends BiobankReport {

    protected static final String NAME = "Patient WBC Aliquots";

    public PatientWBC() {
        super(
            "Displays a list of the WBC aliquots located in Cabinets taken from"
                + " a patient. Note: the full name of the sample type must contain"
                + " \"DNA\", and the top container's name must contain \"Cabinet\"",
            new String[] { "Study", "Clinic", "Patient", "Date", "Sample Type",
                "Inventory ID", "Location" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
