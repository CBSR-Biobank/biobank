package edu.ualberta.med.biobank.server.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.ReportOption;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class AliquotRequestImpl extends AbstractReport {

    private static final String QUERY = "select p.aliquot from "
        + AliquotPosition.class.getName()
        + " p where p.aliquot.patientVisit.patient.study.site " + SITE_OPERATOR
        + SITE_ID + " and p.container.label not like '"
        + SENT_SAMPLES_FREEZER_NAME
        + "' and p.aliquot.patientVisit.patient.pnumber like ? and"
        + " datediff(p.aliquot.patientVisit.dateDrawn, ?) between 0 and 1  and"
        + " p.aliquot.sampleType.nameShort like ? ORDER BY RAND()";

    public AliquotRequestImpl(List<Object> parameters,
        List<ReportOption> options) {
        super(QUERY, parameters, options);
    }

    @Override
    public List<Object> executeQuery(WritableApplicationService appService,
        String siteOperator, Integer siteId) throws ApplicationException {
        List<Object> results = new ArrayList<Object>();
        queryString = queryString.replaceAll(SITE_OPERATOR_SEARCH_STRING,
            siteOperator);
        queryString = queryString.replaceAll(SITE_ID_SEARCH_STRING,
            siteId.toString());
        HQLCriteria c;
        int i = 0;
        for (; i + 4 <= parameters.size(); i += 4) {
            String pnumber = null;
            Date dateDrawn = null;
            String typeName = null;

            c = new HQLCriteria(queryString);
            try {
                pnumber = (String) parameters.get(i);
            } catch (ClassCastException e) {
                throw new ApplicationException("Failed to parse CSV: Line "
                    + ((i / 4) + 1) + ", Column 1 \nInvalid Patient Number: "
                    + parameters.get(i));
            }
            dateDrawn = DateFormatter.parseToDate((String) parameters
                .get(i + 1));
            if (dateDrawn == null)
                throw new ApplicationException("Failed to parse CSV: Line "
                    + ((i / 4) + 1) + ", Column 2 \nInvalid Date: "
                    + parameters.get(i + 1));
            try {
                typeName = (String) parameters.get(i + 2);
            } catch (ClassCastException e) {
                throw new ApplicationException("Failed to parse CSV: Line "
                    + ((i / 4) + 1) + ", Column 3 \nInvalid Sample Type: "
                    + parameters.get(i + 2));
            }
            c.setParameters(Arrays.asList(new Object[] { pnumber, dateDrawn,
                typeName }));
            // need to limit query size but not possible in hql
            Integer maxResults = 0;
            try {
                maxResults = Integer.parseInt((String) parameters.get(i + 3));
            } catch (Exception e) {
                throw new ApplicationException("Failed to parse CSV: Line "
                    + ((i / 4) + 1) + ", Column 4 \nInvalid Integer: "
                    + parameters.get(i + 3));
            }
            if (maxResults <= 0)
                throw new ApplicationException("Failed to parse CSV: Line "
                    + ((i / 4) + 1)
                    + ", Column 4 \n Value must be greater than zero.");
            if (maxResults >= 1000)
                throw new ApplicationException("Failed to parse CSV: Line "
                    + ((i / 4) + 1)
                    + ", Column 4 \n Value must be less than 1000.");
            List<Object> queried = appService.query(c);
            for (int j = 0; j < maxResults; j++) {
                if (j < queried.size())
                    results.add(queried.get(j));
                else
                    results.add(new Object[] { pnumber, "", dateDrawn,
                        typeName, "NOT FOUND" });
            }
        }
        return results;
    }

    // Database calls are made so can't use RowPostProcess
    @Override
    protected List<Object> postProcess(WritableApplicationService appService,
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
                modifiedResults.add(new Object[] { pnumber, inventoryId,
                    dateDrawn, stName, aliquotLabel });
            }
        }
        return modifiedResults;
    }

}
