package edu.ualberta.med.biobank.client.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.util.DateGroup;

public class NewPVsByStudyClinic extends AbstractReport {

    protected static final String NAME = "New Patient Visits per Study per Clinic by Date";

    public NewPVsByStudyClinic() {
        super(
            "Displays the total number of patient visits added per study per "
                + "clinic grouped by date processed in a calendar week/month/quarter/year.",
            new String[] { "Study", "Clinic", "", "Total" });
        addOption("Date Range", DateGroup.class, DateGroup.Month);
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