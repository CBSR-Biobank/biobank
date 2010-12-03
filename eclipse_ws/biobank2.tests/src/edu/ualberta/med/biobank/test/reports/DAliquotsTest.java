package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Mapper;
import edu.ualberta.med.biobank.common.util.MapperUtil;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;

public class DAliquotsTest extends AbstractReportTest {

    @Test
    public void testResults() throws Exception {
        checkResults(getTopContainerIds(getContainers()), new Date(0),
            new Date());
    }

    @Test
    public void testEmptyDateRange() throws Exception {
        checkResults(getTopContainerIds(getContainers()), new Date(), new Date(
            0));
    }

    @Test
    public void testSmallDatePoint() throws Exception {
        List<AliquotWrapper> aliquots = getAliquots();
        Assert.assertTrue(aliquots.size() > 0);

        AliquotWrapper aliquot = aliquots.get(aliquots.size() / 2);
        checkResults(getTopContainerIds(getContainers()),
            aliquot.getLinkDate(), aliquot.getLinkDate());
    }

    @Test
    public void testSmallDateRange() throws Exception {
        List<AliquotWrapper> aliquots = getAliquots();
        Assert.assertTrue(aliquots.size() > 0);

        AliquotWrapper aliquot = aliquots.get(aliquots.size() / 2);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(aliquot.getLinkDate());
        calendar.add(Calendar.HOUR_OF_DAY, 24);

        checkResults(getTopContainerIds(getContainers()),
            aliquot.getLinkDate(), calendar.getTime());
    }

    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        String topContainerIdList = getReport().getContainerList();
        String groupByDateField = getReport().getGroupBy();
        Date after = (Date) getReport().getParams().get(0);
        Date before = (Date) getReport().getParams().get(1);

        Collection<AliquotWrapper> allAliquots = getAliquots();
        Collection<AliquotWrapper> filteredAliquots = PredicateUtil.filter(
            allAliquots, PredicateUtil.andPredicate(
                AbstractReportTest.aliquotLinkedBetween(after, before),
                aliquotTopContainerIdIn(topContainerIdList)));

        List<Object> expectedResults = new ArrayList<Object>();

        Map<List<Object>, Long> groupedData = MapperUtil.map(filteredAliquots,
            groupAliquotsByStudyAndClinicAndDateField(groupByDateField));

        for (Map.Entry<List<Object>, Long> entry : groupedData.entrySet()) {
            List<Object> data = new ArrayList<Object>();
            data.addAll(entry.getKey());
            data.add(entry.getValue());

            expectedResults.add(data.toArray());
        }

        return expectedResults;
    }

    private void checkResults(Collection<Integer> topContainerIds, Date after,
        Date before) throws Exception {
        getReport().setParams(Arrays.asList((Object) after, (Object) before));
        getReport().setContainerList(StringUtils.join(topContainerIds, ","));

        for (String dateField : DATE_FIELDS) {
            // check the results against each possible date field
            getReport().setGroupBy(dateField);

            checkResults(EnumSet.of(CompareResult.SIZE));
        }
    }

    private static Mapper<AliquotWrapper, List<Object>, Long> groupAliquotsByStudyAndClinicAndDateField(
        final String dateField) {
        final Calendar calendar = Calendar.getInstance();
        return new Mapper<AliquotWrapper, List<Object>, Long>() {
            public List<Object> getKey(AliquotWrapper aliquot) {
                calendar.setTime(aliquot.getLinkDate());

                List<Object> key = new ArrayList<Object>();
                key.add(aliquot.getPatientVisit().getPatient().getStudy()
                    .getNameShort());
                key.add(aliquot.getPatientVisit().getShipment().getClinic()
                    .getNameShort());
                key.add(new Integer(calendar.get(Calendar.YEAR)));
                key.add(new Long(getDateFieldValue(calendar, dateField)));

                return key;
            }

            public Long getValue(AliquotWrapper type, Long oldValue) {
                return oldValue != null ? new Long(oldValue + 1) : new Long(1);
            }
        };
    }
}
