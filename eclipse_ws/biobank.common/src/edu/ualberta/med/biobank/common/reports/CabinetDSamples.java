package edu.ualberta.med.biobank.common.reports;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.ContainerPath;

public class CabinetDSamples extends QueryObject {

    protected static final String NAME = "Cabinet Aliquots per Study per Clinic by Date";

    protected static final String query = "select aliquot.patientVisit.patient.study.nameShort, aliquot.patientVisit.shipment.clinic.name, year(aliquot.linkDate), {2}(aliquot.linkDate), count(aliquot.linkDate) from "
        + Aliquot.class.getName()
        + " as aliquot where aliquot.aliquotPosition.container.id in (select path1.container.id from "
        + ContainerPath.class.getName()
        + " as path1, "
        + ContainerPath.class.getName()
        + " as path2 where locate(path2.path, path1.path) > 0 and path2.container.containerType.name like ?) and aliquot.patientVisit.patient.study.site {0} {1,number,#}"
        + " group by aliquot.patientVisit.patient.study.nameShort, aliquot.patientVisit.shipment.clinic.name, year(aliquot.linkDate), {2}(aliquot.linkDate)";

    public CabinetDSamples(String op, Integer siteId) {
        super(
            "Displays the total number of cabinet samples per study per clinic by date range.",
            MessageFormat.format(query, op, siteId, "{0}"), new String[] {
                "Study", "Clinic", "", "Total" });
        addOption("Date Range", DateRange.class, DateRange.Week);
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
        params.set(0, "%Cabinet%");
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