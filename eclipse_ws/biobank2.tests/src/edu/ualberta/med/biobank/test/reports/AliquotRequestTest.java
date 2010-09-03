package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.server.reports.AliquotRequestImpl;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class AliquotRequestTest {

    private List<Object> getExpectedResults(List<Object> params) {
        List<Object> expectedResults = new ArrayList<Object>();

        for (int i = 0, numParams = params.size(); i < numParams; i += 4) {
            final String pnumber = (String) params.get(i);
            Date dateDrawn = (Date) params.get(i + 1);
            final String typeName = (String) params.get(i + 2);
            Integer maxResults = (Integer) params.get(i + 3);

            Predicate<AliquotWrapper> aliquotPnumber = new Predicate<AliquotWrapper>() {
                public boolean evaluate(AliquotWrapper aliquot) {
                    return aliquot.getPatientVisit().getPatient().getPnumber()
                        .equals(pnumber);
                }
            };

            Predicate<AliquotWrapper> aliquotSampleType = new Predicate<AliquotWrapper>() {
                public boolean evaluate(AliquotWrapper aliquot) {
                    return aliquot.getSampleType().getNameShort()
                        .equals(typeName);
                }
            };

            Collection<AliquotWrapper> allAliquots = TestReports.getInstance()
                .getAliquots();
            @SuppressWarnings("unchecked")
            List<AliquotWrapper> filteredAliquots = new ArrayList<AliquotWrapper>(
                PredicateUtil.filter(allAliquots, PredicateUtil.andPredicate(
                    TestReports.aliquotDrawnSameDay(dateDrawn),
                    TestReports.ALIQUOT_NOT_IN_SENT_SAMPLE_CONTAINER,
                    TestReports.ALIQUOT_HAS_POSITION, aliquotPnumber,
                    aliquotSampleType)));

            for (AliquotWrapper aliquot : filteredAliquots) {
                expectedResults.add(aliquot.getWrappedObject());
            }

            if (filteredAliquots.size() < maxResults) {
                expectedResults.add(AliquotRequestImpl.getNotFoundRow(pnumber,
                    dateDrawn, typeName, maxResults, filteredAliquots.size()));
            }
        }

        return expectedResults;
    }

    private BiobankReport getReport(List<Object> params) {
        BiobankReport report = BiobankReport.getReportByName("AliquotRequest");
        report.setSiteInfo("=", TestReports.getInstance().getSites().get(0)
            .getId());
        report.setContainerList("");
        report.setGroupBy("");

        // convert parameters to String objects for the report
        List<Object> stringParams = new ArrayList<Object>();
        for (Object o : params) {
            if (o instanceof Date) {
                stringParams.add(DateFormatter.formatAsDate((Date) o));
            } else {
                stringParams.add(o.toString());
            }
        }

        report.setParams(stringParams);

        return report;
    }

    private Collection<Object> checkReport(List<Object> params)
        throws ApplicationException {
        // because this report selects a random subset of the possibly results,
        // we cannot enforce a common order or size between the expected and
        // actual results
        return TestReports.getInstance().checkReport(getReport(params),
            getExpectedResults(params),
            EnumSet.noneOf(TestReports.CompareResult.class));
    }

    private static void addParams(List<Object> params, AliquotWrapper aliquot,
        Integer limit) {
        params.add(aliquot.getPatientVisit().getPatient().getPnumber());
        params.add(aliquot.getPatientVisit().getDateDrawn());
        params.add(aliquot.getSampleType().getNameShort());
        params.add(limit);
    }

    @Test
    public void testResultsForOneSetOfParams() throws Exception {
        List<Object> params = new ArrayList<Object>();
        for (AliquotWrapper aliquot : TestReports.getInstance().getAliquots()) {
            params.clear();

            params.add(aliquot.getPatientVisit().getPatient().getPnumber());
            params.add(aliquot.getPatientVisit().getDateDrawn());
            params.add(aliquot.getSampleType().getNameShort());
            params.add(5);

            Collection<Object> results;

            results = checkReport(params);
            Assert.assertTrue(results.size() > 0);
        }
    }

    @Test
    public void testResultsForManySetsOfParams() throws Exception {
        List<Object> params = new ArrayList<Object>();
        for (AliquotWrapper aliquot : TestReports.getInstance().getAliquots()) {
            addParams(params, aliquot, 5);
            if (params.size() > 12) {
                break;
            }
        }

        Collection<Object> results = checkReport(params);
        Assert.assertTrue(results.size() > 0);
    }

    @Test
    public void testDayWithNoResults() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        Assert.assertTrue(TestReports.getInstance().getAliquots().size() > 0);

        AliquotWrapper aliquot = TestReports.getInstance().getAliquots().get(0);
        List<Object> params = new ArrayList<Object>();
        addParams(params, aliquot, 5);
        params.set(1, calendar.getTime());

        Collection<Object> results = checkReport(params);
        Assert.assertTrue(results.size() == 1);
    }
}
