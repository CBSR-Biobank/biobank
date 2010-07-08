package edu.ualberta.med.biobank.server.reports;

import java.text.MessageFormat;
import java.util.List;

import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;
import edu.ualberta.med.biobank.common.util.ReportOption;

public class PsByStudyImpl extends AbstractReport {

    private static final String QUERY = "select pv.patient.study.nameShort,"
        + " year(pv.dateProcessed), {0}(pv.dateProcessed), "
        + "count(distinct pv.patient) from edu.ualberta.med.biobank.model.PatientVisit pv"
        + " where pv.patient.study.site "
        + SITE_OPERATOR
        + SITE_ID
        + " group by pv.patient.study.nameShort, year(pv.dateProcessed), {0}(pv.dateProcessed)";

    private DateRangeRowPostProcess dateRangePostProcess;

    public PsByStudyImpl(List<Object> parameters, List<ReportOption> options) {
        super(QUERY, parameters, options);
        for (int i = 0; i < options.size(); i++) {
            ReportOption option = options.get(i);
            if (parameters.get(i) == null)
                parameters.set(i, option.getDefaultValue());
            if (option.getType().equals(String.class))
                parameters.set(i, "%" + parameters.get(i) + "%");
        }
        String groupBy = (String) parameters.remove(0);
        queryString = MessageFormat.format(queryString, groupBy);
        dateRangePostProcess = new DateRangeRowPostProcess(
            groupBy.equals("Year"), 1);
    }

    @Override
    protected AbstractRowPostProcess getRowPostProcess() {
        return dateRangePostProcess;
    }

}