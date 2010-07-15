package edu.ualberta.med.biobank.client.reports;

import edu.ualberta.med.biobank.common.util.DateGroup;

public class PVsByStudy extends BiobankReport {

    protected static final String NAME = "Patient Visits per Study by Date";

    public PVsByStudy() {
        super(
            "Displays the total number of patient visits per study"
                + " grouped by date processed in a calendar week/month/quarter/year.",
            new String[] { "Study", "", "Total" });
        addOption("Date Range", DateGroup.class, DateGroup.Month);
    }

    @Override
    public String getName() {
        return NAME;
    }
}