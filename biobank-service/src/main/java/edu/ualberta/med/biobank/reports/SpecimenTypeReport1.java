package edu.ualberta.med.biobank.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.study.CollectionEvent;
import edu.ualberta.med.biobank.model.type.ActivityStatus;

/**
 * needs one parameters = study.nameShort
 */
public class SpecimenTypeReport1 extends AbstractReport {
    @SuppressWarnings("nls")
    private static final String QUERY =
        "SELECT ce.patient.pnumber,"
            + "     min(s.parentSpecimen.processingEvent.createdAt) as first_date_processed,"
            + "     min(s.topSpecimen.createdAt) as first_date_drawn, s.specimenType.name, count(*)"
            + " FROM "
            + CollectionEvent.class.getName()
            + " as ce join ce.allSpecimens as s"
            + " left join s.specimenPosition as p WHERE (p is null or p not in (from "
            + SpecimenPosition.class.getName()
            + " a where a.container.label like '"
            + SENT_SAMPLES_FREEZER_NAME
            + "'"
            + ")) and ce.patient.study.nameShort like ?"
            + " and s.activityStatus != "
            + ActivityStatus.CLOSED.getId()
            + " GROUP BY ce, s.specimenType "
            + " ORDER BY ce.patient.pnumber, min(s.parentSpecimen.processingEvent.createdAt)";

    public SpecimenTypeReport1(BiobankReport report) {
        super(QUERY, report);
    }
}
