package edu.ualberta.med.biobank.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.model.center.Container;
import edu.ualberta.med.biobank.model.util.RowColPos;

public class ContainerReport2 extends AbstractReport {

    // @formatter:off 
    @SuppressWarnings("nls")
    private static final String QUERY = "SELECT c" 
        + " FROM " + Container.class.getName() + " c "  
        + "    inner join fetch c.containerType" 
        + "    ," + Container.class.getName() + " parent "  
        + " WHERE parent.id in (" + CONTAINER_LIST + ")"  
        + "    and (c.path LIKE if(length(parent.path),parent.path || '/','') || parent.id || '/%' "  
        + "         OR c.id=parent.id) " 
        + "    and c.label LIKE ? || '%' " 
        + "    and c.containerType.specimenTypes.size > 0" 
        + "    and (c.containerType.capacity.rowCapacity "  
        + "         * c.containerType.capacity.colCapacity)" 
        + "        > c.specimenPositions.size"; 
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
                                container.getContainerType().getName() });

                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return processedResults;
    }
}