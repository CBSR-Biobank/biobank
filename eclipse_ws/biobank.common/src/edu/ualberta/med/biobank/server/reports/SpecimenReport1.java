package edu.ualberta.med.biobank.server.reports;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Specimen;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SpecimenReport1 extends AbstractReport {
    private static final String QUERY = "SELECT s2" //$NON-NLS-1$
        + (" FROM " + Specimen.class.getName() + " s2") //$NON-NLS-1$ //$NON-NLS-2$
        + ("    inner join fetch s2.collectionEvent ce") //$NON-NLS-1$
        + ("    inner join fetch ce.patient p") //$NON-NLS-1$
        + ("    inner join fetch s2.topSpecimen ts") //$NON-NLS-1$
        + ("    inner join fetch s2.specimenType st") //$NON-NLS-1$
        + ("    inner join fetch s2.currentCenter c") //$NON-NLS-1$
        + ("    inner join fetch s2.specimenPosition pos") //$NON-NLS-1$
        + ("    inner join fetch pos.container cnt") //$NON-NLS-1$
        + " WHERE s2.id = (SELECT min(s.id) " //$NON-NLS-1$
        + ("        FROM " + Specimen.class.getName() + " s") //$NON-NLS-1$ //$NON-NLS-2$
        + "         WHERE s.collectionEvent.visitNumber = 1" //$NON-NLS-1$
        + "             and s.collectionEvent = s2.collectionEvent" //$NON-NLS-1$
        + "             and s.collectionEvent.patient.study.nameShort = ?" //$NON-NLS-1$
        + ("            and s.specimenType.nameShort = '" //$NON-NLS-1$
            + FTA_CARD_SAMPLE_TYPE_NAME + "'") //$NON-NLS-1$
        + "             and s.topSpecimen.createdAt > ?" //$NON-NLS-1$
        + ("            and s.specimenPosition.container.label not like '" //$NON-NLS-1$
            + SENT_SAMPLES_FREEZER_NAME + "'") + ")" //$NON-NLS-1$ //$NON-NLS-2$
        + " ORDER BY s2.collectionEvent.patient.pnumber"; //$NON-NLS-1$

    public SpecimenReport1(BiobankReport report) {
        super(QUERY, report);
    }

    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        ArrayList<Object> modifiedResults = new ArrayList<Object>();

        for (Object result : results) {
            Specimen specimen = (Specimen) result;

            String pnumber = specimen.getCollectionEvent().getPatient()
                .getPnumber();
            String inventoryId = specimen.getInventoryId();
            String dateDrawn = DateFormatter.formatAsDate(specimen
                .getTopSpecimen().getCreatedAt());
            String specimenType = specimen.getSpecimenType().getNameShort();
            String currentCenter = specimen.getCurrentCenter().getNameShort();

            String containerLabel = specimen.getSpecimenPosition()
                .getContainer().getLabel();
            String positionString = specimen.getSpecimenPosition()
                .getPositionString();

            modifiedResults.add(new Object[] { pnumber, dateDrawn, inventoryId,
                specimenType, currentCenter, containerLabel + positionString });
        }
        return modifiedResults;
    }
}