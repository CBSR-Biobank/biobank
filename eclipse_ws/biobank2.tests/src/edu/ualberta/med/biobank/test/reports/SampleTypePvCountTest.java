package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SampleTypePvCountTest extends AbstractReportTest {
    private static final Mapper<AliquotWrapper, List<Object>, Long> GROUP_BY_PV_AND_SAMPLE_TYPE = new Mapper<AliquotWrapper, List<Object>, Long>() {
        public List<Object> getKey(AliquotWrapper aliquot) {
            PatientVisitWrapper visit = aliquot.getPatientVisit();
            return Arrays.asList(new Object[] {
                visit.getPatient().getPnumber(), visit.getDateProcessed(),
                visit.getDateDrawn(), aliquot.getSampleType().getName() });
        }

        public Long getValue(AliquotWrapper aliquot, Long count) {
            return count == null ? new Long(1) : new Long(count + 1);
        }
    };
    private static final Comparator<List<Object>> ORDER_BY_PNUMBER_DATE_PROCESSED = new Comparator<List<Object>>() {
        public int compare(List<Object> lhs, List<Object> rhs) {
            String lhsPnumber = (String) lhs.get(0);
            String rhsPnumber = (String) rhs.get(0);

            int cmp = lhsPnumber.compareTo(rhsPnumber);
            if (cmp != 0) {
                return cmp;
            }

            Date lhsDateProcessed = (Date) lhs.get(1);
            Date rhsDateProcessed = (Date) rhs.get(1);

            return lhsDateProcessed.compareTo(rhsDateProcessed);
        }
    };

    @Test
    public void testResults() throws Exception {
        for (StudyWrapper study : getStudies()) {
            checkResults(study.getNameShort());
        }
    }

    @Test
    public void testEmptyStudyName() throws Exception {
        checkResults("");
    }

    @Override
    protected Collection<Object> getExpectedResults() {
        String studyNameShort = (String) getReport().getParams().get(0);

        Collection<AliquotWrapper> allAliquots = getAliquots();

        @SuppressWarnings("unchecked")
        Collection<AliquotWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(
                ALIQUOT_NOT_IN_SENT_SAMPLE_CONTAINER,
                aliquotStudyNameShortLike(studyNameShort),
                aliquotSite(isInSite(), getSiteId())));

        Map<List<Object>, Long> groupedData = MapperUtil.map(filteredAliquots,
            GROUP_BY_PV_AND_SAMPLE_TYPE);

        List<List<Object>> keys = new ArrayList<List<Object>>(
            groupedData.keySet());

        Collections.sort(keys, ORDER_BY_PNUMBER_DATE_PROCESSED);

        List<Object> expectedResults = new ArrayList<Object>();

        for (List<Object> key : keys) {
            List<Object> params = new ArrayList<Object>();
            params.addAll(key);
            params.add(groupedData.get(key));

            expectedResults.add(params.toArray());
        }

        return expectedResults;
    }

    private void checkResults(String studyNameShort)
        throws ApplicationException {
        getReport().setParams(Arrays.asList((Object) studyNameShort));
        checkResults(EnumSet.of(CompareResult.SIZE));
    }

    private static Predicate<AliquotWrapper> aliquotStudyNameShortLike(
        final String studyNameShort) {
        return new Predicate<AliquotWrapper>() {
            public boolean evaluate(AliquotWrapper aliquot) {
                return aliquot.getPatientVisit().getPatient().getStudy()
                    .getNameShort().equals(studyNameShort);
            }
        };
    }
}
