package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.util.ReportOption;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.ContainerPath;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class QACabinetAliquotsImpl extends AbstractReport {

    private static final String CONTAINER_TYPE_NAME = "%Cabinet%";

    private static final String QUERY = "select aliquot.aliquotPosition.container.label,"
        + " aliquot.inventoryId, aliquot.patientVisit.patient.pnumber, aliquot.patientVisit.id, "
        + "aliquot.patientVisit.dateProcessed, aliquot.sampleType.nameShort from "
        + Aliquot.class.getName()
        + " as aliquot where aliquot.patientVisit.dateProcessed "
        + "between ? and ? and aliquot.sampleType.nameShort LIKE ?"
        + " and aliquot.aliquotPosition.container.id "
        + "in (select path1.container.id from "
        + ContainerPath.class.getName()
        + " as path1, "
        + ContainerPath.class.getName()
        + " as path2 where locate(path2.path, path1.path) > 0 and"
        + " path2.container.containerType.name like '"
        + CONTAINER_TYPE_NAME
        + "') and aliquot.patientVisit.patient.study.site "
        + SITE_OPERATOR
        + SITE_ID + " ORDER BY RAND()";

    private int numResults;

    public QACabinetAliquotsImpl(List<Object> parameters,
        List<ReportOption> options) {
        super(QUERY, parameters, options);
        for (int i = 0; i < options.size() - 1; i++) {
            ReportOption option = options.get(i);
            if (parameters.get(i) == null)
                parameters.set(i, option.getDefaultValue());
            if (option.getType().equals(String.class))
                parameters.set(i, "%" + parameters.get(i) + "%");
        }
        numResults = (Integer) parameters.remove(parameters.size() - 1);
    }

    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        return results.subList(0, numResults);
    }
}