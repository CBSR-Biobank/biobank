package edu.ualberta.med.biobank.common.reports;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.model.PatientVisit;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class NewPVsByStudyClinicMonth extends QueryObject {

    public NewPVsByStudyClinicMonth(String op, Integer siteId) {
        super(
            "Displays the total number of patient visits added per study per clinic by month.",
            "Select Alias.dateProcessed, Alias.patient.study.name, Alias.shipment.clinic.name from "
                + PatientVisit.class.getName()
                + " as Alias where Alias.patient.study.site "
                + op
                + siteId
                + "ORDER BY Alias.patient.study.name, Alias.shipment.clinic.name, Alias.dateProcessed ASC",
            new String[] { "", "Study", "Clinic", "Total" });
        addOption("Date Range", DateRange.class, DateRange.Week);
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
        columnNames[0] = (String) params.get(0);
        HQLCriteria c = new HQLCriteria(queryString);

        List<Object> results = appService.query(c);
        return postProcess(results);
    }

    @Override
    public List<Object> postProcess(List<Object> results) {
        if (results.size() == 0)
            return results;
        List<Object> totalledResults = new ArrayList<Object>();

        int upperBound = 1, lowerBound = 0;
        // weakly typed, consider changing to enum with method to protect this
        // variable from being mis-set.. but might not be a big deal
        int CALENDAR_TYPE;
        int incrementBy;

        if (columnNames[0].compareTo("Week") == 0) {
            CALENDAR_TYPE = Calendar.WEEK_OF_YEAR;
            incrementBy = 1;
        } else if (columnNames[0].compareTo("Month") == 0) {
            CALENDAR_TYPE = Calendar.MONTH;
            incrementBy = 1;
        } else if (columnNames[0].compareTo("Quarter") == 0) {
            CALENDAR_TYPE = Calendar.MONTH;
            incrementBy = 4;
        } else {
            CALENDAR_TYPE = Calendar.YEAR;
            incrementBy = 1;
        }

        String lastStudy = (String) ((Object[]) results.get(0))[1], lastClinic = (String) ((Object[]) results
            .get(0))[2];
        for (Object obj : results) {
            Object[] castObj = (Object[]) obj;
            if (((String) castObj[1]).compareTo(lastStudy) != 0
                || ((String) castObj[2]).compareTo(lastClinic) != 0) {
                totalledResults.addAll(sumSection(results.subList(lowerBound,
                    upperBound), CALENDAR_TYPE, incrementBy, lastStudy,
                    lastClinic));
                lastStudy = (String) ((Object[]) results.get(0))[1];
                lastClinic = (String) ((Object[]) results.get(0))[2];
                lowerBound = upperBound;
            }
            upperBound++;
        }
        totalledResults
            .addAll(sumSection(results.subList(lowerBound, upperBound - 1),
                CALENDAR_TYPE, incrementBy, lastStudy, lastClinic));
        return totalledResults;
    }

    private List<Object> sumSection(List<Object> results, int CALENDAR_TYPE,
        int incrementBy, String study, String clinic) {
        int count = 0, grpNumber = 1;
        List<Object> totalledResults = new ArrayList<Object>();
        Calendar start = Calendar.getInstance();
        start.setTime((Date) ((Object[]) results.get(0))[0]);
        Calendar end = Calendar.getInstance();
        end.setTime(start.getTime());
        end.add(CALENDAR_TYPE, incrementBy);
        Calendar date = Calendar.getInstance();
        for (Object obj : results) {
            Object[] castObj = (Object[]) obj;
            date.setTime((Date) castObj[0]);
            if (date.compareTo(start) >= 0 && date.compareTo(end) <= 0) {
                count++;
            } else {
                totalledResults.add(new Object[] { grpNumber, study, clinic,
                    count });
                count = 1;
                while (date.after(end)) {
                    start.add(CALENDAR_TYPE, incrementBy);
                    end.add(CALENDAR_TYPE, incrementBy);
                    grpNumber++;
                }
            }
        }
        totalledResults.add(new Object[] { grpNumber, study, clinic, count });
        return totalledResults;
    }
}