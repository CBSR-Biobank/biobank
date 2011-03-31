package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.CollectionEvent;

/**
 * needs one parameters = study.nameShort
 */
public class SampleTypePvCountImpl extends AbstractReport {
    private static final String QUERY = "SELECT ce.patient.pnumber,"
        + "     min(s.parentSpecimen.processingEvent.createdAt) as first_date_processed,"
        + "     min(s.topSpecimen.createdAt) as first_date_drawn, s.specimenType.name, count(*)"
        + (" FROM " + CollectionEvent.class.getName() + " as ce ")
        + "     join ce.allSpecimenCollection as s"
        + (" WHERE s.specimenPosition.container.label not like '"
            + SENT_SAMPLES_FREEZER_NAME + "'")
        + "     and ce.patient.study.nameShort like ?"
        + " GROUP BY ce.patient.pnumber, s.specimenType "
        + " ORDER BY ce.patient.pnumber, min(s.parentSpecimen.processingEvent.createdAt)";

    public SampleTypePvCountImpl(BiobankReport report) {
        super(QUERY, report);
    }

}
