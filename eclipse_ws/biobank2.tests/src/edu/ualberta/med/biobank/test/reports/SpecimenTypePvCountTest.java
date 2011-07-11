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

import edu.ualberta.med.biobank.common.util.DateCompare;
import edu.ualberta.med.biobank.common.util.Mapper;
import edu.ualberta.med.biobank.common.util.MapperUtil;
import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class SpecimenTypePvCountTest extends AbstractReportTest {
    private static final Mapper<SpecimenWrapper, List<Object>, Long> GROUP_BY_PV_AND_SAMPLE_TYPE = new Mapper<SpecimenWrapper, List<Object>, Long>() {
        public List<Object> getKey(SpecimenWrapper aliquot) {
            ProcessingEventWrapper pevent = aliquot.getProcessingEvent();
            SpecimenWrapper parentSpc = aliquot.getParentSpecimen();
            return Arrays.asList(new Object[] { pevent.getId(),
                parentSpc.getCollectionEvent().getPatient().getPnumber(),
                pevent.getCreatedAt(), parentSpc.getCreatedAt(),
                aliquot.getSpecimenType().getName() });
        }

        public Long getValue(SpecimenWrapper aliquot, Long count) {
            return count == null ? new Long(1) : new Long(count + 1);
        }
    };
    private static final Comparator<List<Object>> ORDER_BY_PNUMBER_DATE_PROCESSED = new Comparator<List<Object>>() {
        public int compare(List<Object> lhs, List<Object> rhs) {
            String lhsPnumber = (String) lhs.get(1);
            String rhsPnumber = (String) rhs.get(1);

            int cmp = compareStrings(lhsPnumber, rhsPnumber);
            if (cmp != 0) {
                return cmp;
            }

            Date lhsDateProcessed = (Date) lhs.get(2);
            Date rhsDateProcessed = (Date) rhs.get(2);

            return DateCompare.compare(rhsDateProcessed, lhsDateProcessed);
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
    protected Collection<Object> getExpectedResults() throws Exception {
        String studyNameShort = (String) getReport().getParams().get(0);

        Collection<SpecimenWrapper> allAliquots = getSpecimens();

        @SuppressWarnings("unchecked")
        Collection<SpecimenWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(
                ALIQUOT_NOT_IN_SENT_SAMPLE_CONTAINER,
                aliquotStudyNameShortLike(studyNameShort),
                aliquotSite(isInSite(), getSiteId()), ALIQUOT_HAS_POSITION));

        Map<List<Object>, Long> groupedData = MapperUtil.map(filteredAliquots,
            GROUP_BY_PV_AND_SAMPLE_TYPE);

        List<List<Object>> keys = new ArrayList<List<Object>>(
            groupedData.keySet());

        Collections.sort(keys, ORDER_BY_PNUMBER_DATE_PROCESSED);

        List<Object> expectedResults = new ArrayList<Object>();

        for (List<Object> key : keys) {
            List<Object> params = new ArrayList<Object>();
            params.add(key.get(1));
            params.add(key.get(2));
            params.add(key.get(3));
            params.add(key.get(4));
            params.add(groupedData.get(key));

            expectedResults.add(params.toArray());
        }

        return expectedResults;
    }

    private void checkResults(String studyNameShort) throws Exception {
        getReport().setParams(Arrays.asList((Object) studyNameShort));
        checkResults(EnumSet.of(CompareResult.SIZE));
    }

    private static Predicate<SpecimenWrapper> aliquotStudyNameShortLike(
        final String studyNameShort) {
        return new Predicate<SpecimenWrapper>() {
            public boolean evaluate(SpecimenWrapper aliquot) {
                return aliquot.getCollectionEvent().getPatient().getStudy()
                    .getNameShort().equals(studyNameShort);
            }
        };
    }
}
