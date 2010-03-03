package edu.ualberta.med.biobank.common.reports;

import java.util.List;

import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.ContainerPath;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class FreezerDSamples extends QueryObject {

    protected static final String NAME = "Freezer Aliquots per Study per Clinic by Date";

    public FreezerDSamples(String op, Integer siteId) {
        super(
            "Displays the total number of freezer aliquots per study per clinic by date range.",
            "select aliquot.linkDate, aliquot.patientVisit.patient.study.nameShort, "
                + "aliquot.patientVisit.shipment.clinic.name from "
                + Aliquot.class.getName()
                + " as aliquot where aliquot.samplePosition.container.id "
                + "in (select path1.container.id from "
                + ContainerPath.class.getName() + " as path1, "
                + ContainerPath.class.getName()
                + " as path2 where locate(path2.path, path1.path) > 0 "
                + "and path2.container.containerType.name like ?) "
                + "and aliquot.patientVisit.patient.study.site" + op + siteId
                + " order by aliquot.patientVisit.patient.study.nameShort, "
                + "aliquot.patientVisit.shipment.clinic.name, aliquot.linkDate",
            new String[] { "", "Study", "Clinic", "Total" }, new int[] { 100,
                200, 100, 100 });
        addOption("Date Range", DateRange.class, DateRange.Week);
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
        params.set(0, "%Freezer%");
        HQLCriteria c = new HQLCriteria(queryString);
        c.setParameters(params);
        List<Object> results = appService.query(c);
        return postProcess(results);
    }

    @Override
    public List<Object> postProcess(List<Object> results) {
        return sumByDate(results);
    }

    @Override
    public String getName() {
        return NAME;
    }
}