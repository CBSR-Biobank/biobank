package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;

public class SAliquotsImpl extends AbstractReport {

    // private static final String QUERY =
    // "select aliquot.patientVisit.shipmentPatient.patient.study.nameShort, count(*) from "
    // + Aliquot.class.getName()
    // +
    // " as aliquot where aliquot.aliquotPosition.container.id in (select path1.container.id from "
    // + ContainerPath.class.getName()
    // + " as path1, "
    // + ContainerPath.class.getName()
    // +
    // " as path2 where path1.path like path2.path || '/%' and path2.container.id in ("
    // + CONTAINER_LIST
    // + ")) and aliquot.linkDate between ? and ?"
    // +
    // " group by aliquot.patientVisit.shipmentPatient.patient.study.nameShort";

    public SAliquotsImpl(BiobankReport report) {
        // super(QUERY, report);

        super("", report); //$NON-NLS-1$
    }

}