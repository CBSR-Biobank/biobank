package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.ContainerPath;

public class SAliquotsImpl extends AbstractReport {

    private static final String QUERY =
        "select aliquot.patientVisit.clinicShipmentPatient.patient.study.nameShort, count(*) from "
            + Aliquot.class.getName()
            + " as aliquot where aliquot.aliquotPosition.container.id in (select path1.container.id from "
            + ContainerPath.class.getName()
            + " as path1, "
            + ContainerPath.class.getName()
            + " as path2 where locate(path2.path, path1.path) > 0 and path2.container.id in ("
            + CONTAINER_LIST
            + ")) and aliquot.linkDate between ? and ? and aliquot.patientVisit.clinicShipmentPatient.clinicShipment.site"
            + SITE_OPERATOR
            + SITE_ID
            + " group by aliquot.patientVisit.clinicShipmentPatient.patient.study.nameShort";

    public SAliquotsImpl(BiobankReport report) {
        super(QUERY, report);
    }

}