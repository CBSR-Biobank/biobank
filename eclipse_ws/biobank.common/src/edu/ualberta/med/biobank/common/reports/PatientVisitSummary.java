package edu.ualberta.med.biobank.common.reports;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PatientVisitSummary extends QueryObject {

    protected static final String NAME = "Patient Visit Statistics by Study and Clinic";

    private static String PVCOUNT_STRING = "(select count(p.id) from "
        + Patient.class.getName()
        + " as p where (select count(pv.id) from "
        + PatientVisit.class.getName()
        + " as pv where pv.patient = p and pv.shipment.clinic = c.clinic and s=p.study and pv.dateProcessed between ? and ?) {0} {1})";

    private static String QUERY_STRING = "select s.nameShort, c.clinic.name, "
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
        + "(select count(pvtotal.id) from "
        + PatientVisit.class.getName()
        + " as pvtotal where pvtotal.shipment.clinic=c.clinic and pvtotal.patient.study=s and pvtotal.dateProcessed between ? and ?), "
        + "(select count(distinct patients.patient.id) from "
        + PatientVisit.class.getName()
        + " as patients where patients.shipment.clinic=c.clinic and patients.patient.study=s and patients.dateProcessed between ? and ?)"
        + " from "
        + Study.class.getName()
        + " as s inner join s.contactCollection as c where s.site.id {1} {0,number,#} ORDER BY s.nameShort";

    public PatientVisitSummary(String op, Integer siteId) {
        super(
            "Displays the total number of patients for each of 1-5+ visits, the total number of visits, and the total number of patients per study per clinic for a given date range.",
            MessageFormat.format(QUERY_STRING, siteId, op), new String[] {
                "Study", "Clinic", "1 Visit", "2 Visit", "3 Visit", "4 Visit",
                "5+ Visits", "Total Visits", "Total Patients" });
        addOption("Start Date (Processed)", Date.class, new Date(0));
        addOption("End Date (Processed)", Date.class, new Date());
    }

    @Override
    public List<Object> preProcess(List<Object> params) {
        for (int i = 0; i < queryOptions.size(); i++) {
            Option option = queryOptions.get(i);
            if (params.get(i) == null)
                params.set(i, option.getDefaultValue());
        }
        int size = params.size();
        for (int j = 0; j < 6; j++) {
            for (int i = 0; i < size; i++) {
                params.add(params.get(i));
            }
        }
        return params;
    }

    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        if (results.get(0) == null)
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
                totalledResults.add(new Object[] { lastStudy, "All Clinics",
                    sums[0], sums[1], sums[2], sums[3], sums[4], sums[5],
                    sums[6] });
                totalledResults.add(new Object[] { "", "", "", "", "", "", "",
                    "", "" });
                for (int i = 0; i < numSums; i++)
                    sums[i] = new Long(0);
            }
            for (int i = 0; i < numSums; i++)
                sums[i] += (Long) castObj[i + 2];
            totalledResults.add(obj);
            lastStudy = (String) castObj[0];
        }
        totalledResults.add(new Object[] { lastStudy, "All Clinics", sums[0],
            sums[1], sums[2], sums[3], sums[4], sums[5], sums[6] });
        return totalledResults;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
