package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Sample;

public class FreezerDSamples extends QueryObject {

    public enum DateRange {
        Week, Month, Quarter, Year
    }

    public FreezerDSamples(String op, Integer siteId) {
        super(
            "Displays the total number of freezer samples per study per clinic by date range.",
            "Select Alias.patientVisit.patient.study.name, Alias.patientVisit.shipment.clinic.name, count (*) from "
                + Sample.class.getName()
                + " as Alias where Alias.patientVisit.patient.study.site "
                + op
                + siteId
                + " GROUP BY ?(Alias.linkDate), Alias.patientVisit.patient.study, Alias.patientVisit.shipment.clinic, count(*)",
            new String[] { "week", "Study", "Clinic", "Total" });
        addOption("Date Range", DateRange.class, DateRange.Week);
    }
}