package edu.ualberta.med.biobank.client.reports;

public class CabinetSAliquots extends AbstractReport {

    protected static final String NAME = "Cabinet Aliquots per Study";

    public CabinetSAliquots() {
        super("Displays the total number of cabinet aliquots per study."
            + " Note: the top container's name must contain \"Cabinet\".",
            new String[] { "Study", "Total" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}