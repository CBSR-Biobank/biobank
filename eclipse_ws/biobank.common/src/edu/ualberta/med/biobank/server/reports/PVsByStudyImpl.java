package edu.ualberta.med.biobank.server.reports;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.reports2.ReportOption;
import edu.ualberta.med.biobank.model.PatientVisit;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PVsByStudyImpl extends AbstractReport {

    private static final String QUERY = "Select Alias.patient.study.nameShort, "
        + " Year(Alias.dateProcessed), "
        + "{0}(Alias.dateProcessed), count(*) from "
        + PatientVisit.class.getName()
        + " as Alias where Alias.patient.study.site "
        + siteOperatorString
        + siteIdString
        + " GROUP BY Alias.patient.study.nameShort, "
        + "Year(Alias.dateProcessed), {0}(Alias.dateProcessed)";

    private boolean groupByYear;

    public PVsByStudyImpl(List<Object> parameters, List<ReportOption> options) {
        super(QUERY, parameters, options);
        for (int i = 0; i < options.size(); i++) {
            ReportOption option = options.get(i);
            if (parameters.get(i) == null)
                parameters.set(i, option.getDefaultValue());
            if (option.getType().equals(String.class))
                parameters.set(i, "%" + parameters.get(i) + "%");
        }
        // FIXME set column on client side
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
            // FIXME needs BiobankListProxy
            for (Object ob : results) {
                Object[] castOb = (Object[]) ob;
                compressedDates.add(new Object[] { castOb[0],
                    castOb[2] + "-" + castOb[1], castOb[3] });
            }
        }
        return compressedDates;
    }

}