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

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class AliquotInvoiceByPatientTest {
    private static final Comparator<AliquotWrapper> ORDER_BY_ALIQUOT_PATIENT_NUMBER = new Comparator<AliquotWrapper>() {
        public int compare(AliquotWrapper lhs, AliquotWrapper rhs) {
            return lhs.getPatientVisit().getPatient().getPnumber()
                .compareTo(rhs.getPatientVisit().getPatient().getPnumber());
        }
    };

    private List<Object> getExpectedResults(final Date after, final Date before) {
        Collection<AliquotWrapper> allAliquots = TestReports.getInstance()
            .getAliquots();
        List<AliquotWrapper> filteredAliquots = new ArrayList<AliquotWrapper>(
            PredicateUtil.filter(allAliquots, PredicateUtil.andPredicate(
                TestReports.aliquotLinkedBetween(after, before),
                TestReports.ALIQUOT_NOT_IN_SENT_SAMPLE_CONTAINER)));

        Collections.sort(filteredAliquots, ORDER_BY_ALIQUOT_PATIENT_NUMBER);

        List<Object> expectedResults = new ArrayList<Object>();

        for (AliquotWrapper aliquot : filteredAliquots) {
            expectedResults.add(new Object[] { aliquot.getInventoryId(),
                aliquot.getPatientVisit().getPatient().getPnumber(),
                aliquot.getPatientVisit().getShipment().getClinic().getName(),
                aliquot.getLinkDate(), aliquot.getSampleType().getName() });
        }

        return expectedResults;
    }

    private BiobankReport getReport(Date after, Date before) {
        BiobankReport report = BiobankReport
            .getReportByName("AliquotInvoiceByPatient");
        report.setSiteInfo("=", TestReports.getInstance().getSites().get(0)
            .getId());
        report.setContainerList("");
        report.setGroupBy("");

        Object[] params = { after, before };
        report.setParams(Arrays.asList(params));

        return report;
    }

    private Collection<Object> checkReport(Date after, Date before)
        throws ApplicationException {
        return TestReports.getInstance().checkReport(
            getReport(after, before),
            getExpectedResults(after, before),
            EnumSet.of(TestReports.CompareResult.Order,
                TestReports.CompareResult.Size));
    }

    @Test
    public void testResults() throws Exception {
        Collection<Object> results = checkReport(new Date(0), new Date());
        Assert.assertTrue(results.size() > 0);
    }

    @Test
    public void testEmptyDateRange() throws Exception {
        Collection<Object> results = checkReport(new Date(), new Date(0));
        Assert.assertTrue(results.size() == 0);
    }

    @Test
    public void testSmallDatePoint() throws Exception {
        List<AliquotWrapper> aliquots = TestReports.getInstance().getAliquots();
        Assert.assertTrue(aliquots.size() > 0);

        AliquotWrapper aliquot = aliquots.get(aliquots.size() / 2);
        checkReport(aliquot.getLinkDate(), aliquot.getLinkDate());
    }

    @Test
    public void testSmallDateRange() throws Exception {
        List<AliquotWrapper> aliquots = TestReports.getInstance().getAliquots();
        Assert.assertTrue(aliquots.size() > 0);

        AliquotWrapper aliquot = aliquots.get(aliquots.size() / 2);
        checkReport(aliquot.getLinkDate(), aliquot.getLinkDate());
    }
}
