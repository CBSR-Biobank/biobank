package edu.ualberta.med.biobank.server.reports;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class QAAliquotsImpl extends AbstractReport {

    // @formatter:off
    private static final String QUERY = 
        "FROM " + Specimen.class.getName() + " as s" //$NON-NLS-1$ //$NON-NLS-2$
        + "    inner join fetch s.collectionEvent ce" //$NON-NLS-1$
        + "    inner join fetch s.specimenType st" //$NON-NLS-1$
        + "    inner join fetch ce.patient p" //$NON-NLS-1$
        + "    inner join fetch s.specimenPosition sp" //$NON-NLS-1$
        + "    left join fetch s.parentSpecimen ps" //$NON-NLS-1$
        + "    left join fetch ps.processingEvent pe" //$NON-NLS-1$
        + " WHERE s.createdAt between ? and ?" //$NON-NLS-1$
        + "     and s.specimenType.nameShort = ?" //$NON-NLS-1$
        + "     and s.specimenPosition.container.id in (SELECT c1.id" //$NON-NLS-1$
        + "        FROM " + Container.class.getName() + " as c1 " //$NON-NLS-1$ //$NON-NLS-2$
        + "            ," + Container.class.getName() + " as c2" //$NON-NLS-1$ //$NON-NLS-2$
        + "         WHERE c1.path LIKE if(length(c2.path),c2.path || '/','') || c2.id || '/%' "  //$NON-NLS-1$
        + "             and c2.id in (" + CONTAINER_LIST + "))" //$NON-NLS-1$ //$NON-NLS-2$
        + " ORDER BY RAND()"; //$NON-NLS-1$
    // @formatter:on

    private int numResults;

    public QAAliquotsImpl(BiobankReport report) {
        super(QUERY, report);
        numResults = (Integer) report.getParams().remove(
            report.getParams().size() - 1);
    }

    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        int lastIndex;
        if (results.size() != -1)
            lastIndex = Math.min(numResults, results.size());
        else
            lastIndex = numResults;
        if (lastIndex > 0) {
            results = results.subList(0, lastIndex);
        }
        List<Object> modifiedResults = new ArrayList<Object>();
        // get the info
        for (Object result : results) {
            Specimen specimen = (Specimen) result;
            String pnumber = specimen.getCollectionEvent().getPatient()
                .getPnumber();
            String inventoryId = specimen.getInventoryId();
            String specimenType = specimen.getSpecimenType().getNameShort();

            String dateProcessed = Messages.getString(
                "QAAliquotsImpl.nodate_processed", report.getLocale()); //$NON-NLS-1$
            Specimen parentSpecimen = specimen.getParentSpecimen();
            if (parentSpecimen != null) {
                ProcessingEvent pe = parentSpecimen.getProcessingEvent();
                if (pe != null) {
                    dateProcessed = DateFormatter.formatAsDate(pe
                        .getCreatedAt());
                }
            }

            String positionString = new StringBuilder(specimen
                .getSpecimenPosition().getContainer().getLabel()).append(
                specimen.getSpecimenPosition().getPositionString()).toString();
            modifiedResults.add(new Object[] { positionString, inventoryId,
                pnumber, dateProcessed, specimenType });
        }
        return modifiedResults;
    }
}