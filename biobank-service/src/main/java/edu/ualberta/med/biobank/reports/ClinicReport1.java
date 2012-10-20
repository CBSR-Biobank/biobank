package edu.ualberta.med.biobank.reports;

import edu.ualberta.med.biobank.model.study.Specimen;

public class ClinicReport1 extends AbstractReport {
    @SuppressWarnings("nls")
    private static final String QUERY =
        "SELECT s.collectionEvent.patient.study.nameShort,"
            + " s.originInfo.center.nameShort, min(s.topSpecimen.createdAt), max(s.topSpecimen.createdAt)"
            + (" FROM " + Specimen.class.getName() + " s")
            + " GROUP BY s.collectionEvent.patient.study.nameShort, s.originInfo.center.nameShort";

    public ClinicReport1(BiobankReport report) {
        super(QUERY, report);
    }

}
