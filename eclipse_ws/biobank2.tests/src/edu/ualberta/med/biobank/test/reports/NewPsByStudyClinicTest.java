package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Mapper;
import edu.ualberta.med.biobank.common.util.MapperUtil;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class NewPsByStudyClinicTest extends AbstractReportTest {

    @Test
    public void testResults() throws Exception {
        checkResults(getTopContainerIds(), new Date(0), new Date());
    }

    @Test
    public void testEmptyDateRange() throws Exception {
        checkResults(getTopContainerIds(), new Date(), new Date(0));
    }

    @Test
    public void testSmallDatePoint() throws Exception {
        List<AliquotWrapper> aliquots = TestReports.getInstance().getAliquots();
        Assert.assertTrue(aliquots.size() > 0);

        AliquotWrapper aliquot = aliquots.get(aliquots.size() / 2);
        checkResults(getTopContainerIds(), aliquot.getLinkDate(),
            aliquot.getLinkDate());
    }

    @Test
    public void testSmallDateRange() throws Exception {
        List<AliquotWrapper> aliquots = TestReports.getInstance().getAliquots();
        Assert.assertTrue(aliquots.size() > 0);

        AliquotWrapper aliquot = aliquots.get(aliquots.size() / 2);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(aliquot.getLinkDate());
        calendar.add(Calendar.HOUR_OF_DAY, 24);

        checkResults(getTopContainerIds(), aliquot.getLinkDate(),
            calendar.getTime());
    }

    @Override
    protected Collection<Object> getExpectedResults() {
        String groupByDateField = getReport().getGroupBy();
        Date after = (Date) getReport().getParams().get(0);
        Date before = (Date) getReport().getParams().get(1);

        Collection<PatientVisitWrapper> allPatientVisits = getPatientVisits();
        Collection<PatientVisitWrapper> filteredPatientVisits = PredicateUtil
            .filter(allPatientVisits, PredicateUtil.andPredicate(
                patientVisitProcessedBetween(after, before),
                patientVisitSite(isInSite(), getSiteId())));

        List<Object> expectedResults = new ArrayList<Object>();

        Map<List<Object>, Set<Integer>> groupedData = MapperUtil.map(
            filteredPatientVisits,
            groupPvsByStudyAndClinicAndDateField(groupByDateField));

        for (Map.Entry<List<Object>, Set<Integer>> entry : groupedData
            .entrySet()) {
            List<Object> data = new ArrayList<Object>();
            data.addAll(entry.getKey());
            data.add(entry.getValue().size());

            expectedResults.add(data.toArray());
        }

        return expectedResults;
    }

    private void checkResults(Collection<Integer> topContainerIds, Date after,
        Date before) throws ApplicationException {
        getReport().setParams(Arrays.asList((Object) after, (Object) before));
        getReport().setContainerList(StringUtils.join(topContainerIds, ","));

        for (String dateField : DATE_FIELDS) {
            // check the results against each possible date field
            getReport().setGroupBy(dateField);

            checkResults(EnumSet.of(CompareResult.SIZE));
        }
    }

    private static Mapper<PatientVisitWrapper, List<Object>, Set<Integer>> groupPvsByStudyAndClinicAndDateField(
        final String dateField) {
        final Calendar calendar = Calendar.getInstance();
        return new Mapper<PatientVisitWrapper, List<Object>, Set<Integer>>() {
            public List<Object> getKey(PatientVisitWrapper patientVisit) {
                calendar.setTime(patientVisit.getDateProcessed());

                List<Object> key = new ArrayList<Object>();
                key.add(patientVisit.getPatient().getStudy().getNameShort());
                key.add(patientVisit.getShipment().getClinic().getNameShort());
                key.add(new Integer(calendar.get(Calendar.YEAR)));
                key.add(new Long(getDateFieldValue(calendar, dateField)));

                return key;
            }

            public Set<Integer> getValue(PatientVisitWrapper type,
                Set<Integer> patientIds) {
                if (patientIds == null) {
                    patientIds = new HashSet<Integer>();
                }

                if (!patientIds.contains(type.getPatient().getId())) {
                    patientIds.add(type.getId());
                }

                return patientIds;
            }
        };
    }
}
