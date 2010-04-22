package edu.ualberta.med.biobank.common.reports;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class PsByStudy extends QueryObject {

    protected static final String NAME = "Patients per Study by Date";

    protected static final String query = "select pv.patient.study.nameShort, year(pv.dateProcessed), {2}(pv.dateProcessed), "
        + "count(distinct pv.patient) from edu.ualberta.med.biobank.model.PatientVisit pv where pv.patient.study.site {0} {1,number,#} group by pv.patient.study.nameShort, year(pv.dateProcessed), {2}(pv.dateProcessed)";

    public PsByStudy(String op, Integer siteId) {
        super(
            "Displays the total number of patients per study with at least one patient visit grouped by calendar week/month/quarter/year.",
            MessageFormat.format(query, op, siteId, "{0}"), new String[] {
                "Study", "", "Total" });
        addOption("Date Range", DateGroup.class, DateGroup.Month);
    }

    @Override
    public List<Object> preProcess(List<Object> params) {
        for (int i = 0; i < queryOptions.size(); i++) {
            Option option = queryOptions.get(i);
            if (params.get(i) == null)
                params.set(i, option.getDefaultValue());
            if (option.type.equals(String.class))
                params.set(i, "%" + params.get(i) + "%");
        }
        columnNames[1] = (String) params.remove(0);
        queryString = MessageFormat.format(queryString, columnNames[1]);
        return params;
    }

    @Override
    public List<Object> postProcess(List<Object> results) {
        List<Object> compressedDates = new ArrayList<Object>();
        if (columnNames[1].compareTo("Year") == 0) {
            for (Object ob : results) {
                Object[] castOb = (Object[]) ob;
                compressedDates.add(new Object[] { castOb[0], castOb[2],
                    castOb[3] });
            }
        } else {
            for (Object ob : results) {
                Object[] castOb = (Object[]) ob;
                compressedDates.add(new Object[] { castOb[0],
                    castOb[2] + "(" + castOb[1] + ")", castOb[3] });
            }
        }
        return compressedDates;
    }

    @Override
    public String getName() {
        return NAME;
    }
}