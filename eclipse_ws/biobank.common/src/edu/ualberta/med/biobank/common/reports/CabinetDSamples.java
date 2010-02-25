package edu.ualberta.med.biobank.common.reports;

import java.util.List;

import edu.ualberta.med.biobank.model.ContainerPath;
import edu.ualberta.med.biobank.model.Sample;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class CabinetDSamples extends QueryObject {

    protected static final String NAME = "Cabinet Samples per Study per Clinic by Date";

    public CabinetDSamples(String op, Integer siteId) {
        super(
            "Displays the total number of cabinet samples per study per clinic by date range.",
            "select sample.linkDate, sample.patientVisit.patient.study.nameShort, sample.patientVisit.shipment.clinic.name from "
                + Sample.class.getName()
                + " as sample where sample.samplePosition.container.id in (select path1.container.id from "
                + ContainerPath.class.getName()
                + " as path1, "
                + ContainerPath.class.getName()
                + " as path2 where locate(path2.path, path1.path) > 0 and path2.container.containerType.name like ?) and sample.patientVisit.patient.study.site"
                + op
                + siteId
                + " order by sample.patientVisit.patient.study.nameShort, sample.patientVisit.shipment.clinic.name, sample.linkDate",
            new String[] { "", "Study", "Clinic", "Total" });
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
        params.set(0, "%Cabinet%");
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