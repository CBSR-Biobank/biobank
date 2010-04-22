package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.PatientVisit;

public class PatientWBC extends QueryObject {

    protected static final String NAME = "Patient WBC Aliquots";

    public PatientWBC(String op, Integer siteId) {
        super(
            "Displays a list of the WBC aliquots taken from a patient. Note: the full name of the sample type must contain \"DNA\".",
            "Select Alias.patient.study.nameShort, Alias.shipment.clinic.name, "
                + "Alias.patient.pnumber, Alias.dateProcessed, aliquot.sampleType.name, aliquot.inventoryId, aliquot.aliquotPosition.container.label from "
                + PatientVisit.class.getName()
                + " as Alias left join Alias.aliquotCollection as aliquot where Alias.patient.study.site "
                + op + siteId + " and aliquot.sampleType.name LIKE '%DNA%'",
            new String[] { "Study", "Clinic", "Patient", "Date", "Sample Type",
                "Inventory ID", "Location" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
