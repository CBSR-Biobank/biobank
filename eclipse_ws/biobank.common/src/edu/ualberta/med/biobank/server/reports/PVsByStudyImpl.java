package edu.ualberta.med.biobank.server.reports;

import java.text.MessageFormat;

import edu.ualberta.med.biobank.client.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;
import edu.ualberta.med.biobank.model.PatientVisit;

public class PVsByStudyImpl extends AbstractReport {

    private static final String QUERY = "Select Alias.patient.study.nameShort, "
        + " Year(Alias.dateProcessed), "
        + "{0}(Alias.dateProcessed), count(*) from "
        + PatientVisit.class.getName()
        + " as Alias where Alias.patient.study.site "
        + SITE_OPERATOR
        + SITE_ID
        + " GROUP BY Alias.patient.study.nameShort, "
        + "Year(Alias.dateProcessed), {0}(Alias.dateProcessed)";

    @SuppressWarnings("unused")
    private DateRangeRowPostProcess dateRangePostProcess;

    public PVsByStudyImpl(BiobankReport report) {
        super(QUERY, report);
        String groupBy = report.getStrings().get(0);
        queryString = MessageFormat.format(queryString, groupBy);
        dateRangePostProcess = new DateRangeRowPostProcess(
            groupBy.equals("Year"), 1);
    }

}