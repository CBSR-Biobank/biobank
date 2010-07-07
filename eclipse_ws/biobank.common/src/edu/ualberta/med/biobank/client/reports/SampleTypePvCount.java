package edu.ualberta.med.biobank.client.reports;

public class SampleTypePvCount extends AbstractReport {

    protected static final String NAME = "Sample Type Totals by Patient Visit and Study";

    public SampleTypePvCount() {
        super(
            "Lists the total number of each sample type per patient visit for a specified study.",
            new String[] { "Patient Number", "Date Processed", "Date Drawn",
                "Sample Type", "Total" });
        addOption("Study", String.class, "");
    }

    @Override
    public String getName() {
        return NAME;
    }
}
