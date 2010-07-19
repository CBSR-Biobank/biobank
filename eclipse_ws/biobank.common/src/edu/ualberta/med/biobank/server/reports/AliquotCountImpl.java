package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;

public class AliquotCountImpl extends AbstractReport {

    private static final String QUERY = "Select Alias.sampleType.name, count(*) from "
        + Aliquot.class.getName()
        + " as Alias where Alias.aliquotPosition not in (from "
        + AliquotPosition.class.getName()
        + " a where a.container.label like 'SS%') and Alias.linkDate between ? and ? and Alias.patientVisit.patient.study.site "
        + SITE_OPERATOR + SITE_ID + " GROUP BY Alias.sampleType.name";

    public AliquotCountImpl(BiobankReport report) {
        super(QUERY, report);
    }
}
