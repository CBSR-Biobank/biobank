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

public class AliquotRequestImpl extends AbstractReport {
    private enum RowSource {
        HIBERNATE, FAKE;
    }

    private static class ResultRow {
        public final RowSource type;
        public final Object data;

        public ResultRow(RowSource type, Object data) {
            this.type = type;
            this.data = data;
        }
    }

    // pull all collection events of (source) specimens with a created at date
    // of the given date and then pull all specimens of those C.E.'s

    // TODO: switch to CollectionEvent.visitNumber?

    private static final String QUERY = "SELECT DISTINCT s.createdAt, alq"
        + (" FROM " + Specimen.class.getName() + " s ")
        + " JOIN s.collectionEvent ce"
        + " JOIN ce.allSpecimenCollection alq"
        + " WHERE s.parentSpecimen is null" // source specimens only
        + ("    and alq.specimenPosition.container.label not like '"
            + SENT_SAMPLES_FREEZER_NAME + "'")
        + "     and ce.patient.pnumber = ?"
        + "     and datediff(s.createdAt, ?) = 0"
        + "     and alq.specimenType.nameShort like ?"
        + "     and alq.activityStatus.name != 'Closed'"
        + " ORDER BY alq.activityStatus.name, RAND()";

    public AliquotRequestImpl(BiobankReport report) {
        super(QUERY, report);
    }

    @Override
    public List<Object> executeQuery(WritableApplicationService appService)
        throws ApplicationException {
        List<Object> parameters = report.getParams();
        List<Object> results = new ArrayList<Object>();
        HQLCriteria c;
        for (Object o : parameters) {
            AliquotRequest request = (AliquotRequest) o;

            c = new HQLCriteria(queryString);
            c.setParameters(Arrays.asList(new Object[] { request.getPnumber(),
                request.getDateDrawn(), request.getSpecimenTypeNameShort() }));
            // need to limit query size but not possible in hql
            List<Object> queried = appService.query(c);

            long maxResults = request.getMaxAliquots();
            for (int j = 0; j < maxResults; j++) {
                if (j < queried.size())
                    results.add(new ResultRow(RowSource.HIBERNATE, queried
                        .get(j)));
            }
            if (queried.size() < maxResults) {
                Object[] data = getNotFoundRow(request.getPnumber(),
                    request.getDateDrawn(), request.getSpecimenTypeNameShort(),
                    maxResults, queried.size());
                ResultRow row = new ResultRow(RowSource.FAKE, data);
                results.add(row);
            }
        }
        return results;
    }

    public static Object[] getNotFoundRow(String pnumber, Date dateDrawn,
        String typeName, long maxResults, Integer numResultsFound) {
        return new Object[] { pnumber, "",
            DateFormatter.formatAsDate(dateDrawn), typeName,
            "NOT FOUND (" + (maxResults - numResultsFound) + ")", "" };
    }

    // Database calls are made so can't use RowPostProcess
    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        ArrayList<Object> modifiedResults = new ArrayList<Object>();
        for (Object result : results) {
            ResultRow row = (ResultRow) result;
            if (row.type == RowSource.FAKE) {
                modifiedResults.add(row.data);
            } else {
                Object[] hibernateRow = (Object[]) row.data;

                Date dateDrawn = (Date) hibernateRow[0];
                Specimen specimen = (Specimen) hibernateRow[1];

                String pnumber = specimen.getCollectionEvent().getPatient()
                    .getPnumber();
                String inventoryId = specimen.getInventoryId();
                String specimenType = specimen.getSpecimenType().getNameShort();
                String positionString = specimen.getSpecimenPosition()
                    .getPositionString();
                String activityStatus = specimen.getActivityStatus().getName();
                modifiedResults.add(new Object[] { pnumber, inventoryId,
                    dateDrawn, specimenType, positionString, activityStatus });
            }
        }

        return modifiedResults;
    }
}
