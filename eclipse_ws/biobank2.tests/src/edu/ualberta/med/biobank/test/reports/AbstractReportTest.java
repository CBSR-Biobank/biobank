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
import edu.ualberta.med.biobank.common.util.DateCompare;
import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.server.reports.AbstractReport;
import edu.ualberta.med.biobank.server.reports.ReportFactory;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class AbstractReportTest {
    public static enum CompareResult {
        ORDER,
        SIZE
    };

    public static final String[] DATE_FIELDS = { "Week", "Month", "Quarter",
        "Year" };

    public static final Predicate<ContainerWrapper> CONTAINER_IS_TOP_LEVEL =
        new Predicate<ContainerWrapper>() {
            @Override
            public boolean evaluate(ContainerWrapper container) {
                return container.getParentContainer() == null;
            }
        };
    public static final Predicate<ContainerWrapper> CONTAINER_CAN_STORE_SAMPLES_PREDICATE =
        new Predicate<ContainerWrapper>() {
            @Override
            public boolean evaluate(ContainerWrapper container) {
                return (container.getContainerType().getSpecimenTypeCollection(
                    false) != null)
                    && (container.getContainerType()
                        .getSpecimenTypeCollection(false).size() > 0);
            }
        };
    public static final Predicate<SpecimenWrapper> ALIQUOT_NOT_IN_SENT_SAMPLE_CONTAINER =
        new Predicate<SpecimenWrapper>() {
            @Override
            public boolean evaluate(SpecimenWrapper aliquot) {
                return (aliquot.getParentContainer() == null)
                    || !aliquot.getParentContainer().getLabel()
                        .startsWith("SS");
            }
        };
    public static final Predicate<SpecimenWrapper> ALIQUOT_HAS_POSITION =
        new Predicate<SpecimenWrapper>() {
            @Override
            public boolean evaluate(SpecimenWrapper aliquot) {
                return aliquot.getParentContainer() != null;
            }
        };
    public static final Comparator<SpecimenWrapper> ORDER_ALIQUOT_BY_PNUMBER =
        new Comparator<SpecimenWrapper>() {
            @Override
            public int compare(SpecimenWrapper lhs, SpecimenWrapper rhs) {
                return compareStrings(lhs.getCollectionEvent().getPatient()
                    .getPnumber(), rhs.getCollectionEvent().getPatient()
                    .getPnumber());
            }
        };

    private BiobankReport report;
    private static ReportDataSource dataSource;

    protected static void setReportDataSource(ReportDataSource dataSource) {
        AbstractReportTest.dataSource = dataSource;
    }

    public static Predicate<SpecimenWrapper> aliquotSite(final boolean isIn,
        final Integer siteId) {
        return new Predicate<SpecimenWrapper>() {
            @Override
            public boolean evaluate(SpecimenWrapper aliquot) {
                return isIn == aliquot.getProcessingEvent().getCenter().getId()
                    .equals(siteId);
            }
        };
    }

    public static Predicate<ContainerWrapper> containerSite(final boolean isIn,
        final Integer siteId) {
        return new Predicate<ContainerWrapper>() {
            @Override
            public boolean evaluate(ContainerWrapper container) {
                return isIn == container.getSite().getId().equals(siteId);
            }
        };
    }

    public static Predicate<ProcessingEventWrapper> patientVisitSite(
        final boolean isIn, final Integer siteId) {
        return new Predicate<ProcessingEventWrapper>() {
            @Override
            public boolean evaluate(ProcessingEventWrapper patientVisit) {
                return !isIn || patientVisit.getCenter().getId().equals(siteId);
            }
        };
    }

    public static Predicate<SpecimenWrapper> aliquotDrawnSameDay(final Date date) {
        final Calendar wanted = Calendar.getInstance();
        wanted.setTime(date);

        return new Predicate<SpecimenWrapper>() {
            private Calendar drawn = Calendar.getInstance();

            @Override
            public boolean evaluate(SpecimenWrapper aliquot) {
                drawn.setTime(aliquot.getParentSpecimen().getCreatedAt());
                int drawnDayOfYear = drawn.get(Calendar.DAY_OF_YEAR);
                int wantedDayOfYear = wanted.get(Calendar.DAY_OF_YEAR);
                int drawnYear = drawn.get(Calendar.YEAR);
                int wantedYear = wanted.get(Calendar.YEAR);
                return (drawnDayOfYear == wantedDayOfYear)
                    && (drawnYear == wantedYear);
            }
        };
    }

    public static Predicate<SpecimenWrapper> aliquotLinkedBetween(
        final Date after, final Date before) {
        return new Predicate<SpecimenWrapper>() {
            @Override
            public boolean evaluate(SpecimenWrapper aliquot) {
                Date linked = aliquot.getCreatedAt();
                return (DateCompare.compare(linked, after) <= 0)
                    && (DateCompare.compare(linked, before) >= 0);
            }
        };
    }

    public static Predicate<SpecimenWrapper> aliquotPvProcessedBetween(
        final Date after, final Date before) {
        return new Predicate<SpecimenWrapper>() {
            @Override
            public boolean evaluate(SpecimenWrapper aliquot) {
                Date processed = aliquot.getProcessingEvent().getCreatedAt();
                return (DateCompare.compare(processed, after) <= 0)
                    && (DateCompare.compare(processed, before) >= 0);
            }
        };
    }

    public static Predicate<ProcessingEventWrapper> patientVisitProcessedBetween(
        final Date after, final Date before) {
        return new Predicate<ProcessingEventWrapper>() {
            @Override
            public boolean evaluate(ProcessingEventWrapper pevent) {
                Date processed = pevent.getCreatedAt();
                return (DateCompare.compare(processed, after) <= 0)
                    && (DateCompare.compare(processed, before) >= 0);
            }
        };
    }

    public static Collection<Integer> getTopContainerIds(
        Collection<ContainerWrapper> containers) {
        Set<Integer> topContainerIds = new HashSet<Integer>();
        for (ContainerWrapper container : PredicateUtil.filter(containers,
            CONTAINER_IS_TOP_LEVEL)) {
            topContainerIds.add(container.getId());
        }
        return topContainerIds;
    }

    public static Collection<ContainerWrapper> getTopContainers(
        Collection<ContainerWrapper> containers) {
        Set<ContainerWrapper> topContainers = new HashSet<ContainerWrapper>();
        for (ContainerWrapper container : PredicateUtil.filter(containers,
            CONTAINER_IS_TOP_LEVEL)) {
            topContainers.add(container);
        }
        return topContainers;
    }

    public static Predicate<SpecimenWrapper> aliquotTopContainerIdIn(String list) {
        if ((list == null) || list.isEmpty()) {
            return new Predicate<SpecimenWrapper>() {
                @Override
                public boolean evaluate(SpecimenWrapper aliquot) {
                    return false;
                }
            };
        }
        final List<Integer> topContainerIds = new ArrayList<Integer>();
        for (String id : list.split(",")) {
            topContainerIds.add(Integer.valueOf(id));
        }
        return new Predicate<SpecimenWrapper>() {
            @Override
            public boolean evaluate(SpecimenWrapper aliquot) {
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

    protected final void setReport(BiobankReport report) {
        this.report = report;
    }

    protected final BiobankReport getReport() {
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

    protected final boolean isInSite() {
        return getReport().getOp() == "=";
    }

    protected final Integer getSiteId() {
        return getReport().getSiteId();
    }

    // use getReport() to get the parameters
    protected abstract Collection<Object> getExpectedResults() throws Exception;

    /**
     * Override this method to return an implementation of PostProcessTester if
     * the postProcess() method should be compared with the results of another
     * implementation.
     * 
     * @return
     */
    protected PostProcessTester getPostProcessTester() {
        return null;
    }

    protected void checkResults(EnumSet<CompareResult> cmpOptions)
        throws Exception {
        for (SiteWrapper site : getSites()) {
            getReport().setSiteInfo("=", site.getId());
            compareResults(cmpOptions);
        }

        // run report across all sites
        getReport().setSiteInfo("!=", 0);
        compareResults(cmpOptions);
    }

    protected final WritableApplicationService getAppService() {
        return dataSource.getAppService();
    }

    protected final List<SiteWrapper> getSites() throws Exception {
        return dataSource.getSites();
    }

    protected final List<SpecimenTypeWrapper> getSpecimenTypes()
        throws Exception {
        return dataSource.getSpecimenTypes();
    }

    protected final List<AliquotedSpecimenWrapper> getAliquotedSpecimens()
        throws Exception {
        return dataSource.getAliquotedSpecimens();
    }

    protected final List<SpecimenWrapper> getSpecimens() throws Exception {
        return dataSource.getSpecimens();
    }

    protected final List<ContainerWrapper> getContainers() throws Exception {
        return dataSource.getContainers();
    }

    protected final List<StudyWrapper> getStudies() throws Exception {
        return dataSource.getStudies();
    }

    protected final List<ProcessingEventWrapper> getPatientVisits()
        throws Exception {
        return dataSource.getPatientVisits();
    }

    protected final List<PatientWrapper> getPatients() throws Exception {
        return dataSource.getPatients();
    }

    private void testPostProcess(EnumSet<CompareResult> cmpOptions,
        Collection<Object> rawResults, List<Object> expectedResults) {
        PostProcessTester postProcessTester = getPostProcessTester();

        if (postProcessTester != null) {
            List<Object> postProcessedRawResults = postProcessTester
                .postProcess(getAppService(), rawResults);

            compareResults(cmpOptions, expectedResults, postProcessedRawResults);
        }
    }

    private Collection<Object> compareResults(EnumSet<CompareResult> cmpOptions)
        throws Exception {
        // TODO: logging?
        // System.out.print("compareResults(" + cmpOptions + ") for "
        // + getReport().getClassName() + " w/ params "
        // + Arrays.toString(getReport().getParams().toArray())
        // + (isInSite() ? "" : " not") + " in site " + getSiteId());
        //
        // if ((getReport().getGroupBy() != null)
        // && (getReport().getGroupBy().length() > 0)) {
        // System.out.print(" grouped by " + getReport().getGroupBy());
        // }
        //
        // if ((getReport().getContainerList() != null)
        // && (getReport().getContainerList().length() > 0)) {
        // System.out
        // .print(" in containers " + getReport().getContainerList());
        // }
        //
        // System.out.println();

        Collection<Object> expectedResults = getExpectedResults();

        List<Object> actualResults = ReportFactory.createReport(getReport())
            .generate(getAppService());
        List<Object> postProcessedExpectedResults =
            postProcessExpectedResults(expectedResults);

        testPostProcess(cmpOptions, expectedResults,
            postProcessedExpectedResults);

        compareResults(cmpOptions, actualResults, postProcessedExpectedResults);

        return postProcessedExpectedResults;
    }

    private static void compareResults(EnumSet<CompareResult> cmpOptions,
        List<Object> actualResults, List<Object> expectedResults) {
        // we may only require the actual results to be a subset of
        // the expected results, so the actual results must be iterated in an
        // outer loop.
        Iterator<Object> it = expectedResults.iterator();
        int actualResultsSize = 0;
        for (Object actualRow : actualResults) {
            boolean isFound = false;
            if (cmpOptions.contains(CompareResult.ORDER)) {
                if (it.hasNext()) {
                    Object[] next = (Object[]) it.next();
                    if (datewiseArraysEquals(next, (Object[]) actualRow)) {
                        isFound = true;
                    }
                }
            } else {
                for (Object expectedRow : expectedResults) {
                    if (datewiseArraysEquals((Object[]) expectedRow,
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
                // TODO: logging?
                // System.out.println("found: "
                // + Arrays.toString((Object[]) actualRow));
            }

            actualResultsSize++;
        }

        it = null; // done with this iterator.

        // cannot accurately know the size of actual results until they have all
        // been run through once, so, do not compare actual size to expected
        // size

        if (cmpOptions.contains(CompareResult.SIZE)
            && (expectedResults.size() != actualResultsSize)) {
            Assert.fail("expected " + expectedResults.size() + " results, got "
                + actualResultsSize);
        }
    }

    /**
     * Compare two Object[] references, paying special attention to Object-s
     * that implement Date, comparing them using a special function.
     * 
     * @param a1
     * @param a2
     * @return true if the two arrays are the same length and the Object
     *         referenced at each corresponding index is equal.
     */
    private static boolean datewiseArraysEquals(Object[] a1, Object[] a2) {
        if (a1.length != a2.length) {
            return false;
        }

        for (int i = 0; i < a1.length; i++) {
            if ((a1[i] instanceof Date) && (a2[i] instanceof Date)) {
                if (DateCompare.compare((Date) a1[i], (Date) a2[i]) != 0) {
                    return false;
                }
            } else if (a1[i] != null) {
                if (!a1[i].equals(a2[i])) {
                    return false;
                }
            } else if (a2[i] != null) {
                return false;
            }
        }

        return true;
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

    /**
     * Database may or may not ignore case when comparing strings. All local
     * Java String comparisons should use this method so we can easily change to
     * match the db's behaviour.
     * 
     * @param left
     * @param right
     * @return
     */
    public static int compareStrings(String left, String right) {
        return left.compareToIgnoreCase(right);
    }
}
