package edu.ualberta.med.biobank.common.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.ContainerPath;

public class QAFreezerAliquots extends QueryObject {

    protected static final String NAME = "Freezer Aliquot QA";
    int numResults;

    public QAFreezerAliquots(String op, Integer siteId) {
        super(
            "Retrieves a list of aliquots, at random, within a date range, by sample type.",
            "select aliquot.aliquotPosition.container.label, aliquot.inventoryId, "
                + "aliquot.patientVisit.patient.pnumber, aliquot.patientVisit.id, "
                + "aliquot.patientVisit.dateProcessed, aliquot.sampleType.nameShort from "
                + Aliquot.class.getName()
                + " as aliquot where aliquot.patientVisit.dateProcessed "
                + "between ? and ? and aliquot.sampleType.name like ?"
                + " and aliquot.aliquotPosition.container.id "
                + "in (select path1.container.id from "
                + ContainerPath.class.getName()
                + " as path1, "
                + ContainerPath.class.getName()
                + " as path2 where locate(path2.path, path1.path) > 0 and path2.container.containerType.name like ?) and aliquot.patientVisit.patient.study.site "
                + op + siteId + " ORDER BY RAND()", new String[] { "Label",
                "Inventory ID", "Patient", "Visit", "Date Processed",
                "Sample Type" });
        addOption("Start Date", Date.class, new Date(0));
        addOption("End Date", Date.class, new Date());
        addOption("Sample Type", String.class, "");
        addOption("# Aliquots", Integer.class, 0);
    }

    @Override
    public List<Object> preProcess(List<Object> params) {
        for (int i = 0; i < queryOptions.size() - 1; i++) {
            Option option = queryOptions.get(i);
            if (params.get(i) == null)
                params.set(i, option.getDefaultValue());
            if (option.type.equals(String.class))
                params.set(i, "%" + params.get(i) + "%");
        }
        numResults = (Integer) params.remove(params.size() - 1);
        params.add("%Freezer%");
        return params;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<Object> postProcess(List<Object> results) {
        ArrayList<Object> newList = new ArrayList<Object>();
        int max = Math.min(numResults, results.size());
        for (int i = 0; i < max; i++)
            newList.add(results.get(i));
        return newList;
    }

}