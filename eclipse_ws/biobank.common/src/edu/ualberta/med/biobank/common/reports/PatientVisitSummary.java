package edu.ualberta.med.biobank.common.reports;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientVisitSummary extends QueryObject {
    private static String PVCOUNT_STRING = "(select count(p.id) from edu.ualberta.med.biobank.model.Patient p where p.study.site.id {0} {01} and p.patientVisitCollection.dateProcessed >= ? and p.patientVisitCollection.dateProcessed <= ? group by p.id having size(p.patientVisitCollection) {0} {1})";

    private static String QUERY_STRING = "select pv.patient.study.nameShort, pv.shipment.clinic.name, "
        + MessageFormat.format(PVCOUNT_STRING, "=", "1")
        + ", "
        + MessageFormat.format(PVCOUNT_STRING, "=", "2")
        + ", "
        + MessageFormat.format(PVCOUNT_STRING, "=", "3")
        + ", "
        + MessageFormat.format(PVCOUNT_STRING, "=", "4")
        + ", "
        + MessageFormat.format(PVCOUNT_STRING, ">=", "5")
        + ", "
        + "count(pv.id), count(distinct pv.patient.id) from edu.ualberta.med.biobank.model.PatientVisit"
        + " pv where pv.patient.study.site.id {1} {0} and pv.dateProcessed >= ? and pv.dateProcessed <= ? group by pv.patient.study.nameShort, pv.shipment.clinic.name";

    public PatientVisitSummary(String op, Integer siteId) {
        super(
            "Displays the total number of patients for each of 1-5+ visits, the total number of visits, and the total number of patients per study per clinic for a given date range.",
            MessageFormat.format(QUERY_STRING, siteId, op), new String[] {
                "Study", "Clinic", "1 Visit", "2 Visit", "3 Visit", "4 Visit",
                "5+ Visits", "Total Visits", "Total Patients" });
        addOption("Start Date", Date.class, new Date(0));
        addOption("End Date", Date.class, new Date());
    }

    @Override
    public List<Object> executeQuery(WritableApplicationService appService,
        List<Object> params) throws ApplicationException {
        for (int i = 0; i < queryOptions.size(); i++) {
            Option option = queryOptions.get(i);
            if (params.get(i) == null)
                params.set(i, option.getDefaultValue());
        }
        int size = params.size();
        for (int j = 0; j < 0; j++) {
            for (int i = 0; i < size; i++) {
                params.add(params.get(i));
            }
        }
        HQLCriteria c = new HQLCriteria(queryString);
        c.setParameters(params);
        List<Object> results = appService.query(c);
        return postProcess(results);
    }

    @Override
    public List<Object> postProcess(List<Object> results) {
        if (results.size() == 0)
            return results;
        List<Object> totalledResults = new ArrayList<Object>();
        String lastStudy = (String) ((Object[]) results.get(0))[0];
        int numSums = 7;
        Long[] sums = new Long[numSums];
        for (int i = 0; i < numSums; i++)
            sums[i] = new Long(0);
        for (Object obj : results) {
            Object[] castObj = (Object[]) obj;
            if (lastStudy.compareTo((String) castObj[0]) != 0) {
                totalledResults.add(new Object[] { lastStudy, "", sums[0],
                    sums[1], sums[2], sums[3], sums[4], sums[5], sums[6] });
                for (int i = 0; i < numSums; i++)
                    sums[i] = new Long(0);
            }
            for (int i = 0; i < numSums; i++)
                sums[i] += (Long) castObj[i + 2];
            totalledResults.add(obj);
            lastStudy = (String) castObj[0];
        }
        totalledResults.add(new Object[] { lastStudy, "", sums[0], sums[1],
            sums[2], sums[3], sums[4], sums[5], sums[6] });
        return totalledResults;
    }
}
