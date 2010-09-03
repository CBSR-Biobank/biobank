package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.Mapper;
import edu.ualberta.med.biobank.common.util.MapperUtil;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class AliquotSCountTest {
    private static final Mapper<AliquotWrapper, List<String>, Long> GROUP_ALIQUOTS_BY_STUDY_AND_SAMPLE_TYPE = new Mapper<AliquotWrapper, List<String>, Long>() {
        public List<String> getKey(AliquotWrapper aliquot) {
            return Arrays.asList(aliquot.getPatientVisit().getPatient()
                .getStudy().getNameShort(), aliquot.getSampleType().getName());
        }

        public Long getValue(AliquotWrapper type, Long oldValue) {
            return oldValue != null ? new Long(oldValue + 1) : new Long(1);
        }
    };

    private List<Object> getExpectedResults(final Date after, final Date before) {
        Collection<AliquotWrapper> allAliquots = TestReports.getInstance()
            .getAliquots();
        Collection<AliquotWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(
                TestReports.aliquotLinkedBetween(after, before),
                TestReports.ALIQUOT_NOT_IN_SENT_SAMPLE_CONTAINER));

        List<Object> expectedResults = new ArrayList<Object>();

        for (Map.Entry<List<String>, Long> entry : MapperUtil.map(
            filteredAliquots, GROUP_ALIQUOTS_BY_STUDY_AND_SAMPLE_TYPE)
            .entrySet()) {
            expectedResults.add(new Object[] { entry.getKey().get(0),
                entry.getKey().get(1), entry.getValue() });
        }

        return expectedResults;
    }

    private BiobankReport getReport(Date after, Date before) {
        BiobankReport report = BiobankReport.getReportByName("AliquotSCount");
        report.setSiteInfo("=", TestReports.getInstance().getSites().get(0)
            .getId());
        report.setContainerList("");
        report.setGroupBy("");

        Object[] params = { after, before };
        report.setParams(Arrays.asList(params));

        return report;
    }

    private Collection<Object> checkReport(Date after, Date before)
        throws ApplicationException {
        return TestReports.getInstance().checkReport(getReport(after, before),
            getExpectedResults(after, before));
    }

    @Test
    public void testResults() throws Exception {
        Collection<Object> results = checkReport(new Date(0), new Date());
        Assert.assertTrue(results.size() > 0);
    }

    @Test
    public void testEmptyDateRange() throws Exception {
        Collection<Object> results = checkReport(new Date(), new Date(0));
        Assert.assertTrue(results.size() == 0);
    }

    @Test
    public void testSmallDatePoint() throws Exception {
        List<AliquotWrapper> aliquots = TestReports.getInstance().getAliquots();
        Assert.assertTrue(aliquots.size() > 0);

        AliquotWrapper aliquot = aliquots.get(aliquots.size() / 2);
        checkReport(aliquot.getLinkDate(), aliquot.getLinkDate());
    }

    @Test
    public void testSmallDateRange() throws Exception {
        List<AliquotWrapper> aliquots = TestReports.getInstance().getAliquots();
        Assert.assertTrue(aliquots.size() > 0);

        AliquotWrapper aliquot = aliquots.get(aliquots.size() / 2);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(aliquot.getLinkDate());
        calendar.add(Calendar.HOUR_OF_DAY, 24);

        checkReport(aliquot.getLinkDate(), calendar.getTime());
    }
}
