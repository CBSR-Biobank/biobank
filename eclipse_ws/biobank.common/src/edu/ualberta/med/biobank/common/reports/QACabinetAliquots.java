package edu.ualberta.med.biobank.common.reports;

import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.ContainerPath;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class QACabinetAliquots extends QueryObject {

    protected static final String NAME = "Cabinet Aliquot QA";
    int numResults;

    public QACabinetAliquots(String op, Integer siteId) {
        super(
            "Retrieves a list of aliquots, at random, within a date range, by sample type.  Note: the number of aliquots must be specified, and the top container's name must contain \"Cabinet\".",
            "select aliquot.aliquotPosition.container.label, aliquot.inventoryId, "
                + "aliquot.patientVisit.patient.pnumber, aliquot.patientVisit.id, "
                + "aliquot.patientVisit.dateProcessed, aliquot.sampleType.nameShort from "
                + Aliquot.class.getName()
                + " as aliquot where aliquot.patientVisit.dateProcessed "
                + "between ? and ? and aliquot.sampleType.nameShort LIKE ?"
                + " and aliquot.aliquotPosition.container.id "
                + "in (select path1.container.id from "
                + ContainerPath.class.getName()
                + " as path1, "
                + ContainerPath.class.getName()
                + " as path2 where locate(path2.path, path1.path) > 0 and path2.container.containerType.name like ?) and aliquot.patientVisit.patient.study.site "
                + op + siteId + " ORDER BY RAND()", new String[] { "Label",
                "Inventory ID", "Patient", "Visit", "Date Processed",
                "Sample Type" });
        addOption("Start Date (Processed)", Date.class, new Date(0));
        addOption("End Date (Processed)", Date.class, new Date());
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
        params.add("%Cabinet%");
        return params;
    }

    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        return results.subList(0, numResults);
    }

    @Override
    public String getName() {
        return NAME;
    }
}