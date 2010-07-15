package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.client.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.ContainerPath;

public class CabinetCAliquotsImpl extends AbstractReport {

    private static final String TYPE_NAME = "%Cabinet%";

    private static final String QUERY = "select aliquot.patientVisit.patient.study.nameShort, aliquot.patientVisit.shipment.clinic.name, count(*) from "
        + Aliquot.class.getName()
        + " as aliquot where aliquot.aliquotPosition.container.id in (select path1.container.id from "
        + ContainerPath.class.getName()
        + " as path1, "
        + ContainerPath.class.getName()
        + " as path2 where locate(path2.path, path1.path) > 0 and path2.container.containerType.name like '"
        + TYPE_NAME
        + "') and aliquot.patientVisit.patient.study.site"
        + SITE_OPERATOR
        + SITE_ID
        + " group by aliquot.patientVisit.patient.study.nameShort, aliquot.patientVisit.shipment.clinic.name";

    public CabinetCAliquotsImpl(BiobankReport report) {
        super(QUERY, report);
    }

}