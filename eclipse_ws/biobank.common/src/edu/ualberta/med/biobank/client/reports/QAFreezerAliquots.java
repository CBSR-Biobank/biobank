package edu.ualberta.med.biobank.client.reports;

import java.util.Date;

public class QAFreezerAliquots extends AbstractReport {

    protected static final String NAME = "Freezer Aliquot QA";
    int numResults;

    public QAFreezerAliquots() {
        super(
            "Retrieves a list of aliquots, at random, within a date range,"
                + " by sample type. Note: the number of aliquots must be specified,"
                + " and the top container's name must contain \"Freezer\".",
            new String[] { "Label", "Inventory ID", "Patient", "Visit",
                "Date Processed", "Sample Type" });
        addOption("Start Date (Processed)", Date.class, new Date(0));
        addOption("End Date (Processed)", Date.class, new Date());
        addOption("Sample Type", String.class, "");
        addOption("# Aliquots", Integer.class, 0);
    }

    @Override
    public String getName() {
        return NAME;
    }

}