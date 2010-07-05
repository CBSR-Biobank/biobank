package edu.ualberta.med.biobank.client.reports;

public class AliquotsByPallet extends AbstractReport {

    protected static final String NAME = "Aliquots by Pallet";

    public AliquotsByPallet() {
        super("Given a pallet label, generate a list of aliquots.",
            new String[] { "Location", "Inventory ID", "Patient", "Type" });
        addOption("Pallet Label", String.class, "");
        addOption("Top Container Type", String.class, "");
    }

    @Override
    public String getName() {
        return NAME;
    }
}