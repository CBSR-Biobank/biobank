package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.SpecimenPosition;

/**
 * needs one parameters = study.nameShort
 */
public class SampleTypePvCountImpl extends AbstractReport {
    private static final String QUERY = "SELECT ce.patient.pnumber," //$NON-NLS-1$
        + "     min(s.parentSpecimen.processingEvent.createdAt) as first_date_processed," //$NON-NLS-1$
        + "     min(s.topSpecimen.createdAt) as first_date_drawn, s.specimenType.name, count(*)" //$NON-NLS-1$
        + " FROM " //$NON-NLS-1$
        + CollectionEvent.class.getName()
        + " as ce join ce.allSpecimenCollection as s" //$NON-NLS-1$
        + " left join s.specimenPosition as p WHERE (p is null or p not in (from " //$NON-NLS-1$
        + SpecimenPosition.class.getName()
        + " a where a.container.label like '" //$NON-NLS-1$
        + SENT_SAMPLES_FREEZER_NAME
        + "'" //$NON-NLS-1$
        + ")) and ce.patient.study.nameShort like ?" //$NON-NLS-1$
        + " GROUP BY ce, s.specimenType " //$NON-NLS-1$
        + " ORDER BY ce.patient.pnumber, min(s.parentSpecimen.processingEvent.createdAt)"; //$NON-NLS-1$

    public SampleTypePvCountImpl(BiobankReport report) {
        super(QUERY, report);
    }
}
