package edu.ualberta.med.biobank.tools.bbpdbconsent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.tools.GenericAppArgs;
import edu.ualberta.med.biobank.tools.utils.HostUrl;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

/**
 * See USAGE constant below for a description of this class.
 * 
 * This class fixes this problem in the current BioBank database.
 * 
 */
public class BbpdbConsent {

    // @formatter:off
    private static String USAGE =
        "Usage: bbpdbconsent [options]\n\n"
            + "Used to fix the consent given by a patient on the BBPSP study.\n"
            + "When the database was imported from MS Access to MySQL, the\n"
            + "consent was incorrectly assigned. The consent that was labeled\n"
            + "as \"consent_genetics\" in the MS Access database should have been\n"
            + "converted to \"genetic mutation\". Instead it was converted as\n"
            + "\"genetic predisposition\".\n\n"
            + "Options\n"
            + "  -H, --host       hostname for BioBank server and MySQL server\n"
            + "  -p, --port       port number for BioBank server\n"
            + "  -u, --user       user name to log into BioBank server\n"
            + "  -w, --password   password to log into BioBank server\n"
            + "  -v, --verbose    shows verbose output\n"
            + "  -h, --help       shows this text\n"; //$NON-NLS-1$
    // @formatter:on

    private static final Logger LOGGER = Logger.getLogger(BbpdbConsent.class
        .getName());

    private static String BBPDB_CONSENT_QRY = "select patient.dec_chr_nr,"
        + "patient_visit.date_taken"
        + " from patient_visit, study_list, patient"
        + " where patient_visit.study_nr=study_list.study_nr"
        + " and consent_genetics=1 and study_name_short='BBP'"
        + " and patient_visit.patient_nr=patient.patient_nr"
        + " order by patient.dec_chr_nr";

    private final Connection bbpdbCon;

    private final BiobankApplicationService appService;

    private Map<String, Map<Date, Boolean>> consentData;

    private List<CollectionEventWrapper> ceventsToCorrect;

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
            new BbpdbConsent(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BbpdbConsent(GenericAppArgs args) throws Exception {
        bbpdbCon = DriverManager.getConnection("jdbc:mysql://" + args.hostname
            + ":3306/bbpdb", "dummy", "ozzy498");

        String hostUrl = HostUrl.getHostUrl(args.hostname, args.port);

        LOGGER.info("host url is " + hostUrl);

        getValidConsentInfo();
        if (consentData.isEmpty()) {
            throw new Exception(
                "no matching consent information found in bbpdb databse");
        }

        appService = ServiceConnection.getAppService(hostUrl, args.username,
            args.password);

        fixConsentInfo();
    }

    private void getValidConsentInfo() throws SQLException {
        PreparedStatement ps = bbpdbCon.prepareStatement(BBPDB_CONSENT_QRY);

        consentData = new HashMap<String, Map<Date, Boolean>>();

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String pnumber = rs.getString(1);
            Date dateDrawn = DateFormatter.parseToDateTime(rs.getString(2));

            Map<Date, Boolean> visitMap = consentData.get(pnumber);
            if (visitMap == null) {
                visitMap = new HashMap<Date, Boolean>();
                consentData.put(pnumber, visitMap);
            }
            visitMap.put(dateDrawn, false);
        }
    }

    private static final String PT_WITH_CE_QRY = "from "
        + Patient.class.getName()
        + " pt inner join fetch pt.collectionEvents cevents"
        + " inner join fetch cevents.originalSpecimens spcs"
        + " inner join fetch cevents.eventAttrs eattrs"
        + " inner join fetch eattrs.studyEventAttr seattrs"
        + " inner join fetch seattrs.eventAttrType where pt.pnumber = ?";

    private void fixConsentInfo() throws Exception {
        ceventsToCorrect = new ArrayList<CollectionEventWrapper>();

        for (Entry<String, Map<Date, Boolean>> entry : consentData.entrySet()) {
            HQLCriteria c = new HQLCriteria(PT_WITH_CE_QRY,
                Arrays.asList(new Object[] { entry.getKey() }));
            List<Patient> rawPatients = appService.query(c);

            if (rawPatients.isEmpty()) {
                throw new Exception("patient " + entry.getKey()
                    + " not found in BioBank database");
            }

            PatientWrapper pt = new PatientWrapper(appService,
                rawPatients.get(0));

            if (!pt.getStudy().getNameShort().equals("BBPSP")) {
                throw new Exception("patient " + entry.getKey()
                    + " does not belong to study BBPSP");
            }

            List<CollectionEventWrapper> cevents = pt
                .getCollectionEventCollection(false);

            if (cevents.isEmpty()) {
                throw new Exception("patient " + entry.getKey()
                    + " does not have any collection events");
            }

            if (entry.getKey().equals("1532")) {
                LOGGER.info("HERE");
            }

            for (CollectionEventWrapper ce : cevents) {
                Date ceDateDrawn = DateFormatter.parseToDateTime(DateFormatter
                    .formatAsDateTime(ce.getMinSourceSpecimenDate()));
                if (entry.getValue().keySet().contains(ceDateDrawn)) {
                    // mark visit as found
                    entry.getValue().put(ceDateDrawn, true);

                    ce.reload();
                    String consentValue = ce.getEventAttrValue("Consent");

                    if (consentValue.contains("Genetic Predisposition")) {
                        ceventsToCorrect.add(ce);
                        LOGGER.info("must update patient "
                            + entry.getKey()
                            + " and cevent with date drawn "
                            + DateFormatter.formatAsDateTime(ce
                                .getMinSourceSpecimenDate()));
                    } else {
                        LOGGER.error("unexpected value for consent: "
                            + consentValue + " patient " + entry.getKey()
                            + ", date drawn: "
                            + DateFormatter.formatAsDateTime(ceDateDrawn));
                    }
                } else {
                    // LOGGER.error("ignoring cevent for patient "
                    // + entry.getKey() + ", date drawn: "
                    // + DateFormatter.formatAsDateTime(ceDateDrawn));
                }
            }
        }

        for (Entry<String, Map<Date, Boolean>> entry : consentData.entrySet()) {
            for (Date dateDrawn : entry.getValue().keySet()) {
                if (!entry.getValue().get(dateDrawn)) {
                    LOGGER.error("visit not found for patient "
                        + entry.getKey() + ", date drawn "
                        + DateFormatter.formatAsDateTime(dateDrawn));
                }
            }
        }

        // for (CollectionEventWrapper ce : ceventsToCorrect) {
        // String consentValue = ce.getEventAttrValue("Consent");
        // consentValue = consentValue.replace("Genetic Predisposition",
        // "Genetic Mutation");
        // ce.setEventAttrValue("Consent", consentValue);
        // ce.persist();
        // LOGGER
        // .info("corrected consent for patient "
        // + ce.getPatient().getPnumber()
        // + " and cevent with date drawn "
        // + DateFormatter.formatAsDateTime(ce
        // .getMinSourceSpecimenDate()));
        //
        // }
    }
}
