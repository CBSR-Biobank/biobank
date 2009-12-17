package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Sample;

public class FreezerSSamplesQueryObject extends QueryObject {

    public FreezerSSamplesQueryObject(String name, Integer siteId) {
        super("Lists freezer samples by study.", name, "Select " + name
            + "Alias.patientVisit.patient.number, " + name
            + "Alias.patientVisit.patient.study.name, " + name
            + "Alias.sampleType.name," + name + "Alias.inventoryId, " + name
            + "Alias.patientVisit.dateProcessed from " + Sample.class.getName()
            + " as " + name + "Alias where " + name
            + "Alias.patientVisit.patient.study.site = " + siteId
            + " Order BY " + name + "Alias.patientVisit.patient.study",
            new String[] { "Patient Number", "Study", "Sample Type",
                "Inventory Id", "Date Processed" });
    }
}
