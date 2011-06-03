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

import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Mapper;
import edu.ualberta.med.biobank.common.util.MapperUtil;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;

public class AliquotCountTest extends AbstractReportTest {
    private static final Mapper<SpecimenWrapper, String, Long> SAMPLE_TYPE_NAME_MAPPER = new Mapper<SpecimenWrapper, String, Long>() {
        public String getKey(SpecimenWrapper aliquot) {
            return aliquot.getSpecimenType().getName();
        }

        public Long getValue(SpecimenWrapper type, Long oldValue) {
            return oldValue != null ? new Long(oldValue + 1) : new Long(1);
        }
    };

    @Test
    public void testResults() throws Exception {
        checkResults(new Date(0), new Date());
    }

    @Test
    public void testEmptyDateRange() throws Exception {
        checkResults(new Date(), new Date(0));
    }

    @Test
    public void testSmallDatePoint() throws Exception {
        List<SpecimenWrapper> aliquots = getSpecimens();
        Assert.assertTrue(aliquots.size() > 0);

        SpecimenWrapper aliquot = aliquots.get(aliquots.size() / 2);
        checkResults(aliquot.getCreatedAt(), aliquot.getCreatedAt());
    }

    @Test
    public void testSmallDateRange() throws Exception {
        List<SpecimenWrapper> aliquots = getSpecimens();
        Assert.assertTrue(aliquots.size() > 0);

        SpecimenWrapper aliquot = aliquots.get(aliquots.size() / 2);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(aliquot.getCreatedAt());
        calendar.add(Calendar.HOUR_OF_DAY, 24);

        checkResults(aliquot.getCreatedAt(), calendar.getTime());
    }

    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        Date after = (Date) getReport().getParams().get(0);
        Date before = (Date) getReport().getParams().get(1);

        Collection<SpecimenWrapper> allAliquots = getSpecimens();
        @SuppressWarnings("unchecked")
        Collection<SpecimenWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(
                AbstractReportTest.aliquotLinkedBetween(after, before),
                ALIQUOT_NOT_IN_SENT_SAMPLE_CONTAINER,
                aliquotSite(isInSite(), getSiteId())));

        List<Object> expectedResults = new ArrayList<Object>();

        for (Map.Entry<String, Long> entry : MapperUtil.map(filteredAliquots,
            SAMPLE_TYPE_NAME_MAPPER).entrySet()) {
            expectedResults
                .add(new Object[] { entry.getKey(), entry.getValue() });
        }

        return expectedResults;
    }

    private void checkResults(Date after, Date before) throws Exception {
        getReport().setParams(Arrays.asList((Object) after, (Object) before));

        checkResults(EnumSet.of(CompareResult.SIZE));
    }
}
