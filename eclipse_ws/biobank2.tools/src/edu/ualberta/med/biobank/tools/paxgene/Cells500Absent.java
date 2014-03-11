package edu.ualberta.med.biobank.tools.paxgene;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.tools.GenericAppArgs;
import edu.ualberta.med.biobank.tools.utils.HostUrl;

/**
 * Matthe Klassen sent the following email on Jan 10, 2011:
 * 
 * On the weekend we finished pulling all Cells500 samples from our freezer 1 and 2 however there
 * are still 470 samples still listed in biobank as in freezer 1 & 2. I have personally verified
 * that all of these aliquots are no longer in the freezer. Would you be able to change the activity
 * status of all the samples in the attached list to closed and add the comment
 * "Sample was absent during Cell Pull MK."
 * 
 * Thanks
 * 
 * The email has a CSV file attached.
 */
@Deprecated
@SuppressWarnings({ "unused", "nls" })
public class Cells500Absent {

    private static String USAGE =
        "Usage: Cells500Absent [options] CSV_FILE\n\n"
            + "Options\n"
            + "  -H, --host       hostname for BioBank server and MySQL server\n"
            + "  -p, --port       port number for BioBank server\n"
            + "  -u, --user       user name to log into BioBank server\n"
            + "  -w, --password   password to log into BioBank server\n"
            + "  -v, --verbose    shows verbose output\n"
            + "  -h, --help       shows this text\n"; //$NON-NLS-1$

    private static String CLOSED_COMMENT =
        "Sample was absent during Cell Pull MK.";

    private static final Logger LOGGER = Logger.getLogger(Cells500Absent.class
        .getName());

    private BiobankApplicationService appService;

    private static Map<String, String> SPC_COMMENTS =
        new HashMap<String, String>();

    static {
        SPC_COMMENTS.put("1DFS", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1DEY", "sample pull 2011-09-27 RV01AG00BY");
        SPC_COMMENTS.put("1DLU", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1DBH", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1DOW", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1oot", "RV01AG00BY sample request 2011-09-27");
        SPC_COMMENTS.put("1ool", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1peq", "RV01AG00BY sample request 2011-09-27");
        SPC_COMMENTS.put("1pew", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1pje", "RV01AG00BY sample request 2011-09-27");
        SPC_COMMENTS.put("1pfb", "RV01AG00BY sample request 2011-09-27");
        SPC_COMMENTS.put("1DRH", "RV01AG00BC 20100827, removed by ET");
        SPC_COMMENTS.put("1DUY", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1DUK", "RV01AG00BY sample request 2011-09-27");
        SPC_COMMENTS.put("1DRE", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1DUN", "RV01AG00BC 20100827, removed by ET");
        SPC_COMMENTS.put("1DVC", "pulled");
        SPC_COMMENTS.put("1DSG", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1DVQ", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1DQU", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1DUC", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1DSU", "20110215- Pulled RV01AG00BL");
        SPC_COMMENTS.put("1psd", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1pqf", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1qez", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1qer", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1EBQ", "Pulled");
        SPC_COMMENTS.put("1EBB", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1qbc", "RV01AG00BY sample request 2011-09-27");
        SPC_COMMENTS.put("1pwv", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1qaq", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1ofy", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1qbs", "RV01AG00BY sample request 2011-09-27");
        SPC_COMMENTS.put("1qbx", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1qax", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1qix", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1pyd", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1qnt", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1qot", "MK 2011/11/03 - RV01AG00CA pulled");
        SPC_COMMENTS.put("1qry", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1qms", "RV01AG00BY sample request 2011-09-27");
        SPC_COMMENTS.put("1qub", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1qum", "RV01AG00BS 20110630");
        SPC_COMMENTS
            .put(
                "1qxw",
                "20100910: found to be previously pulled when looking for Sample Request RV01AG00BD 20100909 JM");
        SPC_COMMENTS.put("1rlw", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1rlz", "Pulled");
        SPC_COMMENTS.put("1rkm", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1rhl", "Pulled");
        SPC_COMMENTS.put("1rky", "Pulled");
        SPC_COMMENTS.put("1ruu",
            "MK 2011/11/02 - Sample not in listed location");
        SPC_COMMENTS.put("1rrz", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1rvn", "MK 2011/11/03 - RV01AG00CA pulled");
        SPC_COMMENTS.put("1rwd", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1rdi", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1rwv", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1rxq", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1rwy", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1rxg", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1rxm", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1sdv", "RV01AG00BY sample request 2011-09-27");
        SPC_COMMENTS.put("1sgx", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1sdy", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1sfn", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1sgn", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1sqq", "RV01AG00BC 20100827, removed by ET");
        SPC_COMMENTS.put("1sov", "RV01AG00BY sample request 2011-09-27");
        SPC_COMMENTS.put("1ssf", "RV01AG00BY sample request 2011-09-27");
        SPC_COMMENTS.put("1sxh", "pulled 20100909- RV01AG00BD");
        SPC_COMMENTS.put("1svx",
            "MK 2011/11/02 - Sample not in listed location");
        SPC_COMMENTS.put("1swm", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1ssc", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1ssk", "RV01AG00BC 20100827, removed by ET");
        SPC_COMMENTS.put("1tiw", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1tft", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1tkx", "Pulled");
        SPC_COMMENTS.put("1tki", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1tvy", "RV01AG00BY sample request 2011-09-27");
        SPC_COMMENTS.put("1twx", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1tzw", "RV01AG00BY sample request 2011-09-27");
        SPC_COMMENTS.put("1tws", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1tsa", "20110215- Pulled RV01AG00BL");
        SPC_COMMENTS.put("1tss", "RV01AG00BC 20100827, removed by ET");
        SPC_COMMENTS.put("1tvl", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1tvt", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1uce", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1uox", "Pulled");
        SPC_COMMENTS.put("1uur", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1umd", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1unr", "RV01AG00BY sample request 2011-09-27");
        SPC_COMMENTS.put("1ull", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1ukc", "20110215- Pulled RV01AG00BL");
        SPC_COMMENTS
            .put("1umr", "20110215 Found to be missing during pull. AP");
        SPC_COMMENTS.put("1ukr", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1uku", "RV01AG00BC 20100827, removed by ET");
        SPC_COMMENTS.put("1uii", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1uln", "RV01AG00BC 20100827, removed by ET");
        SPC_COMMENTS.put("1uik", "20110215- Pulled RV01AG00BL");
        SPC_COMMENTS.put("1uxj", "sample pull RV01AG00BY 2011-09-27 AL");
        SPC_COMMENTS.put("1uki", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1unb", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1uuy", "sample pull RV01AG00BY 2011-09-27 AL");
        SPC_COMMENTS.put("1uvq", "RV01AG00BC 20100827, removed by ET");
        SPC_COMMENTS.put("1upp", "20110215- Pulled RV01AG00BL");
        SPC_COMMENTS.put("1uqv", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1uta", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1ujt", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1utb", "sample pull RV01AG00BY 2011-09-27 AL");
        SPC_COMMENTS.put("1ums", "sample pull RV01AG00BY 2011-09-27 AL");
        SPC_COMMENTS.put("1utj", "pulled 20100909- RV01AG00BD");
        SPC_COMMENTS.put("1uma", "Pulled");
        SPC_COMMENTS.put("1umw", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1umq", "sample pull RV01AG00BY 2011-09-27 AL");
        SPC_COMMENTS.put("1vfm", "RV01AG00BC 20100827, removed by ET");
        SPC_COMMENTS.put("1FAP", "pulled for request");
        SPC_COMMENTS.put("1FCX", "pulled for request");
        SPC_COMMENTS.put("1FDB", "pulled for request");
        SPC_COMMENTS.put("1FGO", "pulled for request");
        SPC_COMMENTS.put("1FHE", "pulled for request");
        SPC_COMMENTS.put("1FCL", "pulled for request");
        SPC_COMMENTS.put("1vig", "sample pull RV01AG00BY 2011-09-27 AL");
        SPC_COMMENTS.put("1vgw", "RV01AG00BC 20100827, removed by ET");
        SPC_COMMENTS.put("1vis", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1vpu", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1FDG", "pulled for request");
        SPC_COMMENTS.put("1vof", "RV01AG00BC 20100827, removed by ET");
        SPC_COMMENTS.put("1FDR", "pulled for request");
        SPC_COMMENTS.put("1FOK", "pulled for request");
        SPC_COMMENTS.put("1FQE", "pulled for request");
        SPC_COMMENTS.put("1wbl", "Pulled");
        SPC_COMMENTS.put("1wam", "Pulled");
        SPC_COMMENTS.put("1vxf", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1wme", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1wva", "sample pull RV01AG00BY 2011-09-27 AL");
        SPC_COMMENTS.put("1wvb", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1wpt", "RV01AG00BC 20100827, removed by ET");
        SPC_COMMENTS.put("1xfi", "pulled 20100909-RV01AG00BD");
        SPC_COMMENTS.put("1xad", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1xfg", "sample pull RV01AG00BY 2011-09-27 AL");
        SPC_COMMENTS.put("1xca", "MK 2011/11/03 - RV01AG00CA pulled");
        SPC_COMMENTS.put("1xci", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1xbo", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1xgh", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1wzo", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1ycv", "sample pull RV01AG00BY 2011-09-27 AL");
        SPC_COMMENTS.put("1xyw", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1yzr", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1yrb",
            "MK 2011/11/02 - Sample not in listed location");
        SPC_COMMENTS.put("1yui", "missing due to prior pull 20110224 CH");
        SPC_COMMENTS.put("1ytt", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1yrr", "20110215- Pulled RV01AG00BL");
        SPC_COMMENTS.put("1yqx", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1yts", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1ytb", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1zqs", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1zoz", "Pulled");
        SPC_COMMENTS.put("1zqb",
            "MK 2011/11/02 - Sample not in location Listed");
        SPC_COMMENTS.put("1zqn", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1zop", "MK 2011/11/03 - RV01AG00CA pulled");
        SPC_COMMENTS.put("1zpj", "sample pull RV01AG00BY 2011-09-27 AL");
        SPC_COMMENTS.put("1znd", "missing due to prior pull 20110224 CH");
        SPC_COMMENTS.put("1zmz", "Pulled");
        SPC_COMMENTS.put("1zpq", "pulled 20100909- RV01AG00BD");
        SPC_COMMENTS.put("1zrk", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1zqt", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1zno", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1znk", "20110215- Pulled RV01AG00BL");
        SPC_COMMENTS.put("1zue", "missing due to prior pull 20110224 CH");
        SPC_COMMENTS.put("1zwf", "missing due to prior pull 20110224 CH");
        SPC_COMMENTS.put("1ztf", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1zwk", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1zsj", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1zrv", "MK 2011/11/03 - RV01AG00CA pulled");
        SPC_COMMENTS.put("1zwp", "20110215- Pulled RV01AG00BL");
        SPC_COMMENTS.put("1BTF", "Pulled");
        SPC_COMMENTS.put("1BXC", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1BWT", "Pulled");
        SPC_COMMENTS.put("1BVE",
            "MK 2011/11/02 - Sample not in location listed");
        SPC_COMMENTS.put("1BZC", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1BRK", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1BVC", "Pulled RV01AG00BS AP");
        SPC_COMMENTS.put("1BXV", "Pulled RV01AG00BS AP");
        SPC_COMMENTS.put("1BTC", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1CFF", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1CCX", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1CCQ", "MK 2011/11/03 - RV01AG00CA pulled");
        SPC_COMMENTS.put("1FTF", "Pulled");
        SPC_COMMENTS.put("1FUN", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1BSO", "20110215- Pulled RV01AG00BL");
        SPC_COMMENTS.put("1CVF", "20110215- Pulled RV01AG00BL");
        SPC_COMMENTS.put("1DAP", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1CZP", "MK 2011/11/03 - RV01AG00CA pulled");
        SPC_COMMENTS.put("1CVU", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1CXT", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1BSP",
            "MK 2011/11/02 - Sample Not in location listed");
        SPC_COMMENTS.put("1CVH", "missing due to prior pull 20110224 CH");
        SPC_COMMENTS.put("1CGE", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1CVK", "20110215- Pulled RV01AG00BL");
        SPC_COMMENTS.put("1CXO", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1CYC", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1CVE", "RV01AG00BS 20110630");
        SPC_COMMENTS.put("1CVS", "pulled for RV01AG00BM on 20110224 CH");
        SPC_COMMENTS.put("1BSE", "RV01AG00BD Pulled 2010-09-10");
        SPC_COMMENTS.put("1CXG", "Pulled RV01AG00BS AP");
        SPC_COMMENTS.put("1BUE", "RV01AG00BC 20100827");
        SPC_COMMENTS.put("1DKK", "RV01AG00BD Pulled 2010-09-10");
    };

    public Cells500Absent(GenericAppArgs appArgs) throws Exception {
        String[] remainingArgs = appArgs.getRemainingArgs();
        if (remainingArgs.length != 1) {
            System.out.println("CSV file not specified");
            return;
        }

        LOGGER.debug("username: " + appArgs.username);

        String hostUrl = HostUrl.getHostUrl(appArgs.hostname, appArgs.port);

        appService = ServiceConnection.getAppService(hostUrl, appArgs.username,
            appArgs.password);

        processSpecimens(parseCsv(remainingArgs[0]));
    }

    private void processSpecimens(List<SpecimenData> specimenDataList)
        throws Exception {

        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // String todaysDate =
        // dateFormat.format(Calendar.getInstance().getTime());

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

            // TODO: comments are now a collection

            // if (spc.getActivityStatus().getName().equals("Closed")) {
            // if (SPC_COMMENTS.get(specimenData.inventoryId) != null) {
            // String comment = spc.getComment();
            // if (comment == null) {
            // LOGGER.info("specimen with inventory id "
            // + specimenData.inventoryId
            // + " has null for comment");
            // }
            //
            // StringBuffer commentBuf = new StringBuffer(
            // SPC_COMMENTS.get(specimenData.inventoryId))
            // .append("\n").append(comment);
            // spc.setComment(commentBuf.toString());
            // spc.persist();
            // LOGGER
            // .info("fixed comment for " + specimenData.inventoryId);
            // }
            // continue;
            // }
            //
            // spc.setActivityStatus(ActivityStatus.CLOSED);
            // StringBuffer commentBuf = new StringBuffer();
            // String comment = spc.getComment();
            // if (comment != null) {
            // commentBuf.append(comment);
            // if (!comment.isEmpty()) {
            // commentBuf.append("\n");
            // }
            // }
            // commentBuf.append(todaysDate).append(" ").append(CLOSED_COMMENT);
            // spc.setComment(commentBuf.toString());
            // spc.persist();
            // LOGGER
            // .info("fixed activity status for " + specimenData.inventoryId);

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
            GenericAppArgs args = new GenericAppArgs();
            args.parse(argv);
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
