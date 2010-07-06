package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.util.ReportOption;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;

public class AliquotCountImpl extends AbstractReport {

    private static final String QUERY = "Select Alias.sampleType.name, count(*) from "
        + Aliquot.class.getName()
        + " as Alias where Alias.aliquotPosition not in (from "
        + AliquotPosition.class.getName()
        + " a where a.container.label like '"
        + SENT_SAMPLES_FREEZER_NAME
        + "') and Alias.patientVisit.patient.study.site "
        + siteOperatorString
        + siteIdString + " GROUP BY Alias.sampleType.name";

    public AliquotCountImpl(List<Object> parameters, List<ReportOption> options) {
        super(QUERY, parameters, options);
    }
}
