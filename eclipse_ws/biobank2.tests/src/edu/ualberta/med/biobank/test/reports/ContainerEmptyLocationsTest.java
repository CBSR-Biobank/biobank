package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerEmptyLocationsTest {
    private List<Object> getExpectedResults(final String containerLabel,
        final String topContainerTypeNameShort) {
        Predicate<ContainerWrapper> specificContainerLabel = new Predicate<ContainerWrapper>() {
            public boolean evaluate(ContainerWrapper container) {
                return container.getLabel().equals(containerLabel);
            }
        };
        Predicate<ContainerWrapper> specificContainerType = new Predicate<ContainerWrapper>() {
            public boolean evaluate(ContainerWrapper container) {
                return container.getContainerType().getNameShort()
                    .equals(topContainerTypeNameShort);
            }
        };

        List<ContainerWrapper> allContainers = TestReports.getInstance()
            .getContainers();
        Collection<ContainerWrapper> topContainers = PredicateUtil.filter(
            allContainers, PredicateUtil.andPredicate(specificContainerLabel,
                specificContainerType));

        Collection<ContainerWrapper> otherContainers = PredicateUtil.filter(
            allContainers, TestReports.CAN_STORE_SAMPLES_PREDICATE);

        List<Object> expectedResults = new ArrayList<Object>();

        for (ContainerWrapper topContainer : topContainers) {
            for (ContainerWrapper container : otherContainers) {
                try {
                    if (container.getPath().contains(
                        topContainer.getPath() + "/")) {
                        expectedResults.add(container.getWrappedObject());
                    }
                } catch (Exception e) {
                }
            }
        }

        return expectedResults;
    }

    private BiobankReport getReport(String containerLabel,
        String topContainerTypeShortName) {
        BiobankReport report = BiobankReport
            .getReportByName("ContainerEmptyLocations");
        report.setSiteInfo("=", TestReports.getInstance().getSites().get(0)
            .getId());
        report.setContainerList("");
        report.setGroupBy("");

        Object[] params = { containerLabel, topContainerTypeShortName };
        report.setParams(Arrays.asList(params));

        return report;
    }

    private void checkReport(String containerLabel,
        String topContainerTypeShortName) throws ApplicationException {
        TestReports.getInstance().checkReport(
            getReport(containerLabel, topContainerTypeShortName),
            getExpectedResults(containerLabel, topContainerTypeShortName));
    }

    @Test
    public void testResults() throws Exception {
        for (ContainerWrapper container : TestReports.getInstance()
            .getContainers()) {
            checkReport(container.getLabel(), container.getContainerType()
                .getNameShort());
            checkReport(container.getLabel(), "");
        }
    }

    @Test
    public void testEmpty() throws ApplicationException {
        checkReport("", "");
    }

    // TODO: test postProcess()
}
