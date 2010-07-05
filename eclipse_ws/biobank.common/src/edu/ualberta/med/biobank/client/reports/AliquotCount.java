package edu.ualberta.med.biobank.client.reports;

public class AliquotCount extends AbstractReport {

    private static final long serialVersionUID = 1L;

    private static final String NAME = "Sample Type Totals";

    public AliquotCount() {
        super("Lists the total number of aliquots per sample type.",
            new String[] { "Sample Type", "Total" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
