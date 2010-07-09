package edu.ualberta.med.biobank.client.reports;

public class AliquotSCount extends AbstractReport {

    protected static final String NAME = "Sample Types by Study";

    public AliquotSCount() {
        super("Lists the total number of each aliquot sample type by study.",
            new String[] { "Study", "Sample Type", "Total" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
