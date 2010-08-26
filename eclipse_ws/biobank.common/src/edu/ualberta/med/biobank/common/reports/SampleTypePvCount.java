package edu.ualberta.med.biobank.common.reports;


public class SampleTypePvCount extends QueryObject {

    protected static final String NAME = "Sample Type Totals by Patient Visit and Study";

    public SampleTypePvCount(String op, Integer siteId) {
        super(
            "Lists the total number of each sample type per patient visit for a specified study.",
            "Select pv.patient.pnumber, pv.dateProcessed, pv.dateDrawn,  Alias.sampleType.name, count(*) from "
                + PatientVisit.class.getName()
                + " as pv join pv.aliquotCollection as Alias where pv.patient.study.nameShort LIKE ? "
                + " and Alias.aliquotPosition.id not in (from "
                + AliquotPosition.class.getName()
                + " a where a.container.label like 'SS%') and Alias.patientVisit.patient.study.site "
                + op
                + siteId
                + " GROUP BY pv, Alias.sampleType ORDER BY pv.patient.pnumber, pv.dateProcessed",
            new String[] { "Patient Number", "Date Processed", "Date Drawn",
                "Sample Type", "Total" });
        addOption("Study", String.class, "");
    }

    @Override
    public String getName() {
        return NAME;
    }
}
