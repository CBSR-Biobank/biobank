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
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;

public class AliquotsByPalletTest extends AbstractReportTest {

    @Test
    public void testResults() throws Exception {
        List<ContainerWrapper> topContainers = new ArrayList<ContainerWrapper>(
            getTopContainers(getContainers()));

        for (ContainerWrapper container : getContainers()) {
            for (int i = 0, n = topContainers.size(); i < n; i++) {
                checkResults(container.getLabel(),
                    topContainers.subList(i, i + 1));
            }
            checkResults(container.getLabel(), topContainers);
        }
    }

    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        final String containerLabel = (String) getReport().getParams().get(0);
        final String topContainers = getReport().getContainerList();

        Collection<SpecimenWrapper> allAliquots = getSpecimens();
        Collection<SpecimenWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(
                aliquotInContainerLabelled(containerLabel),
                aliquotTopContainerIdIn(topContainers)));

        List<Object> expectedResults = new ArrayList<Object>();

        for (SpecimenWrapper aliquot : filteredAliquots) {
            expectedResults.add(aliquot.getWrappedObject());
        }

        return expectedResults;
    }

    private void checkResults(String containerLabel,
        List<ContainerWrapper> topContainerList) throws Exception {
        List<Object> params = new ArrayList<Object>();
        params.add(containerLabel);
        getReport().setParams(params);
        getReport().setContainerList(
            StringUtils.join(getTopContainerIds(topContainerList), ","));
        checkResults(EnumSet.of(CompareResult.SIZE));
    }

    private static final Predicate<SpecimenWrapper> aliquotInContainerLabelled(
        final String containerLabel) {
        return new Predicate<SpecimenWrapper>() {
            public boolean evaluate(SpecimenWrapper aliquot) {
                return (aliquot.getParentContainer() != null)
                    && aliquot.getParentContainer().getLabel()
                        .equals(containerLabel);
            }
        };
    }
}
