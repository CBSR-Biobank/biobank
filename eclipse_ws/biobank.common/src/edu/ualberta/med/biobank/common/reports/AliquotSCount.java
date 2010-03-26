package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Aliquot;

public class AliquotSCount extends QueryObject {

    protected static final String NAME = "Aliquot Types by Study";

    public AliquotSCount(String op, Integer siteId) {
        super(
            "Lists the total number of each aliquot sample type by study.",
            "Select Alias.patientVisit.patient.study.nameShort, Alias.sampleType.name, count(*) from "
                + Aliquot.class.getName()
                + " as Alias where Alias.patientVisit.patient.study.site "
                + op
                + siteId
                + " GROUP BY Alias.patientVisit.patient.study.nameShort, Alias.sampleType.name",
            new String[] { "Study", "Sample Type", "Total" });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
