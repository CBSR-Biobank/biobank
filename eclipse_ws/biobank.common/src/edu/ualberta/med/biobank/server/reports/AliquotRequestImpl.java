package edu.ualberta.med.biobank.server.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class AliquotRequestImpl extends AbstractReport {

    private static final String QUERY = "select p.aliquot from "
        + AliquotPosition.class.getName()
        + " p where p.aliquot.patientVisit.shipment.site "
        + SITE_OPERATOR
        + SITE_ID
        + " and p.container.label not like '"
        + SENT_SAMPLES_FREEZER_NAME
        + "' and p.aliquot.patientVisit.patient.pnumber like ? and"
        + " datediff(p.aliquot.patientVisit.dateDrawn, ?) = 0  and"
        + " p.aliquot.sampleType.nameShort like ? and p.aliquot.activityStatus.name != 'Closed' ORDER BY p.aliquot.activityStatus.name, RAND()";

    public AliquotRequestImpl(BiobankReport report) {
        super(QUERY, report);
    }

    @Override
    public List<Object> executeQuery(WritableApplicationService appService)
        throws ApplicationException {
        List<Object> parameters = report.getParams();
        List<Object> results = new ArrayList<Object>();
        queryString = queryString.replaceAll(SITE_OPERATOR_SEARCH_STRING,
            report.getOp());
        queryString = queryString.replaceAll(SITE_ID_SEARCH_STRING, report
            .getSiteId().toString());
        HQLCriteria c;
        for (Object o : parameters) {
            AliquotRequest request = (AliquotRequest) o;

            c = new HQLCriteria(queryString);
            c.setParameters(Arrays.asList(new Object[] { request.getPnumber(),
                request.getDateDrawn(), request.getSampleTypeNameShort() }));
            // need to limit query size but not possible in hql
            List<Object> queried = appService.query(c);

            Integer maxResults = request.getMaxAliquots();
            for (int j = 0; j < maxResults; j++) {
                if (j < queried.size())
                    results.add(queried.get(j));
            }
            if (queried.size() < maxResults)
                results.add(getNotFoundRow(request.getPnumber(),
                    request.getDateDrawn(), request.getSampleTypeNameShort(),
                    maxResults, queried.size()));
        }
        return results;
    }

    public static Object[] getNotFoundRow(String pnumber, Date dateDrawn,
        String typeName, Integer maxResults, Integer numResultsFound) {
        return new Object[] { pnumber, "",
            DateFormatter.formatAsDate(dateDrawn), typeName,
            "NOT FOUND (" + (maxResults - numResultsFound) + ")", "" };
    }

    // Database calls are made so can't use RowPostProcess
    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        ArrayList<Object> modifiedResults = new ArrayList<Object>();
        for (Object ob : results) {
            if (ob instanceof Object[]) {
                modifiedResults.add(ob);
            } else {
                Aliquot aliquot = (Aliquot) ob;
                String pnumber = aliquot.getPatientVisit().getPatient()
                    .getPnumber();
                String inventoryId = aliquot.getInventoryId();
                Date dateDrawn = aliquot.getPatientVisit().getDateDrawn();
                String stName = aliquot.getSampleType().getNameShort();
                String aliquotLabel = new AliquotWrapper(appService, aliquot)
                    .getPositionString(true, false);
                String activityStatus = aliquot.getActivityStatus().getName();
                modifiedResults.add(new Object[] { pnumber, inventoryId,
                    dateDrawn, stName, aliquotLabel, activityStatus });
            }
        }
        return modifiedResults;
    }

}
