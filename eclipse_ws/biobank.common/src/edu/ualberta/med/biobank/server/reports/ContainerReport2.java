package edu.ualberta.med.biobank.server.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.model.Container;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ContainerReport2 extends AbstractReport {

    // @formatter:off 
    private static final String QUERY = "SELECT c" //$NON-NLS-1$
        + " FROM " + Container.class.getName() + " c " //$NON-NLS-1$ //$NON-NLS-2$
        + "    inner join fetch c.containerType" //$NON-NLS-1$
        + "    ," + Container.class.getName() + " parent " //$NON-NLS-1$ //$NON-NLS-2$
        + " WHERE parent.id in (" + CONTAINER_LIST + ")" //$NON-NLS-1$ //$NON-NLS-2$
        + "    and (c.path LIKE if(length(parent.path),parent.path || '/','') || parent.id || '/%' "  //$NON-NLS-1$
        + "         OR c.id=parent.id) " //$NON-NLS-1$
        + "    and c.label LIKE ? || '%' " //$NON-NLS-1$
        + "    and c.containerType.specimenTypeCollection.size > 0" //$NON-NLS-1$
        + "    and (c.containerType.capacity.rowCapacity "  //$NON-NLS-1$
        + "         * c.containerType.capacity.colCapacity)" //$NON-NLS-1$
        + "        > c.specimenPositionCollection.size"; //$NON-NLS-1$
    // @formatter:on 

    public ContainerReport2(BiobankReport report) {
        super(QUERY, report);
    }

    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        List<Object> processedResults = new ArrayList<Object>();
        for (Object c : results) {

            ContainerWrapper container = new ContainerWrapper(appService,
                (Container) c);
            try {
                container.reload();
            } catch (Exception e) {
                e.printStackTrace();
            }
            int rows = container.getRowCapacity();
            int cols = container.getColCapacity();
            try {
                Map<RowColPos, SpecimenWrapper> aliquots = container
                    .getSpecimens();

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        RowColPos pos = new RowColPos(i, j);
                        if (!aliquots.containsKey(pos))
                            processedResults.add(new Object[] {
                                container.getLabel()
                                    + ContainerLabelingSchemeWrapper
                                        .getPositionString(pos, container
                                            .getContainerType()
                                            .getChildLabelingSchemeId(), rows,
                                            cols),
                                container.getContainerType().getNameShort() });

                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return processedResults;
    }
}