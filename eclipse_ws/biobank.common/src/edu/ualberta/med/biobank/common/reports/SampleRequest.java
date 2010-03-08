package edu.ualberta.med.biobank.common.reports;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.model.Aliquot;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SampleRequest extends QueryObject {

    protected static final String NAME = "Aliquot Request by CSV file";

    public SampleRequest(String op, Integer siteId) {
        super(
            "Given a CSV file detailing a request (Study, Date Drawn, Sample Type, # Requested), generate a list of sample locations.",
            "select s.patientVisit.patient.study.nameShort, s.inventoryId, s.linkDate, s.sampleType.name, s.aliquotPosition.container.label from "
                + Aliquot.class.getName()
                + " s where s.patientVisit.patient.study.site "
                + op
                + siteId
                + " and s.patientVisit.patient.study.nameShort like ? and datediff(s.linkDate, ?) between 0 and 1  and s.sampleType.name like ? ORDER BY RAND()",
            new String[] { "Study", "Inventory ID", "Date Drawn", "Type",
                "Location" });
        addOption("CSV File", String.class, "");
    }

    @Override
    public List<Object> executeQuery(WritableApplicationService appService,
        List<Object> params) throws ApplicationException {
        List<Object> results = new ArrayList<Object>();
        HQLCriteria c;
        for (int i = 0; i + 4 <= params.size(); i += 4) {
            c = new HQLCriteria(queryString);
            c.setParameters(params.subList(i, i + 3));
            // need to limit query size but not possible in hql
            Integer maxResults = (Integer) params.get(i + 3);
            List<Object> queried = appService.query(c);
            for (int j = 0; j < queried.size() && j < maxResults; j++)
                results.add(queried.get(j));
        }
        return postProcess(results);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
