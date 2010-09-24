package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;

public class NewPsByStudyClinicImpl extends AbstractReport {

    // does it make more sense to do: "pv = (select min(pvCollection.id) ..."?
    private static final String QUERY = "select pv.patient.study.nameShort,"
        + " pv.shipment.clinic.name, year(pv.dateProcessed), "
        + GROUPBY_DATE
        + "(pv.dateProcessed),"
        + " count(distinct pv.patient) from edu.ualberta.med.biobank.model.PatientVisit pv"
        + " where pv.dateProcessed=(select min(pvCollection.dateProcessed)"
        + " from edu.ualberta.med.biobank.model.Patient p join p.patientVisitCollection"
        + " as pvCollection where p=pv.patient) and pv.dateProcessed between ? and ? "
        + " group by pv.patient.study.nameShort, pv.shipment.clinic.name,"
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