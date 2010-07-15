package edu.ualberta.med.biobank.server.reports;

import java.text.MessageFormat;

import edu.ualberta.med.biobank.client.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;

public class PsByStudyImpl extends AbstractReport {

    private static final String QUERY = "select pv.patient.study.nameShort,"
        + " year(pv.dateProcessed), {0}(pv.dateProcessed), "
        + "count(distinct pv.patient) from edu.ualberta.med.biobank.model.PatientVisit pv"
        + " where pv.patient.study.site "
        + SITE_OPERATOR
        + SITE_ID
        + " group by pv.patient.study.nameShort, year(pv.dateProcessed), {0}(pv.dateProcessed)";

    private DateRangeRowPostProcess dateRangePostProcess;

    public PsByStudyImpl(BiobankReport report) {
        super(QUERY, report);
        String groupBy = report.getStrings().get(0);
        queryString = MessageFormat.format(queryString, groupBy);
        dateRangePostProcess = new DateRangeRowPostProcess(
            groupBy.equals("Year"), 1);
    }

    @Override
    protected AbstractRowPostProcess getRowPostProcess() {
        return dateRangePostProcess;
    }

}