package edu.ualberta.med.biobank.test.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.server.reports.AbstractReport;
import edu.ualberta.med.biobank.server.reports.ReportFactory;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

// TODO: extend this class
public abstract class AbstractReportTest {
    public static enum CompareResult {
        ORDER, SIZE
    };

    public static final String[] DATE_FIELDS = { "Week", "Month", "Quarter",
        "Year" };

    public static final Predicate<ContainerWrapper> CONTAINER_IS_TOP_LEVEL = new Predicate<ContainerWrapper>() {
        public boolean evaluate(ContainerWrapper container) {
            return container.getParent() == null;
        }
    };
    public static final Comparator<AliquotWrapper> ORDER_ALIQUOT_BY_PNUMBER = new Comparator<AliquotWrapper>() {
        public int compare(AliquotWrapper lhs, AliquotWrapper rhs) {
            return lhs.getPatientVisit().getPatient().getPnumber()
                .compareTo(rhs.getPatientVisit().getPatient().getPnumber());
        }
    };

    private static final String[] SITE_OPS = { "=", "!=" };
    private BiobankReport report;
    private ReportDataSource dataSource = TestReports.getInstance();

    // public void setReportDataSource(ReportDataSource dataSource) {
    // this.dataSource = dataSource;
    // }

    public WritableApplicationService getAppService() {
        return TestReports.getInstance().getAppService();
    }

    public static Predicate<AliquotWrapper> aliquotSite(final boolean isIn,
        final Integer siteId) {
        return new Predicate<AliquotWrapper>() {
            public boolean evaluate(AliquotWrapper aliquot) {
                return isIn == aliquot.getPatientVisit().getShipment()
                    .getSite().getId().equals(siteId);
            }
        };
    }

    public static Predicate<PatientVisitWrapper> patientVisitSite(
        final boolean isIn, final Integer siteId) {
        return new Predicate<PatientVisitWrapper>() {
            public boolean evaluate(PatientVisitWrapper patientVisit) {
                return isIn == patientVisit.getShipment().getSite().getId()
                    .equals(siteId);
            }
        };
    }

    public static Predicate<PatientVisitWrapper> patientVisitProcessedBetween(
        final Date after, final Date before) {
        return new Predicate<PatientVisitWrapper>() {
            public boolean evaluate(PatientVisitWrapper patientVisit) {
                return (patientVisit.getDateProcessed().after(after) || patientVisit
                    .getDateProcessed().equals(after))
                    && (patientVisit.getDateProcessed().before(before) || patientVisit
                        .getDateProcessed().equals(before));
            }
        };
    }

    public static Collection<Integer> getTopContainerIds() {
        Set<Integer> topContainerIds = new HashSet<Integer>();
        for (ContainerWrapper container : PredicateUtil.filter(TestReports
            .getInstance().getContainers(), CONTAINER_IS_TOP_LEVEL)) {
            topContainerIds.add(container.getId());
        }
        return topContainerIds;
    }

    public static Predicate<AliquotWrapper> aliquotTopContainerIdIn(String list) {
        final List<Integer> topContainerIds = new ArrayList<Integer>();
        for (String id : list.split(",")) {
            topContainerIds.add(Integer.valueOf(id));
        }
        return new Predicate<AliquotWrapper>() {
            public boolean evaluate(AliquotWrapper aliquot) {
                ContainerWrapper top = aliquot.getTop();
                return (top != null) && topContainerIds.contains(top.getId());
            }
        };
    }

    public static int getDateFieldValue(Calendar calendar, String dateField) {
        int dateGroupBy = 0;

        if (dateField.equals("Year")) {
            dateGroupBy = calendar.get(Calendar.YEAR);
        } else if (dateField.equals("Quarter")) {
            dateGroupBy = (calendar.get(Calendar.MONTH) / 3) + 1;
        } else if (dateField.equals("Month")) {
            dateGroupBy = calendar.get(Calendar.MONTH) + 1;
        } else if (dateField.equals("Week")) {
            dateGroupBy = calendar.get(Calendar.WEEK_OF_YEAR);

            // java.util.GregorianCalendar.WEEK_OF_YEAR can only have 1 to 53
            // weeks a year. However, it is seemingly possibly to have 54 weeks
            // in a year (e.g. "Jan 01, 2000" - 366 days, with the first and
            // last week in the year having 1 day). MySQL SELECT
            // WEEK('2000-01-01', 0) returns 0 and SELECT WEEK('2000-12-31')
            // returns 53. However, java.util.GregorianCalendar would say that
            // the week of '2000-01-01' is 1 and the week of '2000-12-31' is 1
            // as well. This is because java.util.GregorianCalendar considers
            // the last week of the year 2000 the first week of the next year,
            // NOT ITS OWN WEEK. So, we must check for this case.
            //
            // Note that MySQL's behaviour depends on the value of the
            // "default_week_format" variable.
            if ((dateGroupBy == 1)
                && (calendar.get(Calendar.DAY_OF_YEAR) > 3 * calendar
                    .getMaximum(Calendar.DAY_OF_WEEK))) {
                // If there has clearly been more than one week in this year,
                // but its week of year is only 1, then add one to the previous
                // week number.
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                int previousWeek = calendar.get(Calendar.WEEK_OF_YEAR);
                calendar.add(Calendar.WEEK_OF_YEAR, 1);

                dateGroupBy = previousWeek + 1;
            }

            // change range from [1..54] to [0..53] to match MySQL
            dateGroupBy -= 1;
        }

        return dateGroupBy;
    }

    protected void setReport(BiobankReport report) {
        this.report = report;
    }

    protected BiobankReport getReport() {
        if (report == null) {
            String reportName = this.getClass().getSimpleName()
                .replace("Test", "");
            report = BiobankReport.getReportByName(reportName);

            report.setParams(new ArrayList<Object>());
            report.setContainerList("");
            report.setGroupBy("");
        }

        return report;
    }

    protected boolean isInSite() {
        return getReport().getOp() == "=";
    }

    protected Integer getSiteId() {
        return getReport().getSiteId();
    }

    // use getReport() to get the parameters
    protected abstract Collection<Object> getExpectedResults();

    protected void checkResults(EnumSet<CompareResult> cmpOptions)
        throws ApplicationException {
        for (SiteWrapper site : getSites()) {
            for (String op : SITE_OPS) {
                getReport().setSiteInfo(op, site.getId());
                compareResults(cmpOptions);
            }
        }
    }

    protected List<SiteWrapper> getSites() {
        return dataSource.getSites();
    }

    protected List<SampleTypeWrapper> getSampleTypes() {
        return dataSource.getSampleTypes();
    }

    protected List<AliquotWrapper> getAliquots() {
        return dataSource.getAliquots();
    }

    protected List<ContainerWrapper> getContainers() {
        return dataSource.getContainers();
    }

    protected List<ClinicWrapper> getClinics() {
        return dataSource.getClinics();
    }

    protected List<StudyWrapper> getStudies() {
        return dataSource.getStudies();
    }

    protected List<ContactWrapper> getContacts() {
        return dataSource.getContacts();
    }

    protected List<PatientVisitWrapper> getPatientVisits() {
        return dataSource.getPatientVisits();
    }

    protected List<PatientWrapper> getPatients() {
        return dataSource.getPatients();
    }

    private Collection<Object> compareResults(EnumSet<CompareResult> cmpOptions)
        throws ApplicationException {
        System.out.println("Comparing results for Report "
            + getReport().getClassName() + " using params "
            + Arrays.toString(getReport().getParams().toArray())
            + (isInSite() ? "" : " not") + " in site " + getSiteId()
            + " grouped by " + getReport().getGroupBy() + " in containers "
            + getReport().getContainerList());

        Collection<Object> expectedResults = getExpectedResults();

        List<Object> actualResults = getReport().generate(getAppService());
        List<Object> postProcessedExpectedResults = postProcessExpectedResults(expectedResults);

        // we may only require the actual results to be a subset of
        // the expected results, so the actual results must be iterated in an
        // outer loop.
        Iterator<Object> it = postProcessedExpectedResults.iterator();
        int actualResultsSize = 0;
        for (Object actualRow : actualResults) {
            boolean isFound = false;
            if (cmpOptions.contains(CompareResult.ORDER)) {
                if (it.hasNext()) {
                    Object[] next = (Object[]) it.next();

                    // the order of arguments to Arrays.equals() matters, e.g.:
                    //
                    // java.util.Date date = new java.util.Date();
                    // java.util.Date stamp =
                    // new java.sql.Timestamp(date.getTime());
                    // assertTrue(date.equals(stamp));
                    // assertTrue(date.compareTo(stamp) == 0);
                    // assertTrue(stamp.compareTo(date) == 0);
                    // assertTrue(stamp.equals(date)); // <-- FAILS
                    if (Arrays.equals(next, (Object[]) actualRow)) {
                        isFound = true;
                    }
                }
            } else {
                for (Object expectedRow : postProcessedExpectedResults) {
                    if (Arrays.equals((Object[]) expectedRow,
                        (Object[]) actualRow)) {
                        isFound = true;
                        break;
                    }
                }
            }

            if (!isFound) {
                Assert.fail("did not expect this row in actual results: "
                    + Arrays.toString((Object[]) actualRow));
            } else {
                System.out.println("found: "
                    + Arrays.toString((Object[]) actualRow));
            }

            actualResultsSize++;
        }

        it = null; // done with this iterator.

        // cannot accurately know the size of actual results until they have all
        // been run through once, so, do not compare actual size to expected
        // size

        if (cmpOptions.contains(CompareResult.SIZE)
            && (postProcessedExpectedResults.size() != actualResultsSize)) {
            Assert.fail("expected " + postProcessedExpectedResults.size()
                + " results, got " + actualResultsSize);
        }

        return postProcessedExpectedResults;
    }

    private List<Object> postProcessExpectedResults(
        Collection<Object> expectedResults) throws ApplicationException {
        // post process individual rows BEFORE post processing the entire
        // collection, if necessary
        List<Object> postProcessedExpectedResults = new ArrayList<Object>(
            expectedResults);

        // some classes derived from AbstractReport modify the BiobankReport
        // objects they are passed, so the BiobankReport params must be
        // remembered and restored when a new AbstractReport is created (e.g.
        // see InvoicingReportImpl).
        List<Object> originalParams = new ArrayList<Object>(getReport()
            .getParams());
        AbstractReport abstractReport = ReportFactory.createReport(getReport());
        getReport().setParams(originalParams); // restore original params
        AbstractRowPostProcess rowPostProcessor = abstractReport
            .getRowPostProcess();

        if (rowPostProcessor != null) {
            Object processedRow;
            for (int i = 0, numRows = postProcessedExpectedResults.size(); i < numRows; i++) {
                processedRow = rowPostProcessor
                    .rowPostProcess(postProcessedExpectedResults.get(i));
                postProcessedExpectedResults.set(i, processedRow);
            }
        }

        // post process the entire expected collection AFTER individual
        // rows have been processed
        postProcessedExpectedResults = abstractReport.postProcess(
            getAppService(), postProcessedExpectedResults);

        return postProcessedExpectedResults;
    }
}
