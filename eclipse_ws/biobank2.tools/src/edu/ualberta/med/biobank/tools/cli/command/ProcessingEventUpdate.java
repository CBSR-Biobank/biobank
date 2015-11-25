package edu.ualberta.med.biobank.tools.cli.command;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventSaveAction;
import edu.ualberta.med.biobank.common.action.search.PEventByWSSearchAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.tools.cli.AppServiceUtils;
import edu.ualberta.med.biobank.tools.cli.CliProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ProcessingEventUpdate extends Command {

    private static final Logger LOG = LoggerFactory.getLogger(SpecimenUpdateActivityStatus.class.getName());

    private static final String NAME = "processing_event_udpate_csv";

    private static final String USAGE = NAME + " CSV_FILE";

    private static final String HELP = "Updates processing events based on worksheet number."
        + " Worksheet number cannot be updated.";

    private BiobankApplicationService appService;

    private Map<String, Site> sites;

    private Map<String, Integer> processingEventIdsByWorksheet;

    private final Set<String> errors = new LinkedHashSet<String>(0);

    public ProcessingEventUpdate(CliProvider cliProvider) {
        super(cliProvider, NAME, HELP, USAGE);
    }

    @Override
    public boolean runCommand(String[] args) {
        if (args.length != 2) {
            System.out.println(USAGE);
            return false;
        }

        final String csvFile = args[1];

        try {
            appService = cliProvider.getAppService();
            sites = AppServiceUtils.getSitesByNameShort(appService);

            final Set<ProcessingEventInfoPojo> pojos = readCsvFile(csvFile);
            processingEventIdsByWorksheet = getProcessingEvents(pojos);

            if (!errors.isEmpty()) {
                for (String error : errors) {
                    System.out.println(error);
                }
                return false;
            }
            LOG.info("no errors found");

            return updateProcessingEvents(pojos);
        } catch (SuperCSVException e) {
            System.out.println("CSV file error: " + e.getCsvContext() + ": " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist: " + csvFile);
        } catch (IOException e) {
            System.out.println("Could not parse csv file: " + csvFile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private Set<ProcessingEventInfoPojo> readCsvFile(String csvFile) throws Exception {
        LOG.info("Reading CSV file: {}", csvFile);

        final Set<ProcessingEventInfoPojo> pojos = new LinkedHashSet<ProcessingEventInfoPojo>(0);

        ICsvBeanReader reader = null;

        try {
            ProcessingEventInfoPojo pojo;

            reader = new CsvBeanReader(
                new FileReader(csvFile), CsvPreference.EXCEL_PREFERENCE);

            String[] csvHeaders = reader.getCSVHeader(true);
            final CellProcessor[] cellProcessors = getCellProcessors();

            while ((pojo = reader.read(
                ProcessingEventInfoPojo.class, csvHeaders, cellProcessors)) != null) {

                pojo.setLineNumber(reader.getLineNumber());
                pojos.add(pojo);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return pojos;
    }

    // cell processors have to be recreated every time the file is read
    public CellProcessor[] getCellProcessors() {
        final CellProcessor[] processors = new CellProcessor[] {
            new Unique(), // inventory ID
            new ParseDate("yyyy-MM-dd HH:mm"),
            new LMinMax(ActivityStatus.ACTIVE.getId(), ActivityStatus.FLAGGED.getId()), // activity
                                                                                        // status
            new StrNotNullOrEmpty()
        };

        return processors;
    }

    private Map<String, Integer> getProcessingEvents(Set<ProcessingEventInfoPojo> pojos)
        throws ApplicationException {
        Map<String, Integer> processingEventIds = new HashMap<String, Integer>();

        LOG.info("Getting processing event information");
        for (ProcessingEventInfoPojo pojo : pojos) {
            Site site = sites.get(pojo.getSiteNameShort());
            PEventByWSSearchAction action = new PEventByWSSearchAction(pojo.getWorksheet(), site);
            List<Integer> result = appService.doAction(action).getList();

            if (result.isEmpty()) {
                errors.add(
                    "processing event with worksheet does not exist: " + pojo.getWorksheet());
                continue;
            }

            if (result.size() > 1) {
                errors.add(
                    "multiple processing events for worksheet: " + pojo.getWorksheet());
                continue;
            }

            processingEventIds.put(pojo.getWorksheet(), result.get(0));
        }

        return processingEventIds;
    }

    private boolean updateProcessingEvents(Set<ProcessingEventInfoPojo> pojos)
        throws ApplicationException {
        LOG.info("Updating processing events");

        Set<Integer> emptySet = new HashSet<Integer>(0);

        for (ProcessingEventInfoPojo pojo : pojos) {
            Site site = sites.get(pojo.getSiteNameShort());
            ProcessingEvent pevent = new ProcessingEvent();
            pevent.setId(processingEventIdsByWorksheet.get(pojo.getWorksheet()));
            ProcessingEventSaveAction action = new ProcessingEventSaveAction(
                pevent,
                site,
                pojo.getCreatedAt(),
                pojo.getWorksheet(),
                ActivityStatus.fromId((int) pojo.getActivityStatus()),
                null,
                emptySet,
                emptySet);
            appService.doAction(action);
            LOG.info("processing event updated for worksheet: " + pojo.getWorksheet());
        }
        LOG.info("processing events updated");
        return true;
    }

    public static class ProcessingEventInfoPojo {
        int lineNumber;
        String worksheet;
        Date createdAt;
        long activityStatus;
        String siteNameShort;

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public String getWorksheet() {
            return worksheet;
        }

        public void setWorksheet(String worksheet) {
            this.worksheet = worksheet;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public long getActivityStatus() {
            return activityStatus;
        }

        public void setActivityStatus(long activityStatus) {
            this.activityStatus = activityStatus;
        }

        public String getSiteNameShort() {
            return siteNameShort;
        }

        public void setSiteNameShort(String siteNameShort) {
            this.siteNameShort = siteNameShort;
        }

    }
}
