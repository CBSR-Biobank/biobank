package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class AliquotsByPalletTest extends AbstractReportTest {

    @Test
    public void testResults() throws Exception {
        List<AliquotWrapper> allAliquots = getAliquots();
        ContainerWrapper container, topContainer;

        for (AliquotWrapper aliquot : allAliquots) {
            container = aliquot.getParent();
            topContainer = getTopContainer(aliquot.getParent());

            if ((container != null) && (topContainer != null)) {
                checkResults(container.getLabel(), topContainer
                    .getContainerType().getNameShort());
            }
        }
    }

    @Test
    public void testEmptyCriteria() throws Exception {
        checkResults("", "");
    }

    @Override
    protected Collection<Object> getExpectedResults() {
        final String containerLabel = (String) getReport().getParams().get(0);
        final String topContainerTypeNameShort = (String) getReport()
            .getParams().get(1);

        Collection<AliquotWrapper> allAliquots = getAliquots();
        @SuppressWarnings("unchecked")
        Collection<AliquotWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(
                aliquotInContainerLabelled(containerLabel),
                aliquotInTopContainerType(topContainerTypeNameShort),
                aliquotSite(isInSite(), getSiteId())));

        List<Object> expectedResults = new ArrayList<Object>();

        for (AliquotWrapper aliquot : filteredAliquots) {
            expectedResults.add(aliquot.getWrappedObject());
        }

        return expectedResults;
    }

    private void checkResults(String containerLabel,
        String topContainerTypeNameShort) throws ApplicationException {
        getReport().setParams(
            Arrays.asList((Object) containerLabel,
                (Object) topContainerTypeNameShort));

        checkResults(EnumSet.of(CompareResult.SIZE));
    }

    private static final Predicate<AliquotWrapper> aliquotInContainerLabelled(
        final String containerLabel) {
        return new Predicate<AliquotWrapper>() {
            public boolean evaluate(AliquotWrapper aliquot) {
                return (aliquot.getParent() != null)
                    && aliquot.getParent().getLabel().equals(containerLabel);
            }
        };
    }

    private static final Predicate<AliquotWrapper> aliquotInTopContainerType(
        final String topContainerTypeNameShort) {
        return new Predicate<AliquotWrapper>() {
            public boolean evaluate(AliquotWrapper aliquot) {
                ContainerWrapper topLevelContainer = getTopContainer(aliquot
                    .getParent());
                return topLevelContainer == null ? false : topLevelContainer
                    .getContainerType().getNameShort()
                    .equals(topContainerTypeNameShort);
            }
        };
    }

    private static final ContainerWrapper getTopContainer(
        ContainerWrapper container) {
        while ((container != null) && (container.getParent() != null)) {
            container = container.getParent();
        }
        return container;
    }
}
