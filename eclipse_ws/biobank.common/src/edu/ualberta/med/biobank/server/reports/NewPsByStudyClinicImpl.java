package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class NewPsByStudyClinicImpl extends AbstractReport {

    private static final String QUERY = "select pv.shipmentPatient.patient.study.nameShort,"
        + " pv.shipmentPatient.shipment.clinic.name, year(pv.dateProcessed), "
        + GROUPBY_DATE
        + "(pv.dateProcessed),"
        + " count(distinct pv.shipmentPatient.patient) from "
        + ProcessingEvent.class.getCanonicalName()
        + " pv"
        + " where pv.dateProcessed=(select min(pvCollection.dateProcessed)"
        + " from edu.ualberta.med.biobank.model.Patient p"
        + " join p.shipmentPatientCollection as csps"
        + " join csps.patientVisitCollection as pvCollection"
        + " where p=pv.shipmentPatient.patient) and pv.dateProcessed between ? and ? "
        + " group by pv.shipmentPatient.patient.study.nameShort, pv.shipmentPatient.shipment.clinic.name,"
        + " year(pv.dateProcessed), " + GROUPBY_DATE + "(pv.dateProcessed)";

    private DateRangeRowPostProcess dateRangePostProcess;

    public NewPsByStudyClinicImpl(BiobankReport report) {
        super(QUERY, report);
        dateRangePostProcess = new DateRangeRowPostProcess(report.getGroupBy()
            .equals("Year"), 2);
    }

    @Override
    public AbstractRowPostProcess getRowPostProcess() {
        return dateRangePostProcess;
    }

}