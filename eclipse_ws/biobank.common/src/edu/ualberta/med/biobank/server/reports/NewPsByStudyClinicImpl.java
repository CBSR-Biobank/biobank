package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;
import edu.ualberta.med.biobank.model.PatientVisit;

public class NewPsByStudyClinicImpl extends AbstractReport {

    private static final String QUERY =
        "select pv.clinicShipmentPatient.patient.study.nameShort,"
            + " pv.clinicShipmentPatient.clinicShipment.clinic.name, year(pv.dateProcessed), "
            + GROUPBY_DATE
            + "(pv.dateProcessed),"
            + " count(distinct pv.clinicShipmentPatient.patient) from "
            + PatientVisit.class.getCanonicalName()
            + " pv"
            + " where pv.dateProcessed=(select min(pvCollection.dateProcessed)"
            + " from edu.ualberta.med.biobank.model.Patient p"
            + " join p.clinicShipmentPatientCollection as csps"
            + " join csps.patientVisitCollection as pvCollection"
            + " where p=pv.clinicShipmentPatient.patient) and pv.dateProcessed between ? and ? "
            + " group by pv.clinicShipmentPatient.patient.study.nameShort, pv.clinicShipmentPatient.clinicShipment.clinic.name,"
            + " year(pv.dateProcessed), " + GROUPBY_DATE + "(pv.dateProcessed)";

    private DateRangeRowPostProcess dateRangePostProcess;

    public NewPsByStudyClinicImpl(BiobankReport report) {
        super(QUERY, report);
        dateRangePostProcess =
            new DateRangeRowPostProcess(report.getGroupBy().equals("Year"), 2);
    }

    @Override
    public AbstractRowPostProcess getRowPostProcess() {
        return dateRangePostProcess;
    }

}