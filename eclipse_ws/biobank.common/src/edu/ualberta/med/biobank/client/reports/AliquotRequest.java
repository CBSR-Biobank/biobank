package edu.ualberta.med.biobank.client.reports;

public class AliquotRequest extends AbstractReport {

    protected static final String NAME = "Aliquot Request by CSV file";

    public AliquotRequest() {
        super(
            "Given a CSV file detailing a request (Patient Number, Date Drawn, Sample Type, # Requested), generate a list of aliquot locations.",
            new String[] { "Patient", "Inventory ID", "Date Drawn", "Type",
                "Location" });
        addOption("CSV File", String.class, "");
    }

    @Override
    public String getName() {
        return NAME;
    }
}
