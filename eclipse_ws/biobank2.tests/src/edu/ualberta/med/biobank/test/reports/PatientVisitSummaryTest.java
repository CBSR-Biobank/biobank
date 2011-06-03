package edu.ualberta.med.biobank.test.reports;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Mapper;
import edu.ualberta.med.biobank.common.util.MapperUtil;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;

public class PatientVisitSummaryTest extends AbstractReportTest {
    private static final Mapper<ProcessingEventWrapper, List<String>, PvCount> PV_COUNT_BY_STUDY_CLINIC_PATIENT = new Mapper<ProcessingEventWrapper, List<String>, PvCount>() {
        public List<String> getKey(ProcessingEventWrapper pevent) {
            return Arrays.asList(pevent.getCenter().getNameShort(), pevent
                .getCenter().getNameShort(), pevent
                .getSpecimenCollection(false).get(0).getCollectionEvent()
                .getPatient().getPnumber());
        }

        public PvCount getValue(ProcessingEventWrapper patientVisit,
            PvCount pvCount) {
            if (pvCount != null) {
                pvCount.count = new Long(pvCount.count + 1);
            } else {
                pvCount = new PvCount();
                pvCount.key = getKey(patientVisit);
                pvCount.count = new Long(1);
            }
            return pvCount;
        }
    };

    private static class PvCount {
        List<String> key;
        Long count;
    }

    private static final Mapper<PvCount, List<String>, List<Long>> PV_STATS_BY_STUDY_CLINIC = new Mapper<PvCount, List<String>, List<Long>>() {
        public List<String> getKey(PvCount pvCount) {
            return pvCount.key.subList(0, 2); // study, clinic
        }

        public List<Long> getValue(PvCount pvCount, List<Long> stats) {
            if (stats == null) {
                Long zero = new Long(0);
                stats = Arrays.asList(zero, zero, zero, zero, zero, zero, zero);
            }

            if ((pvCount.count >= 1) && (pvCount.count <= 4)) {
                int index = pvCount.count.intValue() - 1;
                stats.set(index, stats.get(index) + 1);
            } else if (pvCount.count >= 5) {
                stats.set(4, stats.get(4) + 1);
            }

            stats.set(5, stats.get(5) + pvCount.count);
            stats.set(6, stats.get(6) + 1);

            return stats;
        }
    };

    private static final Comparator<List<String>> ORDER_BY_STUDY_CLINIC = new Comparator<List<String>>() {
        public int compare(List<String> lhs, List<String> rhs) {
            int cmp = compareStrings(lhs.get(0), rhs.get(0)); // clinic name

            if (cmp != 0) {
                return cmp;
            }

            return compareStrings(lhs.get(1), rhs.get(1)); // study name
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
        List<ProcessingEventWrapper> patientVisits = getPatientVisits();
        Assert.assertTrue(patientVisits.size() > 0);

        ProcessingEventWrapper patientVisit = patientVisits.get(patientVisits
            .size() / 2);

        checkResults(patientVisit.getCreatedAt(), patientVisit.getCreatedAt());
    }

    @Test
    public void testSmallDateRange() throws Exception {
        List<ProcessingEventWrapper> patientVisits = getPatientVisits();
        Assert.assertTrue(patientVisits.size() > 0);

        ProcessingEventWrapper patientVisit = patientVisits.get(patientVisits
            .size() / 2);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(patientVisit.getCreatedAt());
        calendar.add(Calendar.HOUR_OF_DAY, 24);

        checkResults(patientVisit.getCreatedAt(), calendar.getTime());
    }

    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        Date after = (Date) getReport().getParams().get(0);
        Date before = (Date) getReport().getParams().get(1);

        Collection<ProcessingEventWrapper> patientVisits = getPatientVisits();

        Collection<ProcessingEventWrapper> filteredPatientVisits = PredicateUtil
            .filter(patientVisits, PredicateUtil.andPredicate(
                patientVisitProcessedBetween(after, before),
                patientVisitSite(isInSite(), getSiteId())));

        Map<List<String>, PvCount> pvCountByStudyClincPatient = MapperUtil.map(
            filteredPatientVisits, PV_COUNT_BY_STUDY_CLINIC_PATIENT);

        Map<List<String>, List<Long>> pvStatsByStudyClinic = MapperUtil.map(
            pvCountByStudyClincPatient.values(), PV_STATS_BY_STUDY_CLINIC);

        List<List<String>> keys = new ArrayList<List<String>>(
            pvStatsByStudyClinic.keySet());
        Collections.sort(keys, ORDER_BY_STUDY_CLINIC);

        List<Object> expectedResults = new ArrayList<Object>();

        for (List<String> key : keys) {
            List<Object> objects = new ArrayList<Object>(key);
            List<Long> stats = pvStatsByStudyClinic.get(key);

            objects.add(new BigDecimal(stats.get(0)));
            objects.add(new BigDecimal(stats.get(1)));
            objects.add(new BigDecimal(stats.get(2)));
            objects.add(new BigDecimal(stats.get(3)));
            objects.add(new BigDecimal(stats.get(4)));
            objects.add(new BigDecimal(stats.get(5)));
            objects.add(BigInteger.valueOf(stats.get(6)));

            expectedResults.add(objects.toArray());
        }

        return expectedResults;
    }

    @Override
    protected PostProcessTester getPostProcessTester() {
        return new PatientVisitSummaryPostProcessTester();
    }

    private void checkResults(Date after, Date before) throws Exception {
        getReport().setParams(Arrays.asList((Object) after, (Object) before));

        checkResults(EnumSet.of(CompareResult.SIZE));
    }
}
