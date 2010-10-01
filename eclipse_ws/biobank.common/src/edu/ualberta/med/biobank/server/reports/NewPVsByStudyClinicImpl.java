package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;
import edu.ualberta.med.biobank.model.PatientVisit;

public class NewPVsByStudyClinicImpl extends AbstractReport {

    private static final String QUERY =
        "Select Alias.clinicShipmentPatient.patient.study.nameShort, "
            + "Alias.clinicShipmentPatient.clinicShipment.clinic.name, Year(Alias.dateProcessed), "
            + GROUPBY_DATE
            + "(Alias.dateProcessed), count(*) from "
            + PatientVisit.class.getName()
            + " as Alias where Alias.dateProcessed between ? and ? and Alias.clinicShipmentPatient.clinicShipment.site "
            + SITE_OPERATOR
            + SITE_ID
            + " GROUP BY Alias.clinicShipmentPatient.patient.study.nameShort, Alias.clinicShipmentPatient.clinicShipment.clinic.name, "
            + "Year(Alias.dateProcessed), " + GROUPBY_DATE
            + "(Alias.dateProcessed)";

    private DateRangeRowPostProcess dateRangePostProcess;

    public NewPVsByStudyClinicImpl(BiobankReport report) {
        super(QUERY, report);
        dateRangePostProcess =
            new DateRangeRowPostProcess(report.getGroupBy().equals("Year"), 2);
    }

    @Override
    public AbstractRowPostProcess getRowPostProcess() {
        return dateRangePostProcess;
    }

}