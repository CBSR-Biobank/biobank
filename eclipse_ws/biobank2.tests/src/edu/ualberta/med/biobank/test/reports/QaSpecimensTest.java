package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;

public class QaSpecimensTest extends AbstractReportTest {
    // cheap way to get all results
    private static final Integer NUM_RESULTS = new Integer(Integer.MAX_VALUE);

    @Test
    public void testResults() throws Exception {
        for (SpecimenTypeWrapper sampleType : getSpecimenTypes()) {
            checkResults(getTopContainerIds(getContainers()), new Date(0),
                new Date(), sampleType.getNameShort());
        }
    }

    @Test
    public void testEmptyDateRange() throws Exception {
        for (SpecimenTypeWrapper sampleType : getSpecimenTypes()) {
            checkResults(getTopContainerIds(getContainers()), new Date(),
                new Date(0), sampleType.getNameShort());
        }
    }

    @Test
    public void testSmallDatePoint() throws Exception {
        List<SpecimenWrapper> aliquots = getSpecimens();
        Assert.assertTrue(aliquots.size() > 0);

        SpecimenWrapper aliquot = aliquots.get(aliquots.size() / 2);
        ProcessingEventWrapper visit = aliquot.getProcessingEvent();
        checkResults(getTopContainerIds(getContainers()), visit.getCreatedAt(),
            visit.getCreatedAt(), aliquot.getSpecimenType().getNameShort());
    }

    @Test
    public void testSmallDateRange() throws Exception {
        List<SpecimenWrapper> aliquots = getSpecimens();
        Assert.assertTrue(aliquots.size() > 0);

        SpecimenWrapper aliquot = aliquots.get(aliquots.size() / 2);
        ProcessingEventWrapper visit = aliquot.getProcessingEvent();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(visit.getCreatedAt());
        calendar.add(Calendar.HOUR_OF_DAY, 24);

        checkResults(getTopContainerIds(getContainers()), visit.getCreatedAt(),
            calendar.getTime(), aliquot.getSpecimenType().getNameShort());
    }

    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        String topContainerIdList = getReport().getContainerList();
        Date after = (Date) getReport().getParams().get(0);
        Date before = (Date) getReport().getParams().get(1);
        String sampleTypeNameShort = (String) getReport().getParams().get(2);

        Collection<SpecimenWrapper> allAliquots = getSpecimens();

        @SuppressWarnings("unchecked")
        Collection<SpecimenWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(
                aliquotPvProcessedBetween(after, before),
                aliquotTopContainerIdIn(topContainerIdList),
                aliquotSampleTypeNameShortLike(sampleTypeNameShort)));

        List<Object> expectedResults = new ArrayList<Object>();

        for (SpecimenWrapper aliquot : filteredAliquots) {
            expectedResults.add(aliquot.getWrappedObject());
        }

        return expectedResults;
    }

    private void checkResults(Collection<Integer> topContainerIds, Date after,
        Date before, String sampleTypeNameShort) throws Exception {
        getReport().setParams(
            Arrays.asList((Object) after, (Object) before,
                (Object) sampleTypeNameShort, (Object) NUM_RESULTS));
        getReport().setContainerList(StringUtils.join(topContainerIds, ","));

        checkResults(EnumSet.noneOf(CompareResult.class));
    }

    private static Predicate<SpecimenWrapper> aliquotSampleTypeNameShortLike(
        final String sampleTypeNameShort) {
        return new Predicate<SpecimenWrapper>() {
            public boolean evaluate(SpecimenWrapper aliquot) {
                return aliquot.getSpecimenType().getNameShort()
                    .equals(sampleTypeNameShort);
            }
        };
    }
}
