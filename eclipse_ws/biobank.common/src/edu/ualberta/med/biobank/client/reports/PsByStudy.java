package edu.ualberta.med.biobank.client.reports;

import edu.ualberta.med.biobank.common.reports2.DateGroup;

public class PsByStudy extends AbstractReport {

    protected static final String NAME = "Patients per Study by Date";

    public PsByStudy() {
        super(
            "Displays the total number of patients per study with at least one"
                + " patient visit grouped by date processed in a calendar"
                + " week/month/quarter/year.", new String[] { "Study", "",
                "Total" });
        addOption("Date Range", DateGroup.class, DateGroup.Month);
    }

    @Override
    public String getName() {
        return NAME;
    }
}