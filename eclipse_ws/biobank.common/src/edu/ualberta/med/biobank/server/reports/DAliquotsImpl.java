package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;

public class DAliquotsImpl extends AbstractReport {

    // private static final String QUERY =
    // "select aliquot.patientVisit.shipmentPatient.patient.study.nameShort, "
    // +
    // " aliquot.patientVisit.shipmentPatient.shipment.clinic.nameShort, year(aliquot.linkDate),"
    // + GROUPBY_DATE
    // + "(aliquot.linkDate), count(aliquot.linkDate) from "
    // + Aliquot.class.getName()
    // + " as aliquot where aliquot.aliquotPosition.container.id in"
    // + " (select path1.container.id from "
    // + ContainerPath.class.getName()
    // + " as path1, "
    // + ContainerPath.class.getName()
    // + " as path2 where path1.path like path2.path || '/%' and"
    // + " path2.container.id in ("
    // + CONTAINER_LIST
    // +
    // ")) and aliquot.linkDate between ? and ? group by aliquot.patientVisit.shipmentPatient.patient.study.nameShort,"
    // +
    // " aliquot.patientVisit.shipmentPatient.shipment.clinic.nameShort, year(aliquot.linkDate), "
    // + GROUPBY_DATE + "(aliquot.linkDate)";

    private DateRangeRowPostProcess dateRangePostProcess;

    public DAliquotsImpl(BiobankReport report) {
        // super(QUERY, report);
        // dateRangePostProcess = new
        // DateRangeRowPostProcess(report.getGroupBy()
        // .equals("Year"), 2);
        super("", report); //$NON-NLS-1$
    }

    @Override
    public AbstractRowPostProcess getRowPostProcess() {
        return dateRangePostProcess;
    }
}