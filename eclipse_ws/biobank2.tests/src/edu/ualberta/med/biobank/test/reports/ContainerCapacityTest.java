package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.Mapper;
import edu.ualberta.med.biobank.common.util.MapperUtil;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerCapacityTest {
    private static final Mapper<ContainerWrapper, String, ResultRow> CONTAINER_PATH_MAPPER = new Mapper<ContainerWrapper, String, ResultRow>() {
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

        public ResultRow getValue(ContainerWrapper container, ResultRow row) {
            row = row != null ? row : new ResultRow();

            if (row.label == null) {
                for (ContainerWrapper possibleRootContainer : TestReports
                    .getInstance().getContainers()) {
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

            row.totalCapacity += container.getContainerType().getRowCapacity()
                * container.getContainerType().getColCapacity();

            if (container.getAliquots() != null) {
                row.usedSlots += container.getAliquots().size();
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

    private static class ResultRow {
        String label = null;
        Long totalCapacity = new Long(0);
        Long usedSlots = new Long(0);
    }

    private Collection<Object> getExpectedResults(
        Collection<ContainerWrapper> containers) {

        Collection<ContainerWrapper> filteredContainers = PredicateUtil.filter(
            containers, TestReports.CAN_STORE_SAMPLES_PREDICATE);

        List<Object> expectedResults = new ArrayList<Object>();

        for (ResultRow row : MapperUtil.map(filteredContainers,
            CONTAINER_PATH_MAPPER).values()) {
            expectedResults.add(new Object[] { row.label, row.totalCapacity,
                row.usedSlots });
        }

        return expectedResults;
    }

    private BiobankReport getReport() throws ApplicationException {
        BiobankReport report = BiobankReport
            .getReportByName("ContainerCapacity");
        System.out.println(report.getClassName());
        report.setSiteInfo("=", TestReports.getInstance().getSites().get(0)
            .getId());
        report.setContainerList("");
        report.setGroupBy("");
        report.setParams(Arrays.asList());

        return report;
    }

    @Test
    public void testResults() throws Exception {
        TestReports.getInstance().checkReport(getReport(),
            getExpectedResults(TestReports.getInstance().getContainers()));
    }

    // TODO: test getRowPostProcess(). Test the implementations of
    // AbstractRowPostProcess instead?
}
