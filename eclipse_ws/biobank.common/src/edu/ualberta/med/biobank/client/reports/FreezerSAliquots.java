package edu.ualberta.med.biobank.client.reports;


public class FreezerSAliquots extends AbstractReport {

    protected static final String NAME = "Freezer Aliquots per Study";

    public FreezerSAliquots() {
        super("Displays the total number of freezer aliquots per study."
            + " Note: the top container's name must contain \"Freezer\".",
            new String[] { "Study", "Total" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}