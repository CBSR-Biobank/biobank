package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class NewPVsByStudyClinicImpl extends AbstractReport {

    private static final String QUERY = "Select Alias.shipmentPatient.patient.study.nameShort, " //$NON-NLS-1$
        + "Alias.shipmentPatient.shipment.clinic.name, Year(Alias.dateProcessed), " //$NON-NLS-1$
        + GROUPBY_DATE
        + "(Alias.dateProcessed), count(*) from " //$NON-NLS-1$
        + ProcessingEvent.class.getName()
        + " as Alias where Alias.dateProcessed between ? and ?" //$NON-NLS-1$
        + " GROUP BY Alias.shipmentPatient.patient.study.nameShort, Alias.shipmentPatient.shipment.clinic.name, " //$NON-NLS-1$
        + "Year(Alias.dateProcessed), " //$NON-NLS-1$
        + GROUPBY_DATE + "(Alias.dateProcessed)"; //$NON-NLS-1$

    private DateRangeRowPostProcess dateRangePostProcess;

    public NewPVsByStudyClinicImpl(BiobankReport report) {
        super(QUERY, report);
        dateRangePostProcess = new DateRangeRowPostProcess(report.getGroupBy()
            .equals("Year"), 2); //$NON-NLS-1$
    }

    @Override
    public AbstractRowPostProcess getRowPostProcess() {
        return dateRangePostProcess;
    }

}