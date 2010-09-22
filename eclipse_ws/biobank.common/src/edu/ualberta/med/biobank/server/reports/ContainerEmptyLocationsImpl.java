package edu.ualberta.med.biobank.server.reports;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.LabelingScheme;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPath;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ContainerEmptyLocationsImpl extends AbstractReport {

    private static final String QUERY = "select c.container from "
        + ContainerPath.class.getName()
        + " c, "
        + ContainerPath.class.getName()
        + " parent where parent.id in ("
        + CONTAINER_LIST
        + ") and (c.path LIKE parent.path || '/%' OR c.id=parent.id) and c.container.label LIKE ?||'%' and c.container.containerType.sampleTypeCollection.size > 0 "
        + "and (c.container.containerType.capacity.rowCapacity * c.container.containerType.capacity.colCapacity) > c.container.aliquotPositionCollection.size and c.container.site "
        + SITE_OPERATOR + SITE_ID;

    public ContainerEmptyLocationsImpl(BiobankReport report) {
        super(QUERY, report);
    }

    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        List<Object> processedResults = new ArrayList<Object>();
        for (Object c : results) {
            ContainerWrapper container = new ContainerWrapper(appService,
                (Container) c);
            int rows = container.getRowCapacity();
            int cols = container.getColCapacity();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    RowColPos pos = new RowColPos(i, j);
                    try {
                        if (container.getAliquot(i, j) == null)
                            processedResults.add(new Object[] {
                                container.getLabel()
                                    + LabelingScheme.getPositionString(pos,
                                        container.getContainerType()
                                            .getChildLabelingScheme(), rows,
                                        cols),
                                container.getContainerType().getNameShort() });
                    } catch (BiobankCheckException e) {
                        // FIXME: not sure what to do, not sure if we care
                        continue;
                    }
                }
            }
        }
        return processedResults;
    }
}