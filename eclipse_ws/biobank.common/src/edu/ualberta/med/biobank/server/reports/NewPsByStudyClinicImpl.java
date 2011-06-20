package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class NewPsByStudyClinicImpl extends AbstractReport {

    private static final String QUERY = "select pv.shipmentPatient.patient.study.nameShort," //$NON-NLS-1$
        + " pv.shipmentPatient.shipment.clinic.name, year(pv.dateProcessed), " //$NON-NLS-1$
        + GROUPBY_DATE
        + "(pv.dateProcessed)," //$NON-NLS-1$
        + " count(distinct pv.shipmentPatient.patient) from " //$NON-NLS-1$
        + ProcessingEvent.class.getCanonicalName()
        + " pv" //$NON-NLS-1$
        + " where pv.dateProcessed=(select min(pvCollection.dateProcessed)" //$NON-NLS-1$
        + " from edu.ualberta.med.biobank.model.Patient p" //$NON-NLS-1$
        + " join p.shipmentPatientCollection as csps" //$NON-NLS-1$
        + " join csps.patientVisitCollection as pvCollection" //$NON-NLS-1$
        + " where p=pv.shipmentPatient.patient) and pv.dateProcessed between ? and ? " //$NON-NLS-1$
        + " group by pv.shipmentPatient.patient.study.nameShort, pv.shipmentPatient.shipment.clinic.name," //$NON-NLS-1$
        + " year(pv.dateProcessed), " + GROUPBY_DATE + "(pv.dateProcessed)"; //$NON-NLS-1$ //$NON-NLS-2$

    private DateRangeRowPostProcess dateRangePostProcess;

    public NewPsByStudyClinicImpl(BiobankReport report) {
        super(QUERY, report);
        dateRangePostProcess = new DateRangeRowPostProcess(report.getGroupBy()
            .equals("Year"), 2); //$NON-NLS-1$
    }

    @Override
    public AbstractRowPostProcess getRowPostProcess() {
        return dateRangePostProcess;
    }

}