package edu.ualberta.med.biobank.common.reports;

import java.util.List;

import edu.ualberta.med.biobank.model.ContainerPath;
import edu.ualberta.med.biobank.model.Sample;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class FreezerSSamples extends QueryObject {

    protected static final String NAME = "Freezer Samples per Study";

    public FreezerSSamples(String op, Integer siteId) {
        super(
            "Displays the total number of freezer samples per study.",

            "select sample.patientVisit.patient.study.nameShort, count(*) from "
                + Sample.class.getName()
                + " as sample where sample.samplePosition.container.id in (select path1.container.id from "
                + ContainerPath.class.getName()
                + " as path1, "
                + ContainerPath.class.getName()
                + " as path2 where locate(path2.path, path1.path) > 0 and path2.container.containerType.name like ?) and sample.patientVisit.patient.study.site"
                + op + siteId
                + " group by sample.patientVisit.patient.study.nameShort",
            new String[] { "Study", "Total" });
    }

    @Override
    public List<Object> executeQuery(WritableApplicationService appService,
        List<Object> params) throws ApplicationException {
        params.add("%Freezer%");
        HQLCriteria c = new HQLCriteria(queryString);
        c.setParameters(params);
        List<Object> results = appService.query(c);
        return postProcess(results);
    }
}