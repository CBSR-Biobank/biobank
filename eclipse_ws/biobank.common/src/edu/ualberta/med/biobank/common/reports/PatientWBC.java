package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.PatientVisit;

public class PatientWBC extends QueryObject {

    protected static final String NAME = "Patient WBC Aliquots";

    public PatientWBC(String op, Integer siteId) {
        super(
            "Displays a list of the aliquots containing WBC taken from a patient.",
            "Select Alias.patient.study.nameShort, Alias.shipment.clinic.name, "
                + "Alias.patient.pnumber, Alias.dateProcessed, aliquot.id from "
                + PatientVisit.class.getName()
                + " as Alias left join Alias.aliquotCollection as aliquot where Alias.patient.study.site "
                + op + siteId
                + " and aliquot.sampleType.nameShort = 'DNA (WBC)'",
            new String[] { "Study", "Clinic", "Patient", "Date", "ID" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
