package edu.ualberta.med.biobank.common.reports;

import java.util.Date;

import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;

public class AliquotCount extends QueryObject {

    protected static final String NAME = "Sample Type Totals";

    public AliquotCount(String op, Integer siteId) {
        super(
            "Lists the total number of aliquots per sample type, within a given date range.",
            "Select Alias.sampleType.name, count(*) from "
                + Aliquot.class.getName()
                + " as Alias where Alias.aliquotPosition.id not in (from "
                + AliquotPosition.class.getName()
                + " a where a.container.label like 'SS%') and Alias.linkDate between ? and ? and Alias.patientVisit.patient.study.site "
                + op + siteId + " GROUP BY Alias.sampleType.name",
            new String[] { "Sample Type", "Total" });
        addOption("Start Date (Linked)", Date.class, new Date(0));
        addOption("End Date (Linked)", Date.class, new Date());
    }

    @Override
    public String getName() {
        return NAME;
    }
}
