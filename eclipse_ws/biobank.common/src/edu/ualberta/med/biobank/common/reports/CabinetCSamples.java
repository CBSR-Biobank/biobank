package edu.ualberta.med.biobank.common.reports;

import java.util.List;

import edu.ualberta.med.biobank.model.ContainerPath;
import edu.ualberta.med.biobank.model.Sample;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class CabinetCSamples extends QueryObject {

    public static final String NAME = "Cabinet Samples per Study per Clinic";

    public CabinetCSamples(String op, Integer siteId) {
        super(
            "Displays the total number of cabinet samples per study per clinic.",

            "select sample.patientVisit.patient.study.nameShort, sample.patientVisit.shipment.clinic.name, count(*) from "
                + Sample.class.getName()
                + " as sample where sample.samplePosition.container.id in (select path1.container.id from "
                + ContainerPath.class.getName()
                + " as path1, "
                + ContainerPath.class.getName()
                + " as path2 where locate(path2.path, path1.path) > 0 and path2.container.containerType.name like ?) and sample.patientVisit.patient.study.site"
                + op
                + siteId
                + " group by sample.patientVisit.patient.study.nameShort, sample.patientVisit.shipment.clinic.name",
            new String[] { "Study", "Clinic", "Total" });
    }

    @Override
    public List<Object> executeQuery(WritableApplicationService appService,
        List<Object> params) throws ApplicationException {
        params.add("%Cabinet%");
        HQLCriteria c = new HQLCriteria(queryString);
        c.setParameters(params);
        List<Object> results = appService.query(c);
        return postProcess(results);
    }

    @Override
    public String getName() {
        return NAME;
    }
}