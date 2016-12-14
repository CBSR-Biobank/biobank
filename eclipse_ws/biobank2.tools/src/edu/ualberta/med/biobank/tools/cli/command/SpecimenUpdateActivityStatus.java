package edu.ualberta.med.biobank.tools.cli.command;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.common.action.specimen.SpecimenBriefInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenSetGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenUpdateAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.tools.cli.CliProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenUpdateActivityStatus extends Command {

    private static final Logger LOG = LoggerFactory.getLogger(SpecimenUpdateActivityStatus.class.getName());

    private static final String NAME = "specimen_update_activity_status_csv";

    private static final String HELP = "Used to update the activity status on specimens.";

    private static final String USAGE = NAME + " CSV_FILE\n\n"
        + "Used to update the Activity Status on one or more specimens.\n\n"
        + "The CSV file must have the following columns:\n"
        + "  1. Specimen inventory ID\n"
        + "  2. Activity status ID\n"
        + ActivityStatusUsage.USAGE;

    private BiobankApplicationService appService;

    private Site site;

    private Map<String, Specimen> specimens;

    private final Set<String> errors = new LinkedHashSet<String>(0);

    public SpecimenUpdateActivityStatus(CliProvider cliProvider) {
        super(cliProvider, NAME, HELP, USAGE);
    }

    @Override
    public boolean runCommand(String[] args) {
        if (args.length != 3) {
            System.out.println(USAGE);
            return false;
        }

        final String csvFile = args[2];

        try {
            appService = cliProvider.getAppService();

            LOG.info("Reading CSV file: {}", csvFile);

            final Set<SpecimenInfoPojo> pojos = readCsvFile(csvFile);
            specimens = getSpecimens(pojos);

            if (!errors.isEmpty()) {
                for (String error : errors) {
                    System.out.println(error);
                }
                return false;
            }

            LOG.info("no errors found");

            for (SpecimenInfoPojo pojo : pojos) {
                LOG.info("updating activity status on " + pojo.getInventoryId());

                Specimen specimen = specimens.get(pojo.getInventoryId());

                if (specimen == null) {
                    throw new IllegalStateException(
                        "specimen not found: " + pojo.getInventoryId());
                }

                SpecimenUpdateAction action = new SpecimenUpdateAction();
                action.setSpecimenId(specimen.getId());
                action.setSpecimenTypeId(specimen.getSpecimenType().getId());
                action.setCollectionEventId(specimen.getCollectionEvent().getId());
                action.setParentSpecimenId(specimen.getParentSpecimen() == null
                    ? null : specimen.getParentSpecimen().getId());
                action.setActivityStatus(ActivityStatus.fromId((int) pojo.getActivityStatus()));

                appService.doAction(action);
            }

            return true;
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

    private Set<SpecimenInfoPojo> readCsvFile(String filename) throws Exception {
        final Set<SpecimenInfoPojo> pojos = new LinkedHashSet<SpecimenInfoPojo>(0);
        ICsvBeanReader reader = null;

        try {
            SpecimenInfoPojo pojo;

            reader = new CsvBeanReader(
                new FileReader(filename), CsvPreference.EXCEL_PREFERENCE);

            String[] csvHeaders = reader.getCSVHeader(true);
            final CellProcessor[] cellProcessors = getCellProcessors();

            while ((pojo = reader.read(
                SpecimenInfoPojo.class, csvHeaders, cellProcessors)) != null) {

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
            new LMinMax(ActivityStatus.ACTIVE.getId(), ActivityStatus.FLAGGED.getId()), // activity
                                                                                        // status
        };

        return processors;
    }

    // if errors are found, the are added to the errors set
    private Map<String, Specimen> getSpecimens(Set<SpecimenInfoPojo> pojos)
        throws ApplicationException {
        Map<String, Specimen> specimens = new HashMap<String, Specimen>();
        for (SpecimenInfoPojo info : pojos) {

            Set<String> inventoryIds = new HashSet<String>();
            inventoryIds.add(info.getInventoryId());

            List<SpecimenBriefInfo> specimenData = appService.doAction(
                new SpecimenSetGetInfoAction(site, inventoryIds)).getList();

            if (specimenData.isEmpty()) {
                errors.add("Line " + info.getLineNumber()
                    + ": specimen inventory ID not found: "
                    + info.getInventoryId());
                continue;
            }

            Specimen spc = specimenData.get(0).getSpecimen();
            specimens.put(spc.getInventoryId(), spc);
        }
        return specimens;
    }

    public static class SpecimenInfoPojo {
        private int lineNumber;
        String inventoryId;
        long activityStatus;

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public String getInventoryId() {
            return inventoryId;
        }

        public void setInventoryId(String inventoryId) {
            this.inventoryId = inventoryId;
        }

        public long getActivityStatus() {
            return activityStatus;
        }

        public void setActivityStatus(long activityStatus) {
            this.activityStatus = activityStatus;
        }
    }

}
