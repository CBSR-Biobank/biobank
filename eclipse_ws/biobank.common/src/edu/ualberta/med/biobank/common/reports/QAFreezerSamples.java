package edu.ualberta.med.biobank.common.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.model.ContainerPath;
import edu.ualberta.med.biobank.model.Sample;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class QAFreezerSamples extends QueryObject {

    protected static final String NAME = "Freezer Sample QA";
    int numResults;

    public QAFreezerSamples(String op, Integer siteId) {
        super(
            "Retrieves a list of samples, at random, within a date range, by sample type.",
            "select sample.samplePosition.container.label, sample.inventoryId, sample.patientVisit.patient.pnumber, sample.patientVisit.id, sample.patientVisit.dateProcessed, sample.sampleType.nameShort from "
                + Sample.class.getName()
                + " as sample where sample.patientVisit.dateProcessed between ? and ? and sample.sampleType.name like ?"
                + " and sample.samplePosition.container.id in (select path1.container.id from "
                + ContainerPath.class.getName()
                + " as path1, "
                + ContainerPath.class.getName()
                + " as path2 where locate(path2.path, path1.path) > 0 and path2.container.containerType.name like ?) and sample.patientVisit.patient.study.site "
                + op + siteId + " ORDER BY RAND()", new String[] { "Label",
                "Inventory ID", "Patient", "Visit", "Date Processed",
                "Sample Type" });
        addOption("Start Date", Date.class, new Date(0));
        addOption("End Date", Date.class, new Date());
        addOption("Sample Type", String.class, "");
        addOption("# Samples", Integer.class, 0);
    }

    @Override
    public List<Object> executeQuery(WritableApplicationService appService,
        List<Object> params) throws ApplicationException {
        for (int i = 0; i < queryOptions.size() - 1; i++) {
            Option option = queryOptions.get(i);
            if (params.get(i) == null)
                params.set(i, option.getDefaultValue());
            if (option.type.equals(String.class))
                params.set(i, "%" + params.get(i) + "%");
        }
        numResults = (Integer) params.remove(params.size() - 1);
        params.add("%Freezer%");
        HQLCriteria c = new HQLCriteria(queryString);
        c.setParameters(params);
        List<Object> results = appService.query(c);
        return postProcess(results);
    }

    @Override
    public List<Object> postProcess(List<Object> results) {
        ArrayList<Object> newList = new ArrayList<Object>();
        for (int i = 0; i < numResults; i++)
            newList.add(results.get(i));
        return newList;
    }

    @Override
    public String getName() {
        return NAME;
    }
}