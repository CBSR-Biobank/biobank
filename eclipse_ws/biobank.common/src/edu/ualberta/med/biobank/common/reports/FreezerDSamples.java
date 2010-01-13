package edu.ualberta.med.biobank.common.reports;

import java.text.MessageFormat;
import java.util.List;

import edu.ualberta.med.biobank.model.Sample;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class FreezerDSamples extends QueryObject {

    public FreezerDSamples(String op, Integer siteId) {
        super(
            "Displays the total number of freezer samples per study per clinic by date range.",
            "Select Alias.{0}, Alias.patientVisit.patient.study.name, Alias.patientVisit.shipment.clinic.name, count (*) from "
                + Sample.class.getName()
                + " as Alias where Alias.patientVisit.patient.study.site "
                + op
                + siteId
                + " GROUP BY Alias.{0}, Alias.patientVisit.patient.study, Alias.patientVisit.shipment.clinic, count(*)",
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
        queryString = MessageFormat.format(queryString, params.get(0));
        columnNames[0] = (String) params.get(0);
        HQLCriteria c = new HQLCriteria(queryString);

        List<Object> results = appService.query(c);
        return postProcess(results);
    }

}