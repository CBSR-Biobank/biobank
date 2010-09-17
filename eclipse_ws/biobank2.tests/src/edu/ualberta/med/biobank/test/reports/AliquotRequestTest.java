package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.server.reports.AliquotRequest;
import edu.ualberta.med.biobank.server.reports.AliquotRequestImpl;

public class AliquotRequestTest extends AbstractReportTest {
    private static final Integer ALIQUOT_LIMIT = new Integer(5);

    @Test
    public void testResultsForOneSetOfParams() throws Exception {
        List<Object> params = new ArrayList<Object>();
        for (AliquotWrapper aliquot : getAliquots()) {
            params.clear();

            addParams(params, aliquot, ALIQUOT_LIMIT);

            checkResults(params);
        }
    }

    @Test
    public void testResultsForManySetsOfParams() throws Exception {
        List<Object> params = new ArrayList<Object>();
        int numIterations = 0;
        for (AliquotWrapper aliquot : getAliquots()) {
            addParams(params, aliquot, ALIQUOT_LIMIT);

            if (++numIterations >= 3) {
                break;
            }
        }

        checkResults(params);
    }

    @Test
    public void testDayWithNoResults() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        Assert.assertTrue(getAliquots().size() > 0);

        AliquotWrapper aliquot = getAliquots().get(0);
        List<Object> params = new ArrayList<Object>();
        addParams(params, aliquot, ALIQUOT_LIMIT);

        ((AliquotRequest) params.get(0)).setDateDrawn(calendar.getTime());

        checkResults(params);
    }

    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        List<Object> expectedResults = new ArrayList<Object>();
        List<Object> params = getReport().getParams();

        for (Object o : params) {
            AliquotRequest request = (AliquotRequest) o;
            final String pnumber = request.getPnumber();
            final String typeName = request.getSampleTypeNameShort();
            Date dateDrawn = request.getDateDrawn();
            Integer maxResults = request.getMaxAliquots();

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

            Collection<AliquotWrapper> allAliquots = getAliquots();
            @SuppressWarnings("unchecked")
            List<AliquotWrapper> filteredAliquots = new ArrayList<AliquotWrapper>(
                PredicateUtil.filter(allAliquots, PredicateUtil.andPredicate(
                    AbstractReportTest.aliquotDrawnSameDay(dateDrawn),
                    ALIQUOT_NOT_IN_SENT_SAMPLE_CONTAINER, ALIQUOT_HAS_POSITION,
                    aliquotPnumber, aliquotSampleType,
                    aliquotSite(isInSite(), getSiteId()))));

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

    private void checkResults(List<Object> params) throws Exception {
        getReport().setParams(params);

        // because this report selects a random subset of the possibly results,
        // we cannot enforce a common order or size between the expected and
        // actual results

        checkResults(EnumSet.noneOf(CompareResult.class));
    }

    private static void addParams(List<Object> params, AliquotWrapper aliquot,
        Integer limit) {

        AliquotRequest request = new AliquotRequest();
        request.setPnumber(aliquot.getPatientVisit().getPatient().getPnumber());
        request.setDateDrawn(aliquot.getPatientVisit().getDateDrawn());
        request.setSampleTypeNameShort(aliquot.getSampleType().getNameShort());
        request.setMaxAliquots(limit);

        params.add(request);
    }
}
