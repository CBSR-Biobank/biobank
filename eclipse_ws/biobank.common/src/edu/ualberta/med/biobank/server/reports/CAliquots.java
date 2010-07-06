package edu.ualberta.med.biobank.server.reports;

import java.text.MessageFormat;
import java.util.List;

import edu.ualberta.med.biobank.common.reports2.ReportOption;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.ContainerPath;

public class CAliquots extends AbstractReport {

    private static final String QUERY = "select aliquot.patientVisit.patient.study.nameShort, aliquot.patientVisit.shipment.clinic.name, count(*) from "
        + Aliquot.class.getName()
        + " as aliquot where aliquot.aliquotPosition.container.id in (select path1.container.id from "
        + ContainerPath.class.getName()
        + " as path1, "
        + ContainerPath.class.getName()
        + " as path2 where locate(path2.path, path1.path) > 0 and path2.container.containerType.name like {0}) and aliquot.patientVisit.patient.study.site"
        + siteOperatorString
        + siteIdString
        + " group by aliquot.patientVisit.patient.study.nameShort, aliquot.patientVisit.shipment.clinic.name";

    protected CAliquots(String containerName, List<Object> parameters,
        List<ReportOption> options) {
        super(MessageFormat.format(QUERY, containerName), parameters, options);
    }

}
