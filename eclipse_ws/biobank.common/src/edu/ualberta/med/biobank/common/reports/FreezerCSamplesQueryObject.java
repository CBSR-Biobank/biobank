package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Sample;

public class FreezerCSamplesQueryObject extends QueryObject {

    public FreezerCSamplesQueryObject(String name, Integer siteId) {
        super("Lists freezer samples by study.", name, "Select " + name
            + "Alias.patientVisit.patient.number, " + name
            + "Alias.patientVisit.patient.study.name, " + name
            + "Alias.patientVisit.clinic.name, " + name
            + "Alias.sampleType.name," + name + "Alias.inventoryId, " + name
            + "Alias.patientVisit.dateProcessed from " + Sample.class.getName()
            + " as " + name + "Alias where " + name
            + "Alias.patientVisit.patient.study.site = " + siteId
            + " Order BY " + name + "Alias.patientVisit.patient.study, " + name
            + "Alias.patientVisit.clinic",
            new String[] { "Patient Number", "Study", "Clinic", "Sample Type",
                "Inventory Id", "Date Processed" });
    }
}
