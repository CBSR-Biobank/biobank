package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;

public class ContainerEmptyLocationsTest extends AbstractReportTest {

    @Test
    public void testResults() throws Exception {
        for (ContainerWrapper container : getContainers()) {
            checkReport(container.getLabel(), container.getContainerType()
                .getNameShort());
            checkReport(container.getLabel(), "");
        }
    }

    @Test
    public void testEmpty() throws Exception {
        checkReport("", "");
    }

    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        final String containerLabel = (String) getReport().getParams().get(0);
        final String topContainerTypeNameShort = (String) getReport()
            .getParams().get(1);

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

        List<ContainerWrapper> allContainers = getContainers();
        Collection<ContainerWrapper> topContainers = PredicateUtil.filter(
            allContainers, PredicateUtil.andPredicate(specificContainerLabel,
                specificContainerType));

        Collection<ContainerWrapper> otherContainers = PredicateUtil.filter(
            allContainers, PredicateUtil.andPredicate(
                CONTAINER_CAN_STORE_SAMPLES_PREDICATE,
                containerSite(isInSite(), getSiteId())));

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

    @Override
    protected PostProcessTester getPostProcessTester() {
        return new ContainerEmptyLocationsPostProcessTester();
    }

    private void checkReport(String containerLabel,
        String topContainerTypeShortName) throws Exception {
        getReport().setParams(
            Arrays.asList((Object) containerLabel,
                (Object) topContainerTypeShortName));
        checkResults(EnumSet.of(CompareResult.SIZE));
    }
}
