package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Mapper;
import edu.ualberta.med.biobank.common.util.MapperUtil;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SAliquotsTest extends AbstractReportTest {
    private static final Mapper<AliquotWrapper, String, Long> GROUP_BY_STUDY = new Mapper<AliquotWrapper, String, Long>() {
        public String getKey(AliquotWrapper aliquot) {
            return aliquot.getPatientVisit().getPatient().getStudy()
                .getNameShort();
        }

        public Long getValue(AliquotWrapper aliquot, Long count) {
            return count == null ? new Long(1) : new Long(count + 1);
        }
    };

    @Test
    public void testResults() throws Exception {
        checkResults(getTopContainerIds(), new Date(0), new Date());
    }

    @Test
    public void testEmptyDateRange() throws Exception {
        checkResults(getTopContainerIds(), new Date(), new Date(0));
    }

    @Test
    public void testSmallDatePoint() throws Exception {
        List<AliquotWrapper> aliquots = TestReports.getInstance().getAliquots();
        Assert.assertTrue(aliquots.size() > 0);

        AliquotWrapper aliquot = aliquots.get(aliquots.size() / 2);
        checkResults(getTopContainerIds(), aliquot.getLinkDate(),
            aliquot.getLinkDate());
    }

    @Test
    public void testSmallDateRange() throws Exception {
        List<AliquotWrapper> aliquots = TestReports.getInstance().getAliquots();
        Assert.assertTrue(aliquots.size() > 0);

        AliquotWrapper aliquot = aliquots.get(aliquots.size() / 2);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(aliquot.getLinkDate());
        calendar.add(Calendar.HOUR_OF_DAY, 24);

        checkResults(getTopContainerIds(), aliquot.getLinkDate(),
            calendar.getTime());
    }

    @Override
    protected Collection<Object> getExpectedResults() {
        String topContainerIdList = getReport().getContainerList();
        Date after = (Date) getReport().getParams().get(0);
        Date before = (Date) getReport().getParams().get(1);

        Collection<AliquotWrapper> allAliquots = getAliquots();

        @SuppressWarnings("unchecked")
        Collection<AliquotWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(
                TestReports.aliquotLinkedBetween(after, before),
                aliquotSite(isInSite(), getSiteId()),
                aliquotTopContainerIdIn(topContainerIdList)));

        Map<String, Long> groupedData = MapperUtil.map(filteredAliquots,
            GROUP_BY_STUDY);

        List<Object> expectedResults = new ArrayList<Object>();

        for (Map.Entry<String, Long> entry : groupedData.entrySet()) {
            expectedResults
                .add(new Object[] { entry.getKey(), entry.getValue() });
        }

        return expectedResults;
    }

    private void checkResults(Collection<Integer> topContainerIds, Date after,
        Date before) throws ApplicationException {
        getReport().setParams(Arrays.asList((Object) after, (Object) before));
        getReport().setContainerList(StringUtils.join(topContainerIds, ","));

        checkResults(EnumSet.of(CompareResult.SIZE));
    }
}
