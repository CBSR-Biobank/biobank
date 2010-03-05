package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.PatientVisit;

public class PatientWBC extends QueryObject {

    protected static final String NAME = "Patient WBC Samples";

    public PatientWBC(String op, Integer siteId) {
        super(
            "Displays a list of the WBC samples taken from a patient.",
            "Select Alias.patient.study.nameShort, Alias.shipment.clinic.name, Alias.patient.pnumber, Alias.dateProcessed, sample.id from "
                + PatientVisit.class.getName()
                + " as Alias left join Alias.sampleCollection as sample where Alias.patient.study.site "
                + op
                + siteId
                + " and sample.sampleType.nameShort = 'DNA (WBC)'",
            new String[] { "Study", "Clinic", "Patient", "Date", "ID" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
