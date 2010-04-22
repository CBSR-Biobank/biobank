package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.PatientVisit;

public class SampleTypePvCount extends QueryObject {

    protected static final String NAME = "Sample Type Totals by Patient Visit and Study";

    public SampleTypePvCount(String op, Integer siteId) {
        super(
            "Lists the total number of each sample type per patient visit for a specified study.",
            "Select pv.patient.pnumber, pv.dateProcessed, pv.dateDrawn,  Alias.sampleType.name, count(*) from "
                + PatientVisit.class.getName()
                + " as pv join pv.aliquotCollection as Alias where pv.patient.study.nameShort = ? "
                + " and Alias.aliquotPosition.container.label not like 'SS%' and Alias.patientVisit.patient.study.site "
                + op
                + siteId
                + " GROUP BY pv, Alias.sampleType ORDER BY pv.patient.pnumber, pv.dateDrawn",
            new String[] { "Patient Number", "Date Drawn", "Date Processed",
                "Sample Type", "Total" });
        addOption("Study", String.class, "");
    }

    @Override
    public String getName() {
        return NAME;
    }
}
