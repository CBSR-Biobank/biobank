package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.util.ReportOption;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.PatientVisit;

/**
 * needs one parameters = study.nameShort
 */
public class SampleTypePvCountImpl extends AbstractReport {

    private static final String QUERY = "Select pv.patient.pnumber, pv.dateProcessed,"
        + " pv.dateDrawn,  Alias.sampleType.name, count(*) from "
        + PatientVisit.class.getName()
        + " as pv join pv.aliquotCollection as Alias where pv.patient.study.nameShort LIKE ? "
        + " and Alias.aliquotPosition not in (from "
        + AliquotPosition.class.getName()
        + " a where a.container.label like '"
        + SENT_SAMPLES_FREEZER_NAME
        + "') and Alias.patientVisit.patient.study.site "
        + siteOperatorString
        + siteIdString
        + " GROUP BY pv, Alias.sampleType ORDER BY pv.patient.pnumber, pv.dateProcessed";

    public SampleTypePvCountImpl(List<Object> parameters,
        List<ReportOption> options) {
        super(QUERY, parameters, options);
    }

}
