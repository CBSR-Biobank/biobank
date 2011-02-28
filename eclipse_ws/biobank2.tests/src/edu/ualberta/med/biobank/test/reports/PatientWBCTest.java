package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;

public class PatientWBCTest extends AbstractReportTest {
    private static final Predicate<SpecimenWrapper> ALIQUOT_IS_DNA_SAMPLE_TYPE = new Predicate<SpecimenWrapper>() {
        public boolean evaluate(SpecimenWrapper aliquot) {
            return aliquot.getSpecimenType().getName().contains("DNA");
        }
    };
    private static final Predicate<SpecimenWrapper> ALIQUOT_IN_CABINET = new Predicate<SpecimenWrapper>() {
        public boolean evaluate(SpecimenWrapper aliquot) {
            return (aliquot.getParent() != null)
                && aliquot.getParent().getLabel().contains("Cabinet");
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
            ProcessingEventWrapper visit = aliquot.getProcessingEvent();
            List<Object> objects = new ArrayList<Object>();
            objects.add(visit.getPatient().getStudy().getNameShort());
            objects.add(visit.getCenter().getNameShort());
            objects.add(visit.getPatient().getPnumber());
            objects.add(visit.getDateProcessed());
            objects.add(aliquot.getSpecimenType().getName());
            objects.add(aliquot.getInventoryId());
            objects.add(aliquot.getParent().getLabel());

            expectedResults.add(objects.toArray());
        }

        return expectedResults;
    }

    private void checkResults() throws Exception {
        checkResults(EnumSet.of(CompareResult.SIZE));
    }
}
