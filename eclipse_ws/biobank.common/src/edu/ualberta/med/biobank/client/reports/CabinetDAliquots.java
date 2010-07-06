package edu.ualberta.med.biobank.client.reports;

import edu.ualberta.med.biobank.common.util.DateGroup;

public class CabinetDAliquots extends AbstractReport {

    protected static final String NAME = "Cabinet Aliquots per Study per Clinic by Date";

    public CabinetDAliquots() {
        super(
            "Displays the total number of cabinet aliquots per study per clinic grouped"
                + " by link date in a calendar week/month/quarter/year. Note: the"
                + " top container's name must contain \"Cabinet\".",
            new String[] { "Study", "Clinic", "", "Total" });
        addOption("Date Range", DateGroup.class, DateGroup.Week);
    }

    @Override
    public String getName() {
        return NAME;
    }

}