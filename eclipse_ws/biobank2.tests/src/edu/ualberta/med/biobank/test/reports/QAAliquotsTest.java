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
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class QAAliquotsTest extends AbstractReportTest {
    // cheap way to get all results
    private static final Integer NUM_RESULTS = new Integer(Integer.MAX_VALUE);

    @Test
    public void testResults() throws Exception {
        for (SampleTypeWrapper sampleType : getSampleTypes()) {
            checkResults(getTopContainerIds(getContainers()), new Date(0),
                new Date(), sampleType.getNameShort());
        }
    }

    @Test
    public void testEmptyDateRange() throws Exception {
        for (SampleTypeWrapper sampleType : getSampleTypes()) {
            checkResults(getTopContainerIds(getContainers()), new Date(),
                new Date(0), sampleType.getNameShort());
        }
    }

    @Test
    public void testSmallDatePoint() throws Exception {
        List<AliquotWrapper> aliquots = getAliquots();
        Assert.assertTrue(aliquots.size() > 0);

        AliquotWrapper aliquot = aliquots.get(aliquots.size() / 2);
        PatientVisitWrapper visit = aliquot.getPatientVisit();
        checkResults(getTopContainerIds(getContainers()),
            visit.getDateProcessed(), visit.getDateProcessed(), aliquot
                .getSampleType().getNameShort());
    }

    @Test
    public void testSmallDateRange() throws Exception {
        List<AliquotWrapper> aliquots = getAliquots();
        Assert.assertTrue(aliquots.size() > 0);

        AliquotWrapper aliquot = aliquots.get(aliquots.size() / 2);
        PatientVisitWrapper visit = aliquot.getPatientVisit();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(visit.getDateProcessed());
        calendar.add(Calendar.HOUR_OF_DAY, 24);

        checkResults(getTopContainerIds(getContainers()),
            visit.getDateProcessed(), calendar.getTime(), aliquot
                .getSampleType().getNameShort());
    }

    @Override
    protected Collection<Object> getExpectedResults() {
        String topContainerIdList = getReport().getContainerList();
        Date after = (Date) getReport().getParams().get(0);
        Date before = (Date) getReport().getParams().get(1);
        String sampleTypeNameShort = (String) getReport().getParams().get(2);

        Collection<AliquotWrapper> allAliquots = getAliquots();

        @SuppressWarnings("unchecked")
        Collection<AliquotWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(
                aliquotPvProcessedBetween(after, before),
                aliquotSite(isInSite(), getSiteId()),
                aliquotTopContainerIdIn(topContainerIdList),
                aliquotSampleTypeNameShortLike(sampleTypeNameShort)));

        List<Object> expectedResults = new ArrayList<Object>();

        for (AliquotWrapper aliquot : filteredAliquots) {
            expectedResults.add(aliquot.getWrappedObject());
        }

        return expectedResults;
    }

    private void checkResults(Collection<Integer> topContainerIds, Date after,
        Date before, String sampleTypeNameShort) throws ApplicationException {
        getReport().setParams(
            Arrays.asList((Object) after, (Object) before,
                (Object) sampleTypeNameShort, (Object) NUM_RESULTS));
        getReport().setContainerList(StringUtils.join(topContainerIds, ","));

        checkResults(EnumSet.noneOf(CompareResult.class));
    }

    private static Predicate<AliquotWrapper> aliquotSampleTypeNameShortLike(
        final String sampleTypeNameShort) {
        return new Predicate<AliquotWrapper>() {
            public boolean evaluate(AliquotWrapper aliquot) {
                return aliquot.getSampleType().getNameShort()
                    .equals(sampleTypeNameShort);
            }
        };
    }
}
