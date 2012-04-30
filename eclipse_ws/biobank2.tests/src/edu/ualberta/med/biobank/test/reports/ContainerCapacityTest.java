package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Mapper;
import edu.ualberta.med.biobank.common.util.MapperUtil;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;

public class ContainerCapacityTest extends AbstractReportTest {
    private static final Mapper<ContainerWrapper, String, ResultRow> groupByTopContainerId(
        final Collection<ContainerWrapper> containers) {
        return new Mapper<ContainerWrapper, String, ResultRow>() {
            @Override
            public String getKey(ContainerWrapper container) {
                String path = getPath(container);

                if (path != null) {
                    int indexOfSlash = path.indexOf("/");
                    if (indexOfSlash > 0) {
                        return path.substring(0, indexOfSlash);
                    }
                }

                return null;
            }

            @Override
            public ResultRow getValue(ContainerWrapper container, ResultRow row) {
                row = row != null ? row : new ResultRow();

                if (row.label == null) {
                    for (ContainerWrapper possibleRootContainer : containers) {
                        String path = getPath(possibleRootContainer);
                        String key = getKey(container);
                        if ((path != null) && path.equals(key)) {
                            row.label = possibleRootContainer.getLabel()
                                + "("
                                + possibleRootContainer.getContainerType()
                                    .getNameShort() + ")";
                        }
                    }
                }

                row.totalCapacity += container.getContainerType()
                    .getRowCapacity()
                    * container.getContainerType().getColCapacity();

                if (container.getSpecimens() != null) {
                    row.usedSlots += container.getSpecimens().size();
                }

                return row;
            }

            private String getPath(ContainerWrapper container) {
                try {
                    return container.getPath();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    private static class ResultRow {
        String label = null;
        Long totalCapacity = new Long(0);
        Long usedSlots = new Long(0);
    }

    @Test
    public void testResults() throws Exception {
        checkResults(EnumSet.of(CompareResult.SIZE));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        Collection<ContainerWrapper> filteredContainers = PredicateUtil.filter(
            getContainers(), PredicateUtil.andPredicate(
                CONTAINER_CAN_STORE_SAMPLES_PREDICATE,
                containerSite(isInSite(), getSiteId())));

        List<Object> expectedResults = new ArrayList<Object>();

        for (ResultRow row : MapperUtil.map(filteredContainers,
            groupByTopContainerId(getContainers())).values()) {
            expectedResults.add(new Object[] { row.label, row.totalCapacity,
                row.usedSlots });
        }

        return expectedResults;
    }
    // TODO: test getRowPostProcess(). Test the implementations of
    // AbstractRowPostProcess instead?
}
