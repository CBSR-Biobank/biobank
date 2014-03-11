package edu.ualberta.med.biobank.tools.heartproblem;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.tools.GenericAppArgs;
import edu.ualberta.med.biobank.tools.utils.HostUrl;

/**
 * The Calgary techs for the HEART study were adding PAXgene tubes to collection events, unaware
 * that they should not be doing this. These source specimens must be set back to active activity
 * status.
 * 
 * The list of PAXgene source specimens were sent in an email to Elizabeth Taylor on Dec 2, 2011.
 * 
 */
@SuppressWarnings("nls")
@Deprecated
public class CalgaryPaxgeneFix {

    private static String USAGE = "Usage: calgarypaxgenefix [options]\n\n"
        + "Options\n"
        + "  -H, --host       hostname for BioBank server and MySQL server\n"
        + "  -p, --port       port number for BioBank server\n"
        + "  -u, --user       user name to log into BioBank server\n"
        + "  -w, --password   password to log into BioBank server\n"
        + "  -v, --verbose    shows verbose output\n"
        + "  -h, --help       shows this text\n"; //$NON-NLS-1$

    private static final Logger LOGGER = Logger
        .getLogger(CalgaryPaxgeneFix.class.getName());

    public static final String[] PAXGENE_INV_IDS = { "CCWK", "CCWL", "DAHK",
        "DAHL", "DAHP", "DAHQ", "DAHR", "DAHS", "DAHT", "DAHU", "DAHV", "DAHW",
        "DAHX", "DAHY", "DAHZ", "DAIA", "DAIG", "DAII", "DAIP", "DAIQ", "DAIU",
        "DAIV", "DAJJ", "DAJK", "DAJM", "DAJN", "DAKD", "DAKE", "DAKL", "DAKM",
        "MNGRWBCF1WZ5", "LIMAM7YCM77I", "O9Q24HNBB286", "WZ8IQ8IFSWWC",
        "1PJKAY6GUEIM", "VQDQSAU4EUZI", "MARDIFNHE9X7", "M50LRM0O9BX1",
        "D1WQPIKSQI49", "2K2JBOLFG1IJ", "D5V7T4SL71O2", "YX28ABA31VY3",
        "45ZA6H21RRX5", "OMCBXMEKLI1Y", "IMRJAMT5C4DQ", "RC3MNYYJ1HL1",
        "PZDG9SA06A06", "2U3O92FA7VN0", "O8WF6X1UC3QM", "40IWWJHQ02H5",
        "16H1BIW2T07T", "ME0A1O4QLO4K", "DAINPV42CMS5", "K95JRWGJMPS3",
        "31CHX7OYVF3W", "HW6OCZH2A94G", "H5YXC5NW5FWI", "A6WYI8I5X2VS",
        "4EF6M0WAMX0K", "LSMAEIRG960Z", "S8D007IDQPBN", "YZ8B0ABNF61O",
        "O3LZH0RFUTSD", "VFHACO879BVQ", "MADU0EBOSKLN", "ZU47Y9MRJJTE",
        "XSJRN8EE8T1S", "J8I7SVA5OESU", "GIC0EP2CSH2F", "2W19FZ2G9XJV",
        "KBCDP3QOHTD7", "SRQO8K031AVH", "9TFH7VW0N0IB", "7WWRQ9M0DQHJ",
        "KGARTZJVZJ63", "E422X4UAPE59", "MR8FT44EXCO2", "OVPEIPHBVDXE",
        "4YIJSDLABY3B", "M5PM0CVK6NJ1", "T1PJ1GJCYGY4", "ASODYRZXZAZ9",
        "JJC661AP1241", "UXGI1JNXM8S0", "XVKV4UNPO6ME", "PEBB89YWGND7",
        "NRDHI2BSOI0N", "XMJPIBEAH0UA", "VJ35OH25MX9S", "OZ0X2S71HX0T",
        "3TG1G028OHZR", "2ZXETJ1OB2MD", "80TXQECO05XU", "H8LTT4R2A4T2",
        "S9OOOZLTFW89", "LKLTICQFB3OG", "TTJXLG1KY9QW", "KWZPUBNG9HNT",
        "4AZKQ046PMNG", "4XZI597UV9HN", "7W83DCMTC0EK", "C70DNX2RRMQJ",
        "DI20IUQ4GLZN", "9A5AVYP44GCS", "ORCAF8Z314UC", "04P4XQYPZGR7",
        "7DCF01HO7ETU", "36HR9AK1DFNB", "3YPRGR4S9J7V", "34EJS9V9S8E7",
        "JLLXS4WMFG0A", "AO6WKF2REFKJ", "QTJL93Q2MCWG", "HIO4ONH4ZI97",
        "98R5BYN6GM8M", "PC7T0QV8TQGA" };

    private final BiobankApplicationService appService;

    public CalgaryPaxgeneFix(GenericAppArgs appArgs) throws Exception {
        LOGGER.debug("username: " + appArgs.username);

        String hostUrl = HostUrl.getHostUrl(appArgs.hostname, appArgs.port);

        appService = ServiceConnection.getAppService(hostUrl, appArgs.username,
            appArgs.password);

        for (String invId : PAXGENE_INV_IDS) {
            SpecimenWrapper spc = SpecimenWrapper
                .getSpecimen(appService, invId);
            if (spc == null) {
                LOGGER.info("specimen with inventory id not found: " + invId);
                continue;
            }

            if (spc.getActivityStatus().getName().equals("Active")) {
                LOGGER.info("specimen with inventory id is already ACTIVE: "
                    + invId);
                continue;
            }

            if (!spc.getActivityStatus().getName().equals("Closed")) {
                throw new Exception("specimen with inv id " + invId
                    + " does not have activity status closed: "
                    + spc.getActivityStatus().getName());
            }
            spc.setActivityStatus(ActivityStatus.ACTIVE);
            spc.persist();
            LOGGER.info("fixed activity status for " + invId);
        }
        LOGGER.info("Done");
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
            new CalgaryPaxgeneFix(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
