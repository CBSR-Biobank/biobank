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
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.Mapper;
import edu.ualberta.med.biobank.common.util.MapperUtil;
import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientVisitSummaryTest {
    private static final Mapper<PatientVisitWrapper, List<String>, PvCount> PV_COUNT_BY_STUDY_CLINIC_PATIENT = new Mapper<PatientVisitWrapper, List<String>, PvCount>() {
        public List<String> getKey(PatientVisitWrapper patientVisit) {
            return Arrays.asList(patientVisit.getPatient().getStudy()
                .getNameShort(), patientVisit.getShipment().getClinic()
                .getNameShort(), patientVisit.getPatient().getPnumber());
        }

        public PvCount getValue(PatientVisitWrapper patientVisit,
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

    private static final Comparator<List<String>> PV_STATS_COMPARATOR = new Comparator<List<String>>() {
        public int compare(List<String> lhs, List<String> rhs) {
            int comparedStudyName = lhs.get(0).compareTo(rhs.get(0));
            int comparedClinicName = lhs.get(1).compareTo(rhs.get(1));
            return comparedStudyName != 0 ? comparedStudyName
                : comparedClinicName;
        }
    };

    private Collection<Object> getExpectedResults(final Date after,
        final Date before) {
        Predicate<PatientVisitWrapper> betweenDates = new Predicate<PatientVisitWrapper>() {
            public boolean evaluate(PatientVisitWrapper pv) {
                return (pv.getDateProcessed().after(after) || pv
                    .getDateProcessed().equals(after))
                    && (pv.getDateProcessed().before(before) || pv
                        .getDateProcessed().equals(before));
            }
        };

        Collection<PatientVisitWrapper> patientVisits = TestReports
            .getInstance().getPatientVisits();

        Collection<PatientVisitWrapper> filteredPatientVisits = PredicateUtil
            .filter(patientVisits, betweenDates);

        Map<List<String>, PvCount> pvCountByStudyClincPatient = MapperUtil.map(
            filteredPatientVisits, PV_COUNT_BY_STUDY_CLINIC_PATIENT);

        Map<List<String>, List<Long>> pvStatsByStudyClinic = MapperUtil.map(
            pvCountByStudyClincPatient.values(), PV_STATS_BY_STUDY_CLINIC);

        List<List<String>> keys = new ArrayList<List<String>>(
            pvStatsByStudyClinic.keySet());
        Collections.sort(keys, PV_STATS_COMPARATOR);

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

    private BiobankReport getReport(Date after, Date before) {
        BiobankReport report = BiobankReport
            .getReportByName("PatientVisitSummary");
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
        List<PatientVisitWrapper> patientVisits = TestReports.getInstance()
            .getPatientVisits();
        Assert.assertTrue(patientVisits.size() > 0);

        PatientVisitWrapper patientVisit = patientVisits.get(patientVisits
            .size() / 2);

        checkReport(patientVisit.getDateProcessed(),
            patientVisit.getDateProcessed());
    }

    @Test
    public void testSmallDateRange() throws Exception {
        List<PatientVisitWrapper> patientVisits = TestReports.getInstance()
            .getPatientVisits();
        Assert.assertTrue(patientVisits.size() > 0);

        PatientVisitWrapper patientVisit = patientVisits.get(patientVisits
            .size() / 2);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(patientVisit.getDateProcessed());
        calendar.add(Calendar.HOUR_OF_DAY, 24);

        checkReport(patientVisit.getDateProcessed(), calendar.getTime());
    }
}
