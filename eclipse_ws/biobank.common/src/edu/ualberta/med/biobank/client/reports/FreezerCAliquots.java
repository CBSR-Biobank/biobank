package edu.ualberta.med.biobank.client.reports;


public class FreezerCAliquots extends AbstractReport {

    protected static final String NAME = "Freezer Aliquots per Study per Clinic";

    public FreezerCAliquots() {
        super(
            "Displays the total number of freezer aliquots per study per clinic."
                + " Note: the top container's name must contain \"Freezer\".",
            new String[] { "Study", "Clinic", "Total" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}