package edu.ualberta.med.biobank.common.reports;

import java.text.MessageFormat;
import java.util.List;

import edu.ualberta.med.biobank.model.PatientVisit;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class NewPsByStudyClinic extends QueryObject {

    public static String sub_query = "(select min(sub_query.dateProcessed) from "
        + PatientVisit.class.getName()
        + " as sub_query where sub_query.patient.study.site {0} {1} and sub_query.patient.id=Alias.patient.id)";

    public NewPsByStudyClinic(String op, Integer siteId) {
        super(
            "Displays the total number of patients added per study per clinic by date range.",
            "select "
                + MessageFormat.format(sub_query, op, siteId)
                + ", Alias.patient.study.nameShort, Alias.shipment.clinic.name from "
                + PatientVisit.class.getName()
                + " as Alias where Alias.patient.study.site "
                + op
                + " "
                + siteId
                + " ORDER BY Alias.patient.study.nameShort, Alias.shipment.clinic.name",
            new String[] { "", "Study", "Clinic", "Total" });
        addOption("Date Range", DateRange.class, DateRange.Month);
    }

    @Override
    public List<Object> executeQuery(WritableApplicationService appService,
        List<Object> params) throws ApplicationException {

        for (int i = 0; i < queryOptions.size(); i++) {
            Option option = queryOptions.get(i);
            if (params.get(i) == null)
                params.set(i, option.getDefaultValue());
            if (option.type.equals(String.class))
                params.set(i, "%" + params.get(i) + "%");
        }
        columnNames[0] = (String) params.get(0);
        HQLCriteria c = new HQLCriteria(queryString);
        System.out.println(queryString);
        List<Object> results = appService.query(c);
        return postProcess(results);
    }

    @Override
    public List<Object> postProcess(List<Object> results) {
        return sumByDate(results);
    }
}