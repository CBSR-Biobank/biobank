package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.PatientVisit;

public class PatientWBC extends QueryObject {

    public PatientWBC(String name, Integer siteId) {
        super("Displays a list of the WBC samples taken from a patient.", name,
            "Select " + name + "Alias.patient.study.name, " + name
                + "Alias.shipment.clinic.name, " + name
                + "Alias.patient.pnumber, " + name + "Alias.dateProcessed, "
                + "sample.id from " + PatientVisit.class.getName() + " as "
                + name + "Alias left join " + name
                + "Alias.sampleCollection as sample where " + name
                + "Alias.patient.study.site = " + siteId
                + " and sample.sampleType.nameShort = DNA(WBC)", new String[] {
                "Study", "Clinic", "Patient", "Date", "ID" });
    }
}
