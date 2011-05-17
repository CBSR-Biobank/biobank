package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Mapper;
import edu.ualberta.med.biobank.common.util.MapperUtil;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;

public class FvLPatientVisitsTest extends AbstractReportTest {
    private static final Mapper<ProcessingEventWrapper, List<String>, List<Date>> GROUP_PVS_BY_STUDY_CLINIC = new Mapper<ProcessingEventWrapper, List<String>, List<Date>>() {
        public List<String> getKey(ProcessingEventWrapper pevent) {
            return Arrays.asList(pevent.getCenter().getNameShort(), pevent
                .getCenter().getNameShort());
        }

        public List<Date> getValue(ProcessingEventWrapper pevent,
            List<Date> stats) {
            Date newDateReceived = pevent.getCreatedAt();
            if (stats == null) {
                return Arrays.asList(newDateReceived, newDateReceived);
            } else {
                Date minDateReceived = stats.get(0);
                Date maxDateReceived = stats.get(1);
                if (newDateReceived.before(minDateReceived)) {
                    minDateReceived = newDateReceived;
                }
                if (newDateReceived.after(maxDateReceived)) {
                    maxDateReceived = newDateReceived;
                }
                return Arrays.asList(minDateReceived, maxDateReceived);
            }
        }
    };

    @Test
    public void testResults() throws Exception {
        checkResults(EnumSet.of(CompareResult.SIZE));
    }

    @Override
    protected Collection<Object> getExpectedResults() throws Exception {
        Collection<ProcessingEventWrapper> allPatientVisits = getPatientVisits();
        Collection<ProcessingEventWrapper> filteredPatientVisits = PredicateUtil
            .filter(allPatientVisits, patientVisitSite(isInSite(), getSiteId()));
        Map<List<String>, List<Date>> groupedPatientVisits = MapperUtil.map(
            filteredPatientVisits, GROUP_PVS_BY_STUDY_CLINIC);

        List<Object> expectedResults = new ArrayList<Object>();

        List<Object> objects = new ArrayList<Object>();
        for (Map.Entry<List<String>, List<Date>> entry : groupedPatientVisits
            .entrySet()) {
            objects.clear();
            objects.addAll(entry.getKey());
            objects.addAll(entry.getValue());
            expectedResults.add(objects.toArray());
        }

        return expectedResults;
    }
}
