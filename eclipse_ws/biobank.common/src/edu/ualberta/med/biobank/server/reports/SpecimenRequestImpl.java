package edu.ualberta.med.biobank.server.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Specimen;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SpecimenRequestImpl extends AbstractReport {

    // TODO: switch to CollectionEvent.visitNumber?

    private static final String QUERY = "SELECT s" //$NON-NLS-1$
        + (" FROM " + Specimen.class.getName() + " s ") //$NON-NLS-1$ //$NON-NLS-2$
        + ("    inner join fetch s.collectionEvent ce") //$NON-NLS-1$
        + ("    inner join fetch ce.patient p") //$NON-NLS-1$
        + ("    inner join fetch s.topSpecimen ts") //$NON-NLS-1$
        + ("    inner join fetch s.specimenType st") //$NON-NLS-1$
        + ("    inner join fetch s.activityStatus a") //$NON-NLS-1$
        + ("    WHERE s.specimenPosition.container.label not like '" //$NON-NLS-1$
            + SENT_SAMPLES_FREEZER_NAME + "'") //$NON-NLS-1$
        + "     and s.collectionEvent.patient.pnumber = ?" //$NON-NLS-1$
        + "     and datediff(s.topSpecimen.createdAt, ?) = 0" //$NON-NLS-1$
        + "     and s.specimenType.nameShort like ?" //$NON-NLS-1$
        + "     and s.activityStatus.name != 'Closed'" //$NON-NLS-1$
        + " ORDER BY s.activityStatus.name, RAND()"; //$NON-NLS-1$

    public SpecimenRequestImpl(BiobankReport report) {
        super(QUERY, report);
    }

    @Override
    public List<Object> executeQuery(WritableApplicationService appService)
        throws ApplicationException {
        List<Object> parameters = report.getParams();
        List<Object> results = new ArrayList<Object>();
        HQLCriteria c;
        for (Object o : parameters) {
            SpecimenRequest request = (SpecimenRequest) o;

            c = new HQLCriteria(queryString);
            c.setParameters(Arrays.asList(new Object[] { request.getPnumber(),
                request.getDateDrawn(), request.getSpecimenTypeNameShort() }));
            // need to limit query size but not possible in hql
            List<Object> queried = appService.query(c);

            long maxResults = request.getMaxAliquots();
            for (int j = 0; j < maxResults; j++) {
                if (j < queried.size())
                    results.add(queried.get(j));
            }
            if (queried.size() < maxResults) {
                results.add(getNotFoundRow(request.getPnumber(),
                    request.getDateDrawn(), request.getSpecimenTypeNameShort(),
                    maxResults, queried.size()));
            }
        }
        return results;
    }

    public static Object[] getNotFoundRow(String pnumber, Date dateDrawn,
        String typeName, long maxResults, Integer numResultsFound) {
        return new Object[] { pnumber,
            "", //$NON-NLS-1$
            DateFormatter.formatAsDate(dateDrawn), typeName,
            "NOT FOUND (" + (maxResults - numResultsFound) + ")", "" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    // Database calls are made so can't use RowPostProcess
    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        ArrayList<Object> modifiedResults = new ArrayList<Object>();
        for (Object result : results) {
            if (result instanceof Specimen) {
                Specimen specimen = (Specimen) result;

                String pnumber = specimen.getCollectionEvent().getPatient()
                    .getPnumber();
                String inventoryId = specimen.getInventoryId();
                Date dateDrawn = specimen.getTopSpecimen().getCreatedAt();
                String specimenType = specimen.getSpecimenType().getNameShort();
                String positionString = specimen.getSpecimenPosition()
                    .getContainer().getLabel()
                    + specimen.getSpecimenPosition().getPositionString();
                String activityStatus = specimen.getActivityStatus().getName();
                modifiedResults.add(new Object[] { pnumber, inventoryId,
                    dateDrawn, specimenType, positionString, activityStatus });
            } else if (result instanceof Object[]) {
                modifiedResults.add(result);
            }
        }

        return modifiedResults;
    }
}
