package edu.ualberta.med.biobank.server.reports;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.reports2.ReportOption;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PsByStudyImpl extends AbstractReport {

    private static final String QUERY = "select pv.patient.study.nameShort,"
        + " year(pv.dateProcessed), {0}(pv.dateProcessed), "
        + "count(distinct pv.patient) from edu.ualberta.med.biobank.model.PatientVisit pv"
        + " where pv.patient.study.site "
        + siteOperatorString
        + siteIdString
        + " group by pv.patient.study.nameShort, year(pv.dateProcessed), {0}(pv.dateProcessed)";

    private boolean groupByYear;

    public PsByStudyImpl(List<Object> parameters, List<ReportOption> options) {
        super(QUERY, parameters, options);
        for (int i = 0; i < options.size(); i++) {
            ReportOption option = options.get(i);
            if (parameters.get(i) == null)
                parameters.set(i, option.getDefaultValue());
            if (option.getType().equals(String.class))
                parameters.set(i, "%" + parameters.get(i) + "%");
        }
        // FIXME modify column in client side
        String groupBy = (String) parameters.remove(0);
        queryString = MessageFormat.format(queryString, groupBy);
        groupByYear = groupBy.equals("Year");
    }

    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        List<Object> compressedDates = new ArrayList<Object>();
        if (groupByYear) {
            for (Object ob : results) {
                Object[] castOb = (Object[]) ob;
                compressedDates.add(new Object[] { castOb[0], castOb[2],
                    castOb[3] });
            }
        } else {
            // FIXME need BiobankListProxy
            for (Object ob : results) {
                Object[] castOb = (Object[]) ob;
                compressedDates.add(new Object[] { castOb[0],
                    castOb[2] + "-" + castOb[1], castOb[3] });
            }
        }
        return compressedDates;
    }

}