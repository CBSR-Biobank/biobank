package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;

public class SampleTypeSUsageTest extends AbstractReportTest {
    private static final Comparator<AliquotedSpecimenWrapper> ORDER_SS_BY_SAMPLE_TYPE_NAME_SHORT = new Comparator<AliquotedSpecimenWrapper>() {
        public int compare(AliquotedSpecimenWrapper lhs, AliquotedSpecimenWrapper rhs) {
            int cmp = compareStrings(lhs.getSpecimenType().getNameShort(), rhs
                .getSpecimenType().getNameShort());

            if (cmp != 0) {
                return cmp;
            }

            return compareStrings(lhs.getStudy().getNameShort(), rhs.getStudy()
                .getNameShort());
        }
    };
    private static final Comparator<SpecimenTypeWrapper> ORDER_ST_BY_SAMPLE_TYPE_NAME_SHORT = new Comparator<SpecimenTypeWrapper>() {
        public int compare(SpecimenTypeWrapper lhs, SpecimenTypeWrapper rhs) {
            return compareStrings(lhs.getNameShort(), rhs.getNameShort());
        }
    };

    @Test
    public void testResults() throws Exception {
        checkResults();
    }

    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        List<AliquotedSpecimenWrapper> allSampleStorages = new ArrayList<AliquotedSpecimenWrapper>(
            getAliquotedSpecimens());
        Collections.sort(allSampleStorages, ORDER_SS_BY_SAMPLE_TYPE_NAME_SHORT);

        List<Object> expectedResults = new ArrayList<Object>();

        final List<Integer> sampleTypeIdsInSs = new ArrayList<Integer>();

        for (AliquotedSpecimenWrapper ss : allSampleStorages) {
            sampleTypeIdsInSs.add(ss.getSpecimenType().getId());
            expectedResults
                .add(new Object[] { ss.getSpecimenType().getNameShort(),
                    ss.getStudy().getNameShort() });
        }

        // add sample types not in any study
        Collection<SpecimenTypeWrapper> sampleTypesNotInSs = PredicateUtil
            .filter(getSpecimenTypes(), new Predicate<SpecimenTypeWrapper>() {
                public boolean evaluate(SpecimenTypeWrapper type) {
                    return !sampleTypeIdsInSs.contains(type.getId());
                }
            });
        List<SpecimenTypeWrapper> sampleTypesNotInSsOrdered = new ArrayList<SpecimenTypeWrapper>(
            sampleTypesNotInSs);
        Collections.sort(sampleTypesNotInSsOrdered,
            ORDER_ST_BY_SAMPLE_TYPE_NAME_SHORT);

        for (SpecimenTypeWrapper sampleType : sampleTypesNotInSsOrdered) {
            expectedResults.add(new Object[] { sampleType.getNameShort(),
                "Unused" });
        }

        return expectedResults;
    }

    private void checkResults() throws Exception {
        checkResults(EnumSet.of(CompareResult.SIZE, CompareResult.ORDER));
    }
}
