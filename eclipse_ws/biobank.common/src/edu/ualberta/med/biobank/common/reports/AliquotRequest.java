package edu.ualberta.med.biobank.common.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
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
                + " and p.aliquot.patientVisit.patient.pnumber like ? and datediff(p.aliquot.patientVisit.dateDrawn, ?) between 0 and 1  and p.aliquot.sampleType.nameShort like ? ORDER BY RAND()",
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
        try {
            for (; i + 4 <= params.size(); i += 4) {
                c = new HQLCriteria(queryString);
                c.setParameters(params.subList(i, i + 3));
                // need to limit query size but not possible in hql
                Integer maxResults = (Integer) params.get(i + 3);
                List<Object> queried = appService.query(c);
                for (int j = 0; j < queried.size() && j < maxResults; j++)
                    results.add(queried.get(j));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BiobankCheckException("Failed to parse CSV: Line "
                + ((i / 4) + 1));
        }
        return results;
    }

    @Override
    protected List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        ArrayList<Object> modifiedResults = new ArrayList<Object>();
        for (Object ob : results) {
            Aliquot a = (Aliquot) ob;
            String pnumber = a.getPatientVisit().getPatient().getPnumber();
            String inventoryId = a.getInventoryId();
            Date dateDrawn = a.getPatientVisit().getDateDrawn();
            String stName = a.getSampleType().getNameShort();
            String aliquotLabel = new AliquotWrapper(appService, a)
                .getPositionString(true, false);
            modifiedResults.add(new Object[] { pnumber, inventoryId, dateDrawn,
                stName, aliquotLabel });
        }
        return modifiedResults;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
