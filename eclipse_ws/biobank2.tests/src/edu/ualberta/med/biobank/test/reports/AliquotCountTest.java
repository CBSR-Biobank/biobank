package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.Mapper;
import edu.ualberta.med.biobank.common.util.MapperUtil;
import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class AliquotCountTest {
    private static final Predicate<AliquotWrapper> NOT_SAMPLE_STORAGE_PREDICATE = new Predicate<AliquotWrapper>() {
        public boolean evaluate(AliquotWrapper aliquot) {
            return !aliquot.getParent().getLabel().startsWith("SS");
        }
    };
    private static final Mapper<AliquotWrapper, String, Long> SAMPLE_TYPE_NAME_MAPPER = new Mapper<AliquotWrapper, String, Long>() {
        public String getKey(AliquotWrapper aliquot) {
            return aliquot.getSampleType().getName();
        }

        public Long getValue(AliquotWrapper type, Long oldValue) {
            return oldValue != null ? new Long(oldValue + 1) : new Long(1);
        }
    };

    private List<Object> getExpectedResults(final Date after, final Date before) {
        Predicate<AliquotWrapper> betweenDates = new Predicate<AliquotWrapper>() {
            public boolean evaluate(AliquotWrapper aliquot) {
                return (aliquot.getLinkDate().after(after) || aliquot
                    .getLinkDate().equals(after))
                    && (aliquot.getLinkDate().before(before) || aliquot
                        .getLinkDate().equals(before));
            }
        };

        Collection<AliquotWrapper> allAliquots = TestReports.getInstance()
            .getAliquots();
        Collection<AliquotWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(betweenDates,
                NOT_SAMPLE_STORAGE_PREDICATE));

        List<Object> expectedResults = new ArrayList<Object>();

        for (Map.Entry<String, Long> entry : MapperUtil.map(filteredAliquots,
            SAMPLE_TYPE_NAME_MAPPER).entrySet()) {
            expectedResults
                .add(new Object[] { entry.getKey(), entry.getValue() });
        }

        return expectedResults;
    }

    private BiobankReport getReport(Date after, Date before) {
        BiobankReport report = BiobankReport.getReportByName("AliquotCount");
        report.setSiteInfo("=", TestReports.getInstance().getSites().get(0)
            .getId());
        report.setContainerList("");
        report.setGroupBy("");

        Object[] params = { after, before };
        report.setParams(Arrays.asList(params));

        return report;
    }

    private void checkReport(Date after, Date before)
        throws ApplicationException {
        TestReports.getInstance().checkReport(getReport(after, before),
            getExpectedResults(after, before));
    }

    @Test
    public void testResults() throws Exception {
        checkReport(new Date(0), new Date());
    }

    @Test
    public void testEmptyDateRange() throws Exception {
        checkReport(new Date(), new Date(0));
    }

    @Test
    public void testSmallDateRange() throws Exception {
        List<AliquotWrapper> aliquots = TestReports.getInstance().getAliquots();
        Assert.assertTrue(aliquots.size() > 0);

        AliquotWrapper aliquot = aliquots.get(aliquots.size() / 2);
        checkReport(aliquot.getLinkDate(), aliquot.getLinkDate());
    }
}
