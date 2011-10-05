package edu.ualberta.med.biobank.tools.bbpdbconsent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.tools.GenericAppArgs;

/**
 * Used to fix the consent given by a patient on the BBPSP study. When the
 * database was imported from MS Access to MySQL, the consent was incorrectly
 * assigned. The consent that was labeled as "consent_genetics" in the MS Access
 * database should have been converted to "genetic mutation". Instead it was
 * converted as "genetic predisposition".
 * 
 * This class fixes this problem in the current BioBank database.
 * 
 */
public class BbpdbConsent {

    private static String USAGE = "Usage: bbpdbconsent [options]\n\n"
        + "Options\n" + "  -v, --verbose    Shows verbose output";

    private static final Logger LOGGER = Logger.getLogger(BbpdbConsent.class
        .getName());

    private static String BBPDB_CONSENT_QRY = "select patient.dec_chr_nr,"
        + "patient_visit.date_taken,patient_visit.date_received"
        + " from patient_visit, study_list, patient"
        + " where patient_visit.study_nr=study_list.study_nr"
        + " and consent_genetics=1 and study_name_short='BBP'"
        + " and patient_visit.patient_nr=patient.patient_nr"
        + " order by patient_visit.date_received";

    public static class ConsentInfo {
        public String pnumber;
        public Date dateDrawn;
        public Date dateReceived;

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder(pnumber).append(" ")
                .append(dateDrawn).append(" ").append(dateReceived);
            return result.toString();
        }
    }

    private Connection bbpdbCon;

    private BiobankApplicationService appService;

    private List<ConsentInfo> consentData;

    public static void main(String[] argv) {
        try {
            GenericAppArgs args = new GenericAppArgs(argv);
            if (args.error) {
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

        String hostUrl;
        if (args.port == 8080) {
            hostUrl = "http://" + args.hostname + ":8080";
        } else {
            hostUrl = "https://" + args.hostname + ":" + args.port;
        }
        hostUrl += "/biobank";

        LOGGER.info("host url is " + hostUrl);

        appService = ServiceConnection.getAppService(hostUrl, args.username,
            args.password);

        getValidConsentInfo();
        if (consentData.isEmpty()) {
            throw new Exception(
                "no matching consent information found in bbpdb databse");
        }

        fixConsentInfo();
    }

    private void getValidConsentInfo() throws SQLException {
        PreparedStatement ps = bbpdbCon.prepareStatement(BBPDB_CONSENT_QRY);

        consentData = new ArrayList<ConsentInfo>();

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            ConsentInfo ci = new ConsentInfo();
            ci.pnumber = rs.getString(1);
            ci.dateDrawn = DateFormatter.parseToDateTime(rs.getString(2));
            ci.dateReceived = DateFormatter.parseToDateTime(rs.getString(3));

            // LOGGER.info(ci.toString());

            consentData.add(ci);
        }
    }

    private void fixConsentInfo() throws Exception {
        for (ConsentInfo ci : consentData) {
            PatientWrapper pt = PatientWrapper.getPatient(appService,
                ci.pnumber);
            if (!pt.getStudy().getNameShort().equals("BBPSP")) {
                throw new Exception("patient " + ci.pnumber
                    + " does not belong to study BBPSP");
            }

            // LOGGER.info("looking for pt " + ci.pnumber + " with date drawn "
            // + DateFormatter.formatAsDateTime(ci.dateDrawn));

            boolean visitFound = false;

            for (CollectionEventWrapper ce : pt
                .getCollectionEventCollection(false)) {

                if (ci.dateDrawn.equals(ce.getMinSourceSpecimenDate())) {
                    visitFound = true;

                    String consentValue = ce.getEventAttrValue("Consent");

                    if (consentValue.contains("Genetic Predisposition")) {
                        consentValue = consentValue.replace(
                            "Genetic Predisposition", "Genetic Mutation");
                        ce.setEventAttrValue("Consent", consentValue);
                        ce.persist();
                        LOGGER.info("corrected consent for patient "
                            + ci.pnumber
                            + " and cevent with date drawn "
                            + DateFormatter.formatAsDateTime(ce
                                .getMinSourceSpecimenDate()));
                    } else {
                        LOGGER.error("unexpected value for consent: "
                            + consentValue + " patient " + ci.pnumber
                            + ", date drawn: " + ci.dateDrawn);
                    }
                }
            }

            if (!visitFound) {
                LOGGER.error("visit not found for date: "
                    + DateFormatter.formatAsDateTime(ci.dateDrawn));
            }
        }

    }

}
