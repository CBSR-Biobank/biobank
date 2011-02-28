package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class QAAliquotsImpl extends AbstractReport {

    // private static final String QUERY = "from " + Aliquot.class.getName()
    // + " as aliquot where aliquot.patientVisit.dateProcessed "
    // + "between ? and ? and aliquot.sampleType.nameShort LIKE ?"
    // + " and aliquot.aliquotPosition.container.id "
    // + "in (select path1.container.id from " + ContainerPath.class.getName()
    // + " as path1, " + ContainerPath.class.getName()
    // + " as path2 where path1.path like path2.path || '/%' and"
    // + " path2.container.id in (" + CONTAINER_LIST + ")) ORDER BY RAND()";

    // private int numResults;

    public QAAliquotsImpl(BiobankReport report) {
        // super(QUERY, report);
        // numResults = (Integer) report.getParams().remove(
        // report.getParams().size() - 1);

        super("", report);
    }

    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        // int lastIndex;
        // if (results.size() != -1)
        // lastIndex = Math.min(numResults, results.size());
        // else
        // lastIndex = numResults;
        // if (lastIndex > 0) {
        // results = results.subList(0, lastIndex);
        // }
        // List<Object> modifiedResults = new ArrayList<Object>();
        // // get the info
        // for (Object ob : results) {
        // Aliquot a = (Aliquot) ob;
        // String pnumber =
        // a.getProcessingEvent().getPatient().getPnumber();
        // String inventoryId = a.getInventoryId();
        // String stName = a.getSpecimenType().getNameShort();
        // String dateProcessed = DateFormatter.formatAsDate(a
        // .getProcessingEvent().getDateProcessed());
        // SpecimenWrapper aliquotWrapper = new SpecimenWrapper(appService,
        // a);
        // String aliquotLabel = aliquotWrapper.getPositionString();
        // modifiedResults.add(new Object[] { aliquotLabel, inventoryId,
        // pnumber, dateProcessed, stName });
        // }
        // return modifiedResults;
        return null;
    }
}