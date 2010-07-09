package edu.ualberta.med.biobank.client.reports;

import java.util.Date;

public class PatientVisitSummary extends AbstractReport {

    protected static final String NAME = "Patient Visit Statistics by Study and Clinic";

    public PatientVisitSummary() {
        super("Displays the total number of patients for each of 1-5+ visits,"
            + " the total number of visits, and the total number of patients"
            + " per study per clinic for a given date range.", new String[] {
            "Study", "Clinic", "1 Visit", "2 Visit", "3 Visit", "4 Visit",
            "5+ Visits", "Total Visits", "Total Patients" });
        addOption("Start Date (Processed)", Date.class, new Date(0));
        addOption("End Date (Processed)", Date.class, new Date());
    }

    @Override
    public String getName() {
        return NAME;
    }
}
