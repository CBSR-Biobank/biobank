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

import edu.ualberta.med.biobank.common.util.MapperUtil;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;

public class NewPVsByStudyClinicTest extends AbstractReportTest {
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
        String groupByDateField = getReport().getGroupBy();
        Date after = (Date) getReport().getParams().get(0);
        Date before = (Date) getReport().getParams().get(1);

        Collection<ProcessingEventWrapper> allPatientVisits = getPatientVisits();

        Collection<ProcessingEventWrapper> filteredPatientVisits = PredicateUtil
            .filter(allPatientVisits, PredicateUtil.andPredicate(
                patientVisitProcessedBetween(after, before),
                patientVisitSite(isInSite(), getSiteId())));

        Map<List<Object>, Long> groupedData = MapperUtil.map(
            filteredPatientVisits, NewPsByStudyClinicTest
                .groupPvsByStudyAndClinicAndDateField(groupByDateField));

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
