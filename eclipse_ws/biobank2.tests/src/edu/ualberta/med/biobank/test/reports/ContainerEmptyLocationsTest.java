package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;

public class ContainerEmptyLocationsTest extends AbstractReportTest {

    @Test
    public void testResults() throws Exception {
        List<ContainerWrapper> topContainers = new ArrayList<ContainerWrapper>(
            getTopContainers(getContainers()));
        for (ContainerWrapper container : getContainers()) {
            for (int i = 0, n = topContainers.size(); i < n; i++) {
                checkReport(container.getLabel(),
                    topContainers.subList(i, i + 1));
            }
            checkReport(container.getLabel(), topContainers);
        }
    }

    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        final String containerLabel = (String) getReport().getParams().get(0);
        final String topContainerList = getReport().getContainerList();

        Predicate<ContainerWrapper> specificContainerLabel =
            new Predicate<ContainerWrapper>() {
                @Override
                public boolean evaluate(ContainerWrapper container) {
                    return container.getLabel().startsWith(containerLabel);
                }
            };
        Predicate<ContainerWrapper> specificTopContainer =
            new Predicate<ContainerWrapper>() {
                @Override
                public boolean evaluate(ContainerWrapper container) {
                    final List<Integer> topContainerIds =
                        new ArrayList<Integer>();
                    for (String id : topContainerList.split(",")) {
                        topContainerIds.add(Integer.valueOf(id));
                    }
                    return topContainerIds.contains(container.getId());
                }
            };

        List<ContainerWrapper> allContainers = getContainers();
        Collection<ContainerWrapper> topContainers = PredicateUtil.filter(
            allContainers, specificTopContainer);

        Collection<ContainerWrapper> otherContainers = PredicateUtil.filter(
            allContainers, PredicateUtil.andPredicate(specificContainerLabel,
                PredicateUtil.andPredicate(
                    CONTAINER_CAN_STORE_SAMPLES_PREDICATE,
                    containerSite(isInSite(), getSiteId()))));

        List<Object> expectedResults = new ArrayList<Object>();

        for (ContainerWrapper topContainer : topContainers) {
            for (ContainerWrapper container : otherContainers) {
                try {
                    if (container.getPath().startsWith(
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
        List<ContainerWrapper> topContainers) throws Exception {
        List<Object> params = new ArrayList<Object>();
        params.add(containerLabel);
        getReport().setParams(params);
        getReport().setContainerList(
            StringUtils.join(getTopContainerIds(topContainers), ","));
        checkResults(EnumSet.of(CompareResult.SIZE));
    }
}
