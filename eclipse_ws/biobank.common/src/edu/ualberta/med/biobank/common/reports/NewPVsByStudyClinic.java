package edu.ualberta.med.biobank.common.reports;

import java.util.List;

import edu.ualberta.med.biobank.model.PatientVisit;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class NewPVsByStudyClinic extends QueryObject {

    public NewPVsByStudyClinic(String op, Integer siteId) {
        super(
            "Displays the total number of patient visits added per study per clinic by date range.",
            "Select Alias.dateProcessed, Alias.patient.study.nameShort, Alias.shipment.clinic.name from "
                + PatientVisit.class.getName()
                + " as Alias where Alias.patient.study.site "
                + op
                + siteId
                + " ORDER BY Alias.patient.study.nameShort, Alias.shipment.clinic.name, Alias.dateProcessed ASC",
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

        List<Object> results = appService.query(c);
        return postProcess(results);
    }

    @Override
    public List<Object> postProcess(List<Object> results) {
        return sumByDate(results);
    }

}