package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;

public class PsByStudyImpl extends AbstractReport {

    private static final String QUERY = "select pv.patient.study.nameShort,"
        + " year(pv.dateProcessed), "
        + GROUPBY_DATE
        + "(pv.dateProcessed), "
        + "count(distinct pv.patient) from edu.ualberta.med.biobank.model.PatientVisit pv"
        + " where pv.dateProcessed between ? and ? and pv.patient.study.site "
        + SITE_OPERATOR + SITE_ID
        + " group by pv.patient.study.nameShort, year(pv.dateProcessed), "
        + GROUPBY_DATE + "(pv.dateProcessed)";

    private DateRangeRowPostProcess dateRangePostProcess;

    public PsByStudyImpl(BiobankReport report) {
        super(QUERY, report);
        dateRangePostProcess = new DateRangeRowPostProcess(report.getGroupBy()
            .equals("Year"), 1);
    }

    @Override
    protected AbstractRowPostProcess getRowPostProcess() {
        return dateRangePostProcess;
    }

}