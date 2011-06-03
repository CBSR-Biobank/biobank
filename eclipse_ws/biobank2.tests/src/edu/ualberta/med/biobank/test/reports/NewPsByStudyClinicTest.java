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
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;

public class NewPsByStudyClinicTest extends AbstractReportTest {
    private static final Mapper<ProcessingEventWrapper, Integer, ProcessingEventWrapper> GROUP_BY_PATIENT = new Mapper<ProcessingEventWrapper, Integer, ProcessingEventWrapper>() {
        public Integer getKey(ProcessingEventWrapper pevent) {
            return pevent.getSpecimenCollection(false).get(0)
                .getCollectionEvent().getPatient().getId();
        }

        public ProcessingEventWrapper getValue(ProcessingEventWrapper newPv,
            ProcessingEventWrapper oldPv) {
            if (oldPv == null) {
                return newPv;
            }

            return oldPv.getCreatedAt().before(newPv.getCreatedAt()) ? oldPv
                : newPv;
        }
    };

    public static Mapper<ProcessingEventWrapper, List<Object>, Long> groupPvsByStudyAndClinicAndDateField(
        final String dateField) {
        final Calendar calendar = Calendar.getInstance();
        return new Mapper<ProcessingEventWrapper, List<Object>, Long>() {
            public List<Object> getKey(ProcessingEventWrapper pevent) {
                calendar.setTime(pevent.getCreatedAt());

                List<Object> key = new ArrayList<Object>();
                key.add(pevent.getCenter().getNameShort());
                key.add(pevent.getCenter().getName());
                key.add(new Integer(calendar.get(Calendar.YEAR)));
                key.add(new Long(getDateFieldValue(calendar, dateField)));

                return key;
            }

            public Long getValue(ProcessingEventWrapper type, Long pvCount) {
                return pvCount == null ? new Long(1) : new Long(pvCount + 1);
            }
        };
    }

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
    public void testSecondPatientVisitDateRange() throws Exception {
        for (PatientWrapper patient : getPatients()) {
            List<ProcessingEventWrapper> visits = patient
                .getProcessingEventCollection(false);
            if (visits.size() >= 2) {
                ProcessingEventWrapper visit = visits.get(1);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(visit.getCreatedAt());
                calendar.add(Calendar.HOUR_OF_DAY, 24);

                checkResults(visit.getCreatedAt(), calendar.getTime());
                return;
            }
        }

        Assert.fail("no patient with 2 or more patient visits");
    }

    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        String groupByDateField = getReport().getGroupBy();
        Date after = (Date) getReport().getParams().get(0);
        Date before = (Date) getReport().getParams().get(1);

        Collection<ProcessingEventWrapper> allPatientVisits = getPatientVisits();

        Collection<ProcessingEventWrapper> firstPatientVisits = MapperUtil.map(
            allPatientVisits, GROUP_BY_PATIENT).values();

        Collection<ProcessingEventWrapper> filteredPatientVisits = PredicateUtil
            .filter(firstPatientVisits,
                patientVisitProcessedBetween(after, before));

        Map<List<Object>, Long> groupedData = MapperUtil.map(
            filteredPatientVisits,
            groupPvsByStudyAndClinicAndDateField(groupByDateField));

        List<Object> expectedResults = new ArrayList<Object>();

        for (Map.Entry<List<Object>, Long> entry : groupedData.entrySet()) {
            List<Object> data = new ArrayList<Object>();
            data.addAll(entry.getKey());
            data.add(entry.getValue());

            expectedResults.add(data.toArray());
        }

        return expectedResults;
    }

    private void checkResults(Date after, Date before) throws Exception {
        getReport().setParams(Arrays.asList((Object) after, (Object) before));

        for (String dateField : DATE_FIELDS) {
            // check the results against each possible date field
            getReport().setGroupBy(dateField);

            checkResults(EnumSet.of(CompareResult.SIZE));
        }
    }
}
