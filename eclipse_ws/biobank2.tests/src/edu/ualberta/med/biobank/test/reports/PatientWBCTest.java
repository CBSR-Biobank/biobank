package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;

public class PatientWBCTest extends AbstractReportTest {
    private static final Predicate<SpecimenWrapper> ALIQUOT_IS_DNA_SAMPLE_TYPE = new Predicate<SpecimenWrapper>() {
        public boolean evaluate(SpecimenWrapper aliquot) {
            return aliquot.getSpecimenType().getName().contains("DNA");
        }
    };
    private static final Predicate<SpecimenWrapper> ALIQUOT_IN_CABINET = new Predicate<SpecimenWrapper>() {
        public boolean evaluate(SpecimenWrapper aliquot) {
            return (aliquot.getParentContainer() != null)
                && aliquot.getParentContainer().getLabel().contains("Cabinet");
        }
    };

    @Test
    public void testResults() throws Exception {
        checkResults();
    }

    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        Collection<SpecimenWrapper> allAliquots = getSpecimens();
        @SuppressWarnings("unchecked")
        Collection<SpecimenWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(
                aliquotSite(isInSite(), getSiteId()),
                ALIQUOT_IS_DNA_SAMPLE_TYPE, ALIQUOT_IN_CABINET,
                ALIQUOT_NOT_IN_SENT_SAMPLE_CONTAINER));

        List<Object> expectedResults = new ArrayList<Object>();

        for (SpecimenWrapper aliquot : filteredAliquots) {
            ProcessingEventWrapper pevent = aliquot.getProcessingEvent();
            List<Object> objects = new ArrayList<Object>();
            objects.add(pevent.getCenter().getNameShort());
            objects.add(pevent.getCenter().getNameShort());
            objects.add(aliquot.getCollectionEvent().getPatient().getPnumber());
            objects.add(pevent.getCreatedAt());
            objects.add(aliquot.getSpecimenType().getName());
            objects.add(aliquot.getInventoryId());
            objects.add(aliquot.getParentContainer().getLabel());
            expectedResults.add(objects.toArray());
        }

        return expectedResults;
    }

    private void checkResults() throws Exception {
        checkResults(EnumSet.of(CompareResult.SIZE));
    }
}
