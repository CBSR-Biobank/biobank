package edu.ualberta.med.biobank.common.reports;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.model.PatientVisit;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class NewPVsByStudyClinic extends QueryObject {

    protected static final String NAME = "New Patient Visits per Study per Clinic by Date";

    protected static final String query = "Select Alias.patient.study.nameShort, "
        + "Alias.shipment.clinic.name, Year(Alias.dateProcessed), "
        + "{2}(Alias.dateProcessed), count(*) from "
        + PatientVisit.class.getName()
        + " as Alias where Alias.patient.study.site {0} {1,number,#}"
        + " GROUP BY Alias.patient.study.nameShort, Alias.shipment.clinic.name, "
        + "Year(Alias.dateProcessed), {2}(Alias.dateProcessed)";

    public NewPVsByStudyClinic(String op, Integer siteId) {
        super(
            "Displays the total number of patient visits added per study per "
                + "clinic by date range.", MessageFormat.format(query, op,
                siteId, "{0}"), new String[] { "Study", "Clinic", "", "Total" });
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
        columnNames[2] = (String) params.get(0);
        queryString = MessageFormat.format(queryString, columnNames[2]);
        HQLCriteria c = new HQLCriteria(queryString);
        return appService.query(c);
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