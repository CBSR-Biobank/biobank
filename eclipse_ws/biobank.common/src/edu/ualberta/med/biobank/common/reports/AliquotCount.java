package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Aliquot;

public class AliquotCount extends QueryObject {

    protected static final String NAME = "Aliquot Type Totals";

    public AliquotCount(String op, Integer siteId) {
        super("Lists the total number of aliquots per sample type.",
            "Select Alias.sampleType.name, count(*) from "
                + Aliquot.class.getName()
                + " as Alias where Alias.patientVisit.patient.study.site " + op
                + siteId + " GROUP BY Alias.sampleType.name", new String[] {
                "Sample Type", "Total" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
