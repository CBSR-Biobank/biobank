package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Specimen;

public class FvLPatientVisitsImpl extends AbstractReport {
    private static final String QUERY = "SELECT s.collectionEvent.patient.study.nameShort," //$NON-NLS-1$
        + " s.originInfo.center.nameShort, min(s.topSpecimen.createdAt), max(s.topSpecimen.createdAt)" //$NON-NLS-1$
        + (" FROM " + Specimen.class.getName() + " s") //$NON-NLS-1$ //$NON-NLS-2$
        + " GROUP BY s.collectionEvent.patient.study.nameShort, s.originInfo.center.nameShort"; //$NON-NLS-1$

    public FvLPatientVisitsImpl(BiobankReport report) {
        super(QUERY, report);
    }

}
