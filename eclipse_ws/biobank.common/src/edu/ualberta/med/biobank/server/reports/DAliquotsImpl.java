package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.ContainerPath;

public class DAliquotsImpl extends AbstractReport {

    private static final String QUERY = "select aliquot.patientVisit.patient.study.nameShort, "
        + " aliquot.patientVisit.shipment.clinic.nameShort, year(aliquot.linkDate),"
        + GROUPBY_DATE
        + "(aliquot.linkDate), count(aliquot.linkDate) from "
        + Aliquot.class.getName()
        + " as aliquot where aliquot.aliquotPosition.container.id in"
        + " (select path1.container.id from "
        + ContainerPath.class.getName()
        + " as path1, "
        + ContainerPath.class.getName()
        + " as path2 where locate(path2.path, path1.path) > 0 and"
        + " path2.container.containerType.nameShort like ?) and aliquot.linkDate between ? and ? and aliquot.patientVisit.patient.study.site "
        + SITE_OPERATOR
        + SITE_ID
        + " group by aliquot.patientVisit.patient.study.nameShort,"
        + " aliquot.patientVisit.shipment.clinic.nameShort, year(aliquot.linkDate), "
        + GROUPBY_DATE + "(aliquot.linkDate)";

    private DateRangeRowPostProcess dateRangePostProcess;

    public DAliquotsImpl(BiobankReport report) {
        super(QUERY, report);
        dateRangePostProcess = new DateRangeRowPostProcess(report.getGroupBy()
            .equals("Year"), 2);
    }

    @Override
    protected AbstractRowPostProcess getRowPostProcess() {
        return dateRangePostProcess;
    }
}