package edu.ualberta.med.biobank.tools.paxgene;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.tools.GenericAppArgs;
import edu.ualberta.med.biobank.tools.utils.HostUrl;

/**
 * Matthe Klassen sent the following email on Jan 10, 2011:
 * 
 * On the weekend we finished pulling all Cells500 samples from our freezer 1
 * and 2 however there are still 470 samples still listed in biobank as in
 * freezer 1 & 2. I have personally verified that all of these aliquots are no
 * longer in the freezer. Would you be able to change the activity status of all
 * the samples in the attached list to closed and add the comment
 * "Sample was absent during Cell Pull MK."
 * 
 * Thanks
 * 
 * The email has a CSV file attached.
 */
public class Cells500Absent {

    private static String USAGE = "Usage: Cells500Absent [options] CSV_FILE\n\n"
        + "Options\n"
        + "  -H, --host       hostname for BioBank server and MySQL server\n"
        + "  -p, --port       port number for BioBank server\n"
        + "  -u, --user       user name to log into BioBank server\n"
        + "  -w, --password   password to log into BioBank server\n"
        + "  -v, --verbose    shows verbose output\n"
        + "  -h, --help       shows this text\n"; //$NON-NLS-1$

    private static String CLOSED_COMMENT = "Sample was absent during Cell Pull MK.";

    private static final Logger LOGGER = Logger.getLogger(Cells500Absent.class
        .getName());

    private BiobankApplicationService appService;

    public Cells500Absent(GenericAppArgs appArgs) throws Exception {
        if (appArgs.remainingArgs.length != 1) {
            System.out.println("CSV file not specified");
            return;
        }

        LOGGER.debug("username: " + appArgs.username);

        String hostUrl = HostUrl.getHostUrl(appArgs.hostname, appArgs.port);

        appService = ServiceConnection.getAppService(hostUrl, appArgs.username,
            appArgs.password);

        processSpecimens(parseCsv(appArgs.remainingArgs[0]));
    }

    private void processSpecimens(List<SpecimenData> specimenDataList)
        throws Exception {

        ActivityStatusWrapper closedStatus = ActivityStatusWrapper
            .getActivityStatus(appService,
                ActivityStatusWrapper.CLOSED_STATUS_STRING);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todaysDate = dateFormat.format(Calendar.getInstance().getTime());

        for (SpecimenData specimenData : specimenDataList) {
            SpecimenWrapper spc = SpecimenWrapper.getSpecimen(appService,
                specimenData.inventoryId);

            if (spc == null) {
                LOGGER.info("specimen with inventory id not found: "
                    + specimenData.inventoryId);
                continue;
            }

            if (!spc.getSpecimenType().getNameShort()
                .equals(specimenData.specimenType)) {
                LOGGER.info("specimen with inventory id "
                    + specimenData.inventoryId + "has invalid specimen type: "
                    + specimenData.specimenType);
                continue;
            }

            spc.setActivityStatus(closedStatus);
            StringBuffer comment = new StringBuffer(spc.getComment());
            if (!comment.toString().isEmpty()) {
                comment.append("\n");
            }
            comment.append(todaysDate).append(" ").append(CLOSED_COMMENT);
            spc.setComment(comment.toString());
            spc.persist();
            LOGGER
                .info("fixed activity status for " + specimenData.inventoryId);

        }
    }

    private List<SpecimenData> parseCsv(String filepath) throws Exception {
        FileReader f = new FileReader(filepath);

        ICsvBeanReader reader = new CsvBeanReader(f,
            CsvPreference.STANDARD_PREFERENCE);

        final CellProcessor[] processors = new CellProcessor[] { null, null,
            new ParseDate("yyyy-MM-dd"), null, null }; //$NON-NLS-1$

        List<SpecimenData> specimenDataList = new ArrayList<SpecimenData>();

        String[] nameMapping = new String[] { "containerLabel", "position",
            "timeDrawn", "inventoryId", "specimenType" };

        try {
            reader.getCSVHeader(true);
            SpecimenData specimenData;
            while ((specimenData = reader.read(SpecimenData.class, nameMapping,
                processors)) != null) {
                specimenDataList.add(specimenData);
            }
        } catch (SuperCSVException e) {
            throw new Exception("Parse error at CSV line "
                + reader.getLineNumber() + "\n" + e.getCsvContext()); //$NON-NLS-1$
        } finally {
            reader.close();
        }
        return specimenDataList;
    }

    public static void main(String[] argv) {
        try {
            GenericAppArgs args = new GenericAppArgs(argv);
            if (args.help) {
                System.out.println(USAGE);
                System.exit(0);
            } else if (args.error) {
                System.out.println(args.errorMsg + "\n" + USAGE);
                System.exit(-1);
            }
            new Cells500Absent(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class SpecimenData {
        public String containerLabel;
        public String position;
        public Date timeDrawn;
        public String inventoryId;
        public String specimenType;

        public void setContainerLabel(String containerLabel) {
            this.containerLabel = containerLabel;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public void setTimeDrawn(Date timeDrawn) {
            this.timeDrawn = timeDrawn;
        }

        public void setInventoryId(String inventoryId) {
            this.inventoryId = inventoryId;
        }

        public void setSpecimenType(String specimenType) {
            this.specimenType = specimenType;
        }
    }
}
