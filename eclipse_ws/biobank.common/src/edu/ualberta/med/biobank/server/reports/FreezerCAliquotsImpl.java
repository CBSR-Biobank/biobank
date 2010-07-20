package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.ContainerPath;

public class FreezerCAliquotsImpl extends AbstractReport {

    private static final String TYPE_NAME = "%Freezer%";

    private static final String QUERY = "select aliquot.patientVisit.patient.study.nameShort, "
        + "aliquot.patientVisit.shipment.clinic.nameShort, count(*) from "
        + Aliquot.class.getName()
        + " as aliquot where aliquot.aliquotPosition not in (from "
        + AliquotPosition.class.getName()
        + " a where a.container.label like '"
        + SENT_SAMPLES_FREEZER_NAME
        + "') and aliquot.aliquotPosition.container.id "
        + "in (select path1.container.id from "
        + ContainerPath.class.getName()
        + " as path1, "
        + ContainerPath.class.getName()
        + " as path2 where locate(path2.path, path1.path) > 0 and path2.container.containerType.name like '"
        + TYPE_NAME
        + "') and aliquot.linkDate between ? and ? and aliquot.patientVisit.patient.study.site"
        + SITE_OPERATOR
        + SITE_ID
        + " group by aliquot.patientVisit.patient.study.nameShort, aliquot.patientVisit.shipment.clinic.nameShort";

    public FreezerCAliquotsImpl(BiobankReport report) {
        super(QUERY, report);
    }

}