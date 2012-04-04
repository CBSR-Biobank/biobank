package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Specimen;

public class ClinicReport1 extends AbstractReport {
    private static final String QUERY = "SELECT s.collectionEvent.patient.study.nameShort," 
        + " s.originInfo.center.nameShort, min(s.topSpecimen.createdAt), max(s.topSpecimen.createdAt)" 
        + (" FROM " + Specimen.class.getName() + " s")  
        + " GROUP BY s.collectionEvent.patient.study.nameShort, s.originInfo.center.nameShort"; 

    public ClinicReport1(BiobankReport report) {
        super(QUERY, report);
    }

}
