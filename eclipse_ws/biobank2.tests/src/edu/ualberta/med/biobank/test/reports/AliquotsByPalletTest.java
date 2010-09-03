package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class AliquotsByPalletTest {
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

    private List<Object> getExpectedResults(final String containerLabel,
        final String topContainerTypeNameShort) {
        Collection<AliquotWrapper> allAliquots = TestReports.getInstance()
            .getAliquots();
        Collection<AliquotWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(
                aliquotInContainerLabelled(containerLabel),
                aliquotInTopContainerType(topContainerTypeNameShort)));

        List<Object> expectedResults = new ArrayList<Object>();

        for (AliquotWrapper aliquot : filteredAliquots) {
            expectedResults.add(aliquot.getWrappedObject());
        }

        return expectedResults;
    }

    private BiobankReport getReport(final String containerLabel,
        final String topContainerTypeNameShort) {
        BiobankReport report = BiobankReport
            .getReportByName("AliquotsByPallet");
        report.setSiteInfo("=", TestReports.getInstance().getSites().get(0)
            .getId());
        report.setContainerList("");
        report.setGroupBy("");

        Object[] params = { containerLabel, topContainerTypeNameShort };
        report.setParams(Arrays.asList(params));

        return report;
    }

    private Collection<Object> checkReport(final String containerLabel,
        final String topContainerTypeNameShort) throws ApplicationException {
        return TestReports.getInstance().checkReport(
            getReport(containerLabel, topContainerTypeNameShort),
            getExpectedResults(containerLabel, topContainerTypeNameShort));
    }

    @Test
    public void testResults() throws Exception {
        Collection<Object> results;
        List<AliquotWrapper> allAliquots = TestReports.getInstance()
            .getAliquots();

        ContainerWrapper container, topContainer;
        for (AliquotWrapper aliquot : allAliquots) {
            container = aliquot.getParent();
            topContainer = getTopContainer(aliquot.getParent());

            if ((container != null) && (topContainer != null)) {
                results = checkReport(container.getLabel(), topContainer
                    .getContainerType().getNameShort());

                if (container.hasAliquots()) {
                    Assert.assertTrue(results.size() > 0);
                } else {
                    Assert.assertTrue(results.size() == 0);
                }
            }
        }
    }

    @Test
    public void testEmptyCriteria() throws Exception {
        Collection<Object> results = checkReport("", "");
        Assert.assertTrue(results.size() == 0);
    }
}
