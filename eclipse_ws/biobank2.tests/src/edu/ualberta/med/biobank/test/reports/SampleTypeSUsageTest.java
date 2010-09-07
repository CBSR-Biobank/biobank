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
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SampleTypeSUsageTest extends AbstractReportTest {
    private static final Comparator<SampleStorageWrapper> ORDER_SS_BY_SAMPLE_TYPE_NAME_SHORT = new Comparator<SampleStorageWrapper>() {
        public int compare(SampleStorageWrapper lhs, SampleStorageWrapper rhs) {
            return lhs.getSampleType().getNameShort()
                .compareTo(rhs.getSampleType().getNameShort());
        }
    };
    private static final Comparator<SampleTypeWrapper> ORDER_ST_BY_SAMPLE_TYPE_NAME_SHORT = new Comparator<SampleTypeWrapper>() {
        public int compare(SampleTypeWrapper lhs, SampleTypeWrapper rhs) {
            return lhs.getNameShort().compareTo(rhs.getNameShort());
        }
    };

    @Test
    public void testResults() throws Exception {
        checkResults();
    }

    @Override
    protected Collection<Object> getExpectedResults() {
        List<SampleStorageWrapper> allSampleStorages = new ArrayList<SampleStorageWrapper>(
            getSampleStorages());
        Collections.sort(allSampleStorages, ORDER_SS_BY_SAMPLE_TYPE_NAME_SHORT);

        List<Object> expectedResults = new ArrayList<Object>();

        final List<Integer> sampleTypeIdsInSs = new ArrayList<Integer>();

        for (SampleStorageWrapper ss : allSampleStorages) {
            sampleTypeIdsInSs.add(ss.getSampleType().getId());
            expectedResults
                .add(new Object[] { ss.getSampleType().getNameShort(),
                    ss.getStudy().getNameShort() });
        }

        // add sample types not in any study
        Collection<SampleTypeWrapper> sampleTypesNotInSs = PredicateUtil
            .filter(getSampleTypes(), new Predicate<SampleTypeWrapper>() {
                public boolean evaluate(SampleTypeWrapper type) {
                    return !sampleTypeIdsInSs.contains(type.getId());
                }
            });
        List<SampleTypeWrapper> sampleTypesNotInSsOrdered = new ArrayList<SampleTypeWrapper>(
            sampleTypesNotInSs);
        Collections.sort(sampleTypesNotInSsOrdered,
            ORDER_ST_BY_SAMPLE_TYPE_NAME_SHORT);

        for (SampleTypeWrapper sampleType : sampleTypesNotInSsOrdered) {
            expectedResults.add(new Object[] { sampleType.getNameShort(),
                "Unused" });
        }

        return expectedResults;
    }

    private void checkResults() throws ApplicationException {
        checkResults(EnumSet.of(CompareResult.SIZE, CompareResult.ORDER));
    }
}
