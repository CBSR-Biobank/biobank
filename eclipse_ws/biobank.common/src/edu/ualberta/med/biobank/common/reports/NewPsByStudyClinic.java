package edu.ualberta.med.biobank.common.reports;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class NewPsByStudyClinic extends QueryObject {

    protected static final String NAME = "New Patients per Study per Clinic by Date";

    protected static final String query = "select pv.patient.study.nameShort, pv.shipment.clinic.name, year(pv.dateProcessed), {2}(pv.dateProcessed), count(*) from edu.ualberta.med.biobank.model.PatientVisit pv where pv.dateProcessed=(select min(pvCollection.dateProcessed) from edu.ualberta.med.biobank.model.Patient p join p.patientVisitCollection as pvCollection where p=pv.patient) and pv.patient.study.site {0} {1,number,#} group by pv.patient.study.nameShort, pv.shipment.clinic.name, year(pv.dateProcessed), {2}(pv.dateProcessed)";

    public NewPsByStudyClinic(String op, Integer siteId) {
        super(
            "Displays the total number of patients added per study per clinic by date range.",
            MessageFormat.format(query, op, siteId, "{0}"), new String[] {
                "Study", "Clinic", "", "Total" });
        addOption("Date Range", DateRange.class, DateRange.Month);
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
        columnNames[2] = (String) params.get(0);
        queryString = MessageFormat.format(queryString, columnNames[2]);
        return params;
    }

    @Override
    public List<Object> postProcess(List<Object> results) {
        List<Object> compressedDates = new ArrayList<Object>();
        if (columnNames[2].compareTo("Year") == 0) {
            for (Object ob : results) {
                Object[] castOb = (Object[]) ob;
                compressedDates.add(new Object[] { castOb[0], castOb[1],
                    castOb[3], castOb[4] });
            }
        } else {
            for (Object ob : results) {
                Object[] castOb = (Object[]) ob;
                compressedDates.add(new Object[] { castOb[0], castOb[1],
                    castOb[3] + "(" + castOb[2] + ")", castOb[4] });
            }
        }
        return compressedDates;
    }

    @Override
    public String getName() {
        return NAME;
    }
}