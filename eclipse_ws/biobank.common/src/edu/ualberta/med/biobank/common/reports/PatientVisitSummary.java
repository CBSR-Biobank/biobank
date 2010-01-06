package edu.ualberta.med.biobank.common.reports;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientVisitSummary extends QueryObject {
    private static String PVCOUNT_STRING = "(select count(*) from "
        + Patient.class.getName()
        + " as p where (select count(*) from "
        + PatientVisit.class.getName()
        + " as pv where pv.patient = p and pv.shipment.clinic.id = c.clinic.id and p.study.site.id ={2} and pv.dateProcessed >= ? and pv.dateProcessed <= ?) {0} {1})";

    private static String QUERY_STRING = "select s.name, c.clinic.name, "
        + MessageFormat.format(PVCOUNT_STRING, "=", "1", "{0}")
        + ", "
        + MessageFormat.format(PVCOUNT_STRING, "=", "2", "{0}")
        + ", "
        + MessageFormat.format(PVCOUNT_STRING, "=", "3", "{0}")
        + ", "
        + MessageFormat.format(PVCOUNT_STRING, "=", "4", "{0}")
        + ", "
        + MessageFormat.format(PVCOUNT_STRING, ">=", "5", "{0}")
        + ", "
        + "(select count(*) from "
        + PatientVisit.class.getName()
        + " as pvtotal where pvtotal.shipment.clinic.id=c.clinic.id and pvtotal.patient.study.id = s.id and pvtotal.patient.study.site.id ={0} and pvtotal.dateProcessed >= ? and pvtotal.dateProcessed <= ?), "
        + "(select count(distinct patients.patient.id) from "
        + PatientVisit.class.getName()
        + " as patients where patients.shipment.clinic.id=c.clinic.id and patients.patient.study.id = s.id and patients.patient.study.site.id ={0} and patients.dateProcessed >= ? and patients.dateProcessed <= ?)"
        + " from " + Study.class.getName()
        + " as s inner join s.contactCollection as c where s.site.id = {0}";

    public PatientVisitSummary(String name, Integer siteId) {
        super(
            "Displays the total number of patients for each of 1-5+ visits, the total number of visits, and the total number of patients per study per clinic for a given date range.",
            name, MessageFormat.format(QUERY_STRING, siteId), new String[] {
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
                params.set(i, option.defaultValue);
            if (option.type.equals(String.class))
                params.set(i, "%" + params.get(i) + "%");
        }
        int size = params.size();
        for (int j = 0; j < 6; j++) {
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
        }
        totalledResults.add(new Object[] { lastStudy, "", sums[0], sums[1],
            sums[2], sums[3], sums[4], sums[5], sums[6] });
        return totalledResults;
    }
}
