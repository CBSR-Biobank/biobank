package edu.ualberta.med.biobank.common.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class AliquotRequest extends QueryObject {

    protected static final String NAME = "Aliquot Request by CSV file";

    public AliquotRequest(String op, Integer siteId) {
        super(
            "Given a CSV file detailing a request (Patient Number, Date Drawn, Sample Type, # Requested), generate a list of aliquot locations.",
            "select p.aliquot from "
                + AliquotPosition.class.getName()
                + " p where p.aliquot.patientVisit.patient.study.site "
                + op
                + siteId
                + " and p.container.label not like 'SS%' and p.aliquot.patientVisit.patient.pnumber like ? and datediff(p.aliquot.patientVisit.dateDrawn, ?) between 0 and 1  and p.aliquot.sampleType.nameShort like ? ORDER BY RAND()",
            new String[] { "Patient", "Inventory ID", "Date Drawn", "Type",
                "Location" });
        addOption("CSV File", String.class, "");
    }

    @Override
    protected List<Object> executeQuery(WritableApplicationService appService,
        List<Object> params) throws ApplicationException, BiobankCheckException {
        List<Object> results = new ArrayList<Object>();
        HQLCriteria c;
        int i = 0;
        for (; i + 4 <= params.size(); i += 4) {
            String pnumber = null;
            Date dateDrawn = null;
            String typeName = null;

            c = new HQLCriteria(queryString);
            try {
                pnumber = (String) params.get(i);
            } catch (ClassCastException e) {
                throw new ApplicationException("Failed to parse CSV: Line "
                    + ((i / 4) + 1) + ", Column 1 \nInvalid Patient Number: "
                    + params.get(i));
            }
            dateDrawn = DateFormatter.parseToDate((String) params.get(i + 1));
            if (dateDrawn == null)
                throw new ApplicationException("Failed to parse CSV: Line "
                    + ((i / 4) + 1) + ", Column 2 \nInvalid Date: "
                    + params.get(i + 1));
            try {
                typeName = (String) params.get(i + 2);
            } catch (ClassCastException e) {
                throw new ApplicationException("Failed to parse CSV: Line "
                    + ((i / 4) + 1) + ", Column 3 \nInvalid Sample Type: "
                    + params.get(i + 2));
            }
            c.setParameters(Arrays.asList(new Object[] { pnumber, dateDrawn,
                typeName }));
            // need to limit query size but not possible in hql
            Integer maxResults = 0;
            try {
                maxResults = Integer.parseInt((String) params.get(i + 3));
            } catch (Exception e) {
                throw new ApplicationException("Failed to parse CSV: Line "
                    + ((i / 4) + 1) + ", Column 4 \nInvalid Integer: "
                    + params.get(i + 3));
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

    @Override
    protected List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        ArrayList<Object> modifiedResults = new ArrayList<Object>();
        for (Object ob : results) {
            if (ob instanceof Object[]) {
                modifiedResults.add(ob);
            } else {
                Aliquot a = (Aliquot) ob;
                String pnumber = a.getPatientVisit().getPatient().getPnumber();
                String inventoryId = a.getInventoryId();
                Date dateDrawn = a.getPatientVisit().getDateDrawn();
                String stName = a.getSampleType().getNameShort();
                String aliquotLabel = new AliquotWrapper(appService, a)
                    .getPositionString(true, false);
                modifiedResults
                    .add(new Object[] { pnumber, inventoryId,
                        DateFormatter.formatAsDate(dateDrawn), stName,
                        aliquotLabel });
            }
        }
        return modifiedResults;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
