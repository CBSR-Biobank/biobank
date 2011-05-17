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
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;

public class CAliquotsTest extends AbstractReportTest {
    private static final Mapper<SpecimenWrapper, List<String>, Long> GROUP_ALIQUOTS_BY_STUDY_AND_CLINIC = new Mapper<SpecimenWrapper, List<String>, Long>() {
        public List<String> getKey(SpecimenWrapper aliquot) {
            return Arrays.asList(aliquot.getCollectionEvent().getPatient()
                .getStudy().getNameShort(), aliquot.getProcessingEvent()
                .getCenter().getNameShort());
        }

        public Long getValue(SpecimenWrapper type, Long oldValue) {
            return oldValue != null ? new Long(oldValue + 1) : new Long(1);
        }
    };

    @Test
    public void testResults() throws Exception {
        checkResults(getTopContainerIds(getContainers()), new Date(0),
            new Date());
    }

    @Test
    public void testEmptyDateRange() throws Exception {
        checkResults(getTopContainerIds(getContainers()), new Date(), new Date(
            0));
    }

    @Test
    public void testSmallDatePoint() throws Exception {
        List<SpecimenWrapper> aliquots = getSpecimens();
        Assert.assertTrue(aliquots.size() > 0);

        SpecimenWrapper aliquot = aliquots.get(aliquots.size() / 2);
        checkResults(getTopContainerIds(getContainers()),
            aliquot.getCreatedAt(), aliquot.getCreatedAt());
    }

    @Test
    public void testSmallDateRange() throws Exception {
        List<SpecimenWrapper> aliquots = getSpecimens();
        Assert.assertTrue(aliquots.size() > 0);

        SpecimenWrapper aliquot = aliquots.get(aliquots.size() / 2);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(aliquot.getCreatedAt());
        calendar.add(Calendar.HOUR_OF_DAY, 24);

        checkResults(getTopContainerIds(getContainers()),
            aliquot.getCreatedAt(), calendar.getTime());
    }

    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        String topContainerIdList = getReport().getContainerList();
        Date after = (Date) getReport().getParams().get(0);
        Date before = (Date) getReport().getParams().get(1);

        Collection<SpecimenWrapper> allAliquots = getSpecimens();
        Collection<SpecimenWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(
                AbstractReportTest.aliquotLinkedBetween(after, before),
                aliquotTopContainerIdIn(topContainerIdList)));

        List<Object> expectedResults = new ArrayList<Object>();

        for (Map.Entry<List<String>, Long> entry : MapperUtil.map(
            filteredAliquots, GROUP_ALIQUOTS_BY_STUDY_AND_CLINIC).entrySet()) {
            expectedResults.add(new Object[] { entry.getKey().get(0),
                entry.getKey().get(1), entry.getValue() });
        }

        return expectedResults;
    }

    private void checkResults(Collection<Integer> topContainerIds, Date after,
        Date before) throws Exception {
        getReport().setParams(Arrays.asList((Object) after, (Object) before));
        getReport().setContainerList(StringUtils.join(topContainerIds, ","));

        checkResults(EnumSet.of(CompareResult.SIZE));
    }
}
