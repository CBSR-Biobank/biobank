package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;

public class PsByStudyImpl extends AbstractReport {

    private static final String QUERY = "select pv.shipmentPatient.patient.study.nameShort," //$NON-NLS-1$
        + " year(pv.dateProcessed), " //$NON-NLS-1$
        + GROUPBY_DATE
        + "(pv.dateProcessed), " //$NON-NLS-1$
        + "count(distinct pv.shipmentPatient.patient) from edu.ualberta.med.biobank.model.PatientVisit pv" //$NON-NLS-1$
        + " where pv.dateProcessed between ? and ?" //$NON-NLS-1$
        + " group by pv.shipmentPatient.patient.study.nameShort, year(pv.dateProcessed), " //$NON-NLS-1$
        + GROUPBY_DATE + "(pv.dateProcessed)"; //$NON-NLS-1$

    private DateRangeRowPostProcess dateRangePostProcess;

    public PsByStudyImpl(BiobankReport report) {
        super(QUERY, report);
        dateRangePostProcess = new DateRangeRowPostProcess(report.getGroupBy()
            .equals("Year"), 1); //$NON-NLS-1$
    }

    @Override
    public AbstractRowPostProcess getRowPostProcess() {
        return dateRangePostProcess;
    }

}