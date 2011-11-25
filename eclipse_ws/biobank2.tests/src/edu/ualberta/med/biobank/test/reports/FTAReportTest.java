package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Mapper;
import edu.ualberta.med.biobank.common.util.MapperUtil;
import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.server.reports.AbstractReport;

public class FTAReportTest extends AbstractReportTest {
    private static final Predicate<SpecimenWrapper> ALIQUOT_FTA_SAMPLE_TYPE =
        new Predicate<SpecimenWrapper>() {
            @Override
            public boolean evaluate(SpecimenWrapper aliquot) {
                return aliquot.getSpecimenType().getNameShort()
                    .equals(AbstractReport.FTA_CARD_SAMPLE_TYPE_NAME);
            }
        };
    private static final Mapper<ProcessingEventWrapper, String, ProcessingEventWrapper> GROUP_PATIENT_VISITS_BY_PNUMBER =
        new Mapper<ProcessingEventWrapper, String, ProcessingEventWrapper>() {
            @Override
            public String getKey(ProcessingEventWrapper pevent) {
                return pevent.getSpecimenCollection(false).get(0)
                    .getCollectionEvent().getPatient().getPnumber();
            }

            @Override
            public ProcessingEventWrapper getValue(
                ProcessingEventWrapper pevent,
                ProcessingEventWrapper oldValue) {
                // keep the earliest patient visit (according to date processed)
                return (oldValue == null)
                    || pevent.getCreatedAt().before(oldValue.getCreatedAt()) ? pevent
                    : oldValue;
            }
        };
    private static final Mapper<SpecimenWrapper, String, SpecimenWrapper> GROUP_ALIQUOTS_BY_PNUMBER =
        new Mapper<SpecimenWrapper, String, SpecimenWrapper>() {
            @Override
            public String getKey(SpecimenWrapper aliquot) {
                return aliquot.getCollectionEvent().getPatient().getPnumber();
            }

            @Override
            public SpecimenWrapper getValue(SpecimenWrapper aliquot,
                SpecimenWrapper oldValue) {
                // keep the earliest patient visit (according to date processed)
                return (oldValue == null)
                    || (aliquot.getId() < oldValue.getId()) ? aliquot
                    : oldValue;
            }
        };

    /**
     * Useful if only considering PatientVisit-s with aliquots, otherwise this
     * is an incorrect approach.
     * 
     * @deprecated
     */
    @SuppressWarnings("unused")
    @Deprecated
    private static final Mapper<SpecimenWrapper, String, SpecimenWrapper> GROUP_ALIQUOTS_BY_PNUMBER_OLD =
        new Mapper<SpecimenWrapper, String, SpecimenWrapper>() {
            @Override
            public String getKey(SpecimenWrapper aliquot) {
                return aliquot.getCollectionEvent().getPatient().getPnumber();
            }

            @Override
            public SpecimenWrapper getValue(SpecimenWrapper aliquot,
                SpecimenWrapper oldValue) {
                // keep aliquots with the earliest patient visit date processed
                // and
                // the smallest aliquot id
                if (oldValue == null) {
                    return aliquot;
                }

                if (aliquot.getProcessingEvent().getCreatedAt()
                    .equals(oldValue.getProcessingEvent().getCreatedAt())) {
                    if (aliquot.getId() > oldValue.getId()) {
                        return oldValue;
                    }
                    return aliquot;
                } else if (aliquot.getProcessingEvent().getCreatedAt()
                    .after(oldValue.getProcessingEvent().getCreatedAt())) {
                    return oldValue;
                }
                return aliquot;
            }
        };

    @Test
    public void testResults() throws Exception {
        for (StudyWrapper study : getStudies()) {
            checkResults(study.getNameShort(), new Date(0));
        }
    }

    @Test
    public void testCurrentDate() throws Exception {
        for (StudyWrapper study : getStudies()) {
            checkResults(study.getNameShort(), new Date());
        }
    }

    @Test
    public void testMiddleDates() throws Exception {
        Calendar calendar = Calendar.getInstance();
        List<ProcessingEventWrapper> pevents;

        for (StudyWrapper study : getStudies()) {
            for (PatientWrapper patient : study.getPatientCollection(false)) {
                pevents = patient.getProcessingEventCollection(false);
                if ((pevents != null) && !pevents.isEmpty()) {
                    // check before, on, and after each patient's first patient
                    // visit
                    calendar.setTime(pevents.get(0).getCreatedAt());
                    checkResults(study.getNameShort(), calendar.getTime());
                    calendar.add(Calendar.MINUTE, -1);
                    checkResults(study.getNameShort(), calendar.getTime());
                    calendar.add(Calendar.MINUTE, 2);
                    checkResults(study.getNameShort(), calendar.getTime());
                }
            }
        }
    }

    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        final String studyNameShort = (String) getReport().getParams().get(0);
        final Date firstPvDateProcessed = (Date) getReport().getParams().get(1);

        Predicate<ProcessingEventWrapper> patientInStudy =
            new Predicate<ProcessingEventWrapper>() {
                @Override
                public boolean evaluate(ProcessingEventWrapper pevent) {
                    return pevent.getCenter().getNameShort()
                        .equals(studyNameShort);
                }
            };

        Predicate<SpecimenWrapper> pvProcessedAfter =
            new Predicate<SpecimenWrapper>() {
                @Override
                public boolean evaluate(SpecimenWrapper aliquot) {
                    return aliquot.getProcessingEvent().getCreatedAt()
                        .after(firstPvDateProcessed);
                }
            };

        Collection<ProcessingEventWrapper> allPatientVisits =
            getPatientVisits();
        Collection<ProcessingEventWrapper> filteredPatientVisits =
            PredicateUtil
                .filter(allPatientVisits, patientInStudy);
        Map<String, ProcessingEventWrapper> groupedPatientVisits = MapperUtil
            .map(filteredPatientVisits, GROUP_PATIENT_VISITS_BY_PNUMBER);

        Collection<SpecimenWrapper> allAliquots = getSpecimens();
        @SuppressWarnings("unchecked")
        Collection<SpecimenWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(ALIQUOT_FTA_SAMPLE_TYPE,
                pvProcessedAfter, ALIQUOT_HAS_POSITION));
        Map<String, SpecimenWrapper> groupedAliquots = MapperUtil.map(
            filteredAliquots, GROUP_ALIQUOTS_BY_PNUMBER);
        List<SpecimenWrapper> filteredAndGroupedAliquots =
            new ArrayList<SpecimenWrapper>(
                groupedAliquots.values());

        Collections.sort(filteredAndGroupedAliquots, ORDER_ALIQUOT_BY_PNUMBER);

        List<Object> expectedResults = new ArrayList<Object>();

        for (SpecimenWrapper aliquot : filteredAndGroupedAliquots) {
            for (ProcessingEventWrapper patientVisit : groupedPatientVisits
                .values()) {
                if (patientVisit.getId().equals(
                    aliquot.getProcessingEvent().getId())) {
                    expectedResults.add(aliquot.getId());
                }
            }
        }

        return expectedResults;
    }

    private void checkResults(String studyNameShort, Date firstPvDateProcessed)
        throws Exception {
        getReport().setParams(
            Arrays.asList((Object) studyNameShort,
                (Object) firstPvDateProcessed));

        checkResults(EnumSet.of(CompareResult.SIZE, CompareResult.ORDER));
    }
}
