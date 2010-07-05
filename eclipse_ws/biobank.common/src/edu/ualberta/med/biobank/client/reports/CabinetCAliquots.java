package edu.ualberta.med.biobank.client.reports;

public class CabinetCAliquots extends AbstractReport {

    public static final String NAME = "Cabinet Aliquots per Study per Clinic";

    public CabinetCAliquots() {
        super(
            "Displays the total number of cabinet aliquots per study per clinic. Note: the top container's name must contain \"Cabinet\".",
            new String[] { "Study", "Clinic", "Total" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}