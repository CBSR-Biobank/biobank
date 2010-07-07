package edu.ualberta.med.biobank.client.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.util.DateGroup;

public class FreezerDAliquots extends AbstractReport {

    protected static final String NAME = "Freezer Aliquots per Study per Clinic by Date";

    public FreezerDAliquots() {
        super(
            "Displays the total number of freezer aliquots per study per"
                + " clinic grouped by link date in a calendar week/month/quarter/year."
                + " Note: the top container's name must contain \"Freezer\".",
            new String[] { "Study", "Clinic", "", "Total" });
        addOption("Date Range", DateGroup.class, DateGroup.Week);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void doColumnModification(List<Object> parameters) {
        columnNames[2] = (String) parameters.get(0);
    }
}