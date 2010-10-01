package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.ContainerPath;

public class CAliquotsImpl extends AbstractReport {

    private static final String QUERY =
        "select aliquot.patientVisit.clinicShipmentPatient.patient.study.nameShort, aliquot.patientVisit.clinicShipmentPatient.clinicShipment.clinic.nameShort, count(*) from "
            + Aliquot.class.getName()
            + " as aliquot where aliquot.aliquotPosition.container.id in (select path1.container.id from "
            + ContainerPath.class.getName()
            + " as path1, "
            + ContainerPath.class.getName()
            + " as path2 where path1.path like path2.path || '/%' and path2.container.id in ("
            + CONTAINER_LIST
            + ")) and aliquot.linkDate between ? and ? and aliquot.patientVisit.clinicShipmentPatient.clinicShipment.site"
            + SITE_OPERATOR
            + SITE_ID
            + " group by aliquot.patientVisit.clinicShipmentPatient.patient.study.nameShort, aliquot.patientVisit.clinicShipmentPatient.clinicShipment.clinic.nameShort";

    public CAliquotsImpl(BiobankReport report) {
        super(QUERY, report);
    }

}