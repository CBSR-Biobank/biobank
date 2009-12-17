package edu.ualberta.med.biobank.common.reports;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.model.PatientVisit;

public class NewPatientCountQueryObject extends QueryObject {
    public NewPatientCountQueryObject(String name, Integer siteId) {
        super(
            "Lists the total number of new patients.",
            name,
            "Select "
                + name
                + "Alias.patient.study.name, "
                + name
                + "Alias.clinic.name, count(select patientVisit.patient.id, count(*) from "
                + PatientVisit.class.getName()
                + " as patientVisit where patientVisit.clinic.id="
                + name
                + "Alias.clinic.id and patientVisit.study.id="
                + name
                + "Alias.patient.study.id"
                + " group by patientVisit.patient.id having count(*) >=5), Count(*), Count("
                + name + "Alias.patient) from " + PatientVisit.class.getName()
                + " as " + name + "Alias " +

                "where " + name + "Alias.patient.study.site = " +

                siteId + " GROUP BY " + name + "Alias.patient.study, " + name
                + "Alias.clinic", new String[] { "Study", "Clinic", "1 Visit",
                "2 Visits", "3 Visits", "4 Visits", "5 Visits", "Total Visits",
                "Total Patients" });
    }

    @Override
    public List<Object> postProcess(List<Object> results) {
        if (results.size() == 0)
            return results;
        List<Object> totalledResults = new ArrayList<Object>();
        String lastStudy = (String) ((Object[]) results.get(0))[0];
        Long[] sums = new Long[5];
        for (int i = 0; i < 5; i++)
            sums[i] = new Long(0);
        for (Object obj : results) {
            Object[] castObj = (Object[]) obj;
            if (lastStudy.compareTo((String) castObj[0]) != 0) {
                totalledResults.add(new Object[] { lastStudy, "", sums[0],
                    sums[1], sums[2], sums[3], sums[4] });
                for (int i = 0; i < 5; i++)
                    sums[i] = new Long(0);
            }
            for (int i = 0; i < 5; i++)
                sums[i] += (Long) castObj[i + 2];
            totalledResults.add(obj);
        }
        totalledResults.add(new Object[] { lastStudy, "", sums[0], sums[1],
            sums[2], sums[3], sums[4] });
        return totalledResults;
    }
}
