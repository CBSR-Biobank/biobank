package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Sample;

public class FreezerDSamplesQueryObject extends QueryObject {

    public enum DateRange {
        Week, Month, Quarter, Year
    }

    public FreezerDSamplesQueryObject(String name, Integer siteId) {
        super(
            "Displays the total number of freezer samples per study per clinic by date range.",
            name, "Select " + name + "Alias.patientVisit.patient.study.name, "
                + name
                + "Alias.patientVisit.shipment.clinic.name, count (*) from "
                + Sample.class.getName() + " as " + name + "Alias where "
                + name + "Alias.patientVisit.patient.study.site = " + siteId
                + " GROUP BY WEEK(" + name + "Alias.linkDate), " + name
                + "Alias.patientVisit.patient.study, " + name
                + "Alias.patientVisit.shipment.clinic, count(*)", new String[] {
                "week", "Study", "Clinic", "Total" });
        // addOption("Date Range", DateRange.class, null);
    }
}