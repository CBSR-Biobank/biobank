package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;

public class AliquotInvoiceByPatientTest extends AbstractReportTest {
    private static final Comparator<SpecimenWrapper> ORDER_BY_ALIQUOT_PNUMBER = new Comparator<SpecimenWrapper>() {
        public int compare(SpecimenWrapper lhs, SpecimenWrapper rhs) {
            int cmp = compareStrings(lhs.getCollectionEvent().getPatient()
                .getPnumber(), rhs.getCollectionEvent().getPatient()
                .getPnumber());

            if (cmp != 0) {
                return cmp;
            }

            return compareStrings(lhs.getInventoryId(), rhs.getInventoryId());
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
        checkResults(aliquot.getCreatedAt(), aliquot.getCreatedAt());
    }

    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        Date after = (Date) getReport().getParams().get(0);
        Date before = (Date) getReport().getParams().get(1);

        Collection<SpecimenWrapper> allAliquots = getSpecimens();
        @SuppressWarnings("unchecked")
        List<SpecimenWrapper> filteredAliquots = new ArrayList<SpecimenWrapper>(
            PredicateUtil.filter(allAliquots, PredicateUtil.andPredicate(
                AbstractReportTest.aliquotLinkedBetween(after, before),
                ALIQUOT_NOT_IN_SENT_SAMPLE_CONTAINER,
                aliquotSite(isInSite(), getSiteId()))));

        Collections.sort(filteredAliquots, ORDER_BY_ALIQUOT_PNUMBER);

        List<Object> expectedResults = new ArrayList<Object>();

        for (SpecimenWrapper aliquot : filteredAliquots) {
            expectedResults.add(new Object[] { aliquot.getInventoryId(),
                aliquot.getCollectionEvent().getPatient().getPnumber(),
                aliquot.getProcessingEvent().getCenter().getName(),
                aliquot.getCreatedAt(), aliquot.getSpecimenType().getName() });
        }

        return expectedResults;
    }

    private void checkResults(Date after, Date before) throws Exception {
        getReport().setParams(Arrays.asList((Object) after, (Object) before));

        checkResults(EnumSet.of(CompareResult.SIZE, CompareResult.ORDER));
    }
}
