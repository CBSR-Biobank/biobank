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
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.server.reports.AbstractReport;

public class FTAReportTest extends AbstractReportTest {
    private static final Predicate<AliquotWrapper> ALIQUOT_FTA_SAMPLE_TYPE = new Predicate<AliquotWrapper>() {
        public boolean evaluate(AliquotWrapper aliquot) {
            return aliquot.getSampleType().getNameShort()
                .equals(AbstractReport.FTA_CARD_SAMPLE_TYPE_NAME);
        }
    };
    private static final Mapper<PatientVisitWrapper, String, PatientVisitWrapper> GROUP_PATIENT_VISITS_BY_PNUMBER = new Mapper<PatientVisitWrapper, String, PatientVisitWrapper>() {
        public String getKey(PatientVisitWrapper patientVisit) {
            return patientVisit.getPatient().getPnumber();
        }

        public PatientVisitWrapper getValue(PatientVisitWrapper patientVisit,
            PatientVisitWrapper oldValue) {
            // keep the earliest patient visit (according to date processed)
            return (oldValue == null)
                || patientVisit.getDateProcessed().before(
                    oldValue.getDateProcessed()) ? patientVisit : oldValue;
        }
    };
    private static final Mapper<AliquotWrapper, String, AliquotWrapper> GROUP_ALIQUOTS_BY_PNUMBER = new Mapper<AliquotWrapper, String, AliquotWrapper>() {
        public String getKey(AliquotWrapper aliquot) {
            return aliquot.getPatientVisit().getPatient().getPnumber();
        }

        public AliquotWrapper getValue(AliquotWrapper aliquot,
            AliquotWrapper oldValue) {
            // keep the earliest patient visit (according to date processed)
            return (oldValue == null) || (aliquot.getId() < oldValue.getId()) ? aliquot
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
    private static final Mapper<AliquotWrapper, String, AliquotWrapper> GROUP_ALIQUOTS_BY_PNUMBER_OLD = new Mapper<AliquotWrapper, String, AliquotWrapper>() {
        public String getKey(AliquotWrapper aliquot) {
            return aliquot.getPatientVisit().getPatient().getPnumber();
        }

        public AliquotWrapper getValue(AliquotWrapper aliquot,
            AliquotWrapper oldValue) {
            // keep aliquots with the earliest patient visit date processed and
            // the smallest aliquot id
            if (oldValue == null) {
                return aliquot;
            } else {
                if (aliquot.getPatientVisit().getDateProcessed()
                    .equals(oldValue.getPatientVisit().getDateProcessed())) {
                    if (aliquot.getId() > oldValue.getId()) {
                        return oldValue;
                    } else {
                        return aliquot;
                    }
                } else if (aliquot.getPatientVisit().getDateProcessed()
                    .after(oldValue.getPatientVisit().getDateProcessed())) {
                    return oldValue;
                } else {
                    return aliquot;
                }
            }
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
        List<PatientVisitWrapper> patientVisits;

        for (StudyWrapper study : getStudies()) {
            for (PatientWrapper patient : study.getPatientCollection()) {
                patientVisits = patient.getPatientVisitCollection(true, true);
                if (patientVisits.size() > 0) {
                    // check before, on, and after each patient's first patient
                    // visit
                    calendar.setTime(patientVisits.get(0).getDateProcessed());
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

        Predicate<PatientVisitWrapper> patientInStudy = new Predicate<PatientVisitWrapper>() {
            public boolean evaluate(PatientVisitWrapper patientVisit) {
                return patientVisit.getPatient().getStudy().getNameShort()
                    .equals(studyNameShort);
            }
        };

        Predicate<AliquotWrapper> pvProcessedAfter = new Predicate<AliquotWrapper>() {
            public boolean evaluate(AliquotWrapper aliquot) {
                return aliquot.getPatientVisit().getDateProcessed()
                    .after(firstPvDateProcessed);
            }
        };

        Collection<PatientVisitWrapper> allPatientVisits = getPatientVisits();
        Collection<PatientVisitWrapper> filteredPatientVisits = PredicateUtil
            .filter(allPatientVisits, patientInStudy);
        Map<String, PatientVisitWrapper> groupedPatientVisits = MapperUtil.map(
            filteredPatientVisits, GROUP_PATIENT_VISITS_BY_PNUMBER);

        Collection<AliquotWrapper> allAliquots = getAliquots();
        @SuppressWarnings("unchecked")
        Collection<AliquotWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(ALIQUOT_FTA_SAMPLE_TYPE,
                pvProcessedAfter, ALIQUOT_HAS_POSITION));
        Map<String, AliquotWrapper> groupedAliquots = MapperUtil.map(
            filteredAliquots, GROUP_ALIQUOTS_BY_PNUMBER);
        List<AliquotWrapper> filteredAndGroupedAliquots = new ArrayList<AliquotWrapper>(
            groupedAliquots.values());

        Collections.sort(filteredAndGroupedAliquots, ORDER_ALIQUOT_BY_PNUMBER);

        List<Object> expectedResults = new ArrayList<Object>();

        for (AliquotWrapper aliquot : filteredAndGroupedAliquots) {
            for (PatientVisitWrapper patientVisit : groupedPatientVisits
                .values()) {
                if (patientVisit.getId().equals(
                    aliquot.getPatientVisit().getId())) {
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
