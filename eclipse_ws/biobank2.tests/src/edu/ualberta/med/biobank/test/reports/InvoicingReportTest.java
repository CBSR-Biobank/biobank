package edu.ualberta.med.biobank.test.reports;

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

import edu.ualberta.med.biobank.common.util.CollectionsUtil;
import edu.ualberta.med.biobank.common.util.Mapper;
import edu.ualberta.med.biobank.common.util.MapperUtil;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;

public class InvoicingReportTest extends AbstractReportTest {
    private static final Mapper<SpecimenWrapper, List<String>, Long> GROUP_ALIQUOTS_BY_STUDY_CLINIC_SAMPLE_TYPE = new Mapper<SpecimenWrapper, List<String>, Long>() {
        public List<String> getKey(SpecimenWrapper aliquot) {
            return Arrays.asList(aliquot.getCollectionEvent().getPatient()
                .getStudy().getNameShort(), aliquot.getProcessingEvent()
                .getCenter().getNameShort(), aliquot.getSpecimenType()
                .getNameShort());
        }

        public Long getValue(SpecimenWrapper aliquot, Long aliquotCount) {
            return aliquotCount == null ? new Long(1) : new Long(
                aliquotCount + 1);
        }
    };
    private static final Mapper<ProcessingEventWrapper, List<String>, Long> GROUP_PVS_BY_STUDY_CLINIC = new Mapper<ProcessingEventWrapper, List<String>, Long>() {
        public List<String> getKey(ProcessingEventWrapper pevent) {
            return Arrays.asList(pevent.getCenter().getNameShort(), pevent
                .getCenter().getNameShort());
        }

        public Long getValue(ProcessingEventWrapper patientVisit, Long pvCount) {
            return pvCount == null ? new Long(1) : new Long(pvCount + 1);
        }
    };
    private static final Comparator<List<String>> ORDER_STUDY_CLINIC_SAMPLE = new Comparator<List<String>>() {
        public int compare(List<String> lhs, List<String> rhs) {
            return CollectionsUtil.compare(lhs, rhs, new Comparator<String>() {
                public int compare(String lhs, String rhs) {
                    return compareStrings(lhs, rhs);
                }
            });
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
        // consider a 2 day range since patient visits are often processed the
        // following day
        calendar.add(Calendar.DAY_OF_YEAR, 2);

        checkResults(aliquot.getCreatedAt(), calendar.getTime());
    }

    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        Date processedAndLinkedAfter = (Date) getReport().getParams().get(0);
        Date processedAndLinkedBefore = (Date) getReport().getParams().get(1);

        Collection<SpecimenWrapper> allAliquots = getSpecimens();
        @SuppressWarnings("unchecked")
        Collection<SpecimenWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(
                aliquotSite(isInSite(), getSiteId()), AbstractReportTest
                    .aliquotLinkedBetween(processedAndLinkedAfter,
                        processedAndLinkedBefore),
                ALIQUOT_NOT_IN_SENT_SAMPLE_CONTAINER));
        Map<List<String>, Long> groupedAliquots = MapperUtil.map(
            filteredAliquots, GROUP_ALIQUOTS_BY_STUDY_CLINIC_SAMPLE_TYPE);

        Collection<ProcessingEventWrapper> allPatientVisits = getPatientVisits();
        Collection<ProcessingEventWrapper> filteredPatientVisits = PredicateUtil
            .filter(
                allPatientVisits,
                patientVisitProcessedBetween(processedAndLinkedAfter,
                    processedAndLinkedBefore));
        Map<List<String>, Long> groupedPatientVisits = MapperUtil.map(
            filteredPatientVisits, GROUP_PVS_BY_STUDY_CLINIC);

        List<Object> expectedResults = new ArrayList<Object>();

        List<List<String>> keys = new ArrayList<List<String>>(
            groupedAliquots.keySet());
        Collections.sort(keys, ORDER_STUDY_CLINIC_SAMPLE);

        for (List<String> key : keys) {
            Long aliquotCount = groupedAliquots.get(key);
            List<String> studyAndClinic = key.subList(0, 2);
            Long pvCount = groupedPatientVisits.get(studyAndClinic);

            if (pvCount == null) {
                // There might not be any patient visits with a date processed
                // in the same range
                pvCount = new Long(0);
            }

            List<Object> objects = new ArrayList<Object>();
            objects.add(key.get(0));
            objects.add(key.get(1));
            objects.add(pvCount);
            objects.add(key.get(2));
            objects.add(aliquotCount);

            expectedResults.add(objects.toArray());
        }

        return expectedResults;
    }

    private void checkResults(Date after, Date before) throws Exception {
        getReport().setParams(Arrays.asList((Object) after, (Object) before));
        checkResults(EnumSet.of(CompareResult.SIZE));
    }
}
