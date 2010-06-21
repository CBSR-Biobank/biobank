package edu.ualberta.med.biobank.importer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ScanLinkdedImporter {

    private static final Logger logger = Logger
        .getLogger(ScanLinkdedImporter.class.getName());

    private static List<String> invIdNotImported;
    static {
        List<String> aList = new ArrayList<String>();
        aList.add("NUBB681914");
        aList.add("NUBB681950");
        aList.add("NUBB682490");
        aList.add("NUBB682506");
        aList.add("NUBB685789");
        aList.add("NUBB685798");
        aList.add("NUBB691489");
        aList.add("NUBB692008");

        invIdNotImported = Collections.unmodifiableList(aList);
    };

    private static Map<String, String> dupInvIdPnumberErrFix;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("NUAW522752", "AA0204");
        aMap.put("NUBS371089", "2234");
        aMap.put("NUBS371353", "2234");
        aMap.put("NUBS371584", "2234");
        aMap.put("NUBS371672", "2234");
        aMap.put("NUBS371821", "2234");
        aMap.put("NUBT477520", "AA0204");
        aMap.put("NUBT485617", "AA0204");
        aMap.put("NUBT485769", "AA0204");
        aMap.put("NUBT485839", "AA0204");
        aMap.put("NUBT485875", "AA0204");
        aMap.put("NUBT485927", "AA0204");
        aMap.put("NUBT485945", "AA0204");
        aMap.put("NUBT485981", "AA0204");
        aMap.put("NUBT486078", "AA0204");
        aMap.put("NUBT486148", "AA0204");
        aMap.put("NUBT486157", "AA0204");
        aMap.put("NUBT486315", "AA0204");
        aMap.put("NUBT486379", "AA0204");
        aMap.put("NUBT486388", "AA0204");
        aMap.put("NUBT486449", "AA0204");
        aMap.put("NUBT486458", "AA0204");
        aMap.put("NUBT486467", "AA0204");
        aMap.put("NUBT486500", "AA0204");
        aMap.put("NUBT602834", "AA0394");
        aMap.put("NUBT347715", "AA0394");
        aMap.put("NUCU131660", "AA0394");
        aMap.put("NUCU132058", "AA0394");
        aMap.put("NUCU132243", "AA0394");
        aMap.put("NUCU145548", "GR0287");
        aMap.put("NUCU145973", "GR0287");
        aMap.put("NUCU145982", "GR0287");
        aMap.put("NUCU146495", "GR0287");
        aMap.put("NUCU146714", "GR0287");
        aMap.put("NUAD358701", "6832");
        aMap.put("NUAD379500", "6832");
        aMap.put("NUAD492528", "6832");
        aMap.put("NUAD531100", "6832");
        aMap.put("NUAD531854", "6832");
        aMap.put("NUAD532349", "6832");
        aMap.put("NUAD532695", "6832");
        aMap.put("NUAD551654", "6832");
        aMap.put("NUAD551867", "6832");
        aMap.put("NUAD557180", "6832");
        aMap.put("NUAD557658", "6832");

        dupInvIdPnumberErrFix = Collections.unmodifiableMap(aMap);
    };

    // these are duplicates which will not be imported
    private static Map<String, String> dupInvIdSampleTypeErrFix;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("NUBR489123", "Urine");
        aMap.put("NUBT538025", "Cells");

        dupInvIdSampleTypeErrFix = Collections.unmodifiableMap(aMap);
    };

    protected WritableApplicationService appService;
    protected Connection con;
    protected final SiteWrapper site;
    protected int sampleImportCount;

    public ScanLinkdedImporter(WritableApplicationService appService,
        Connection con, final SiteWrapper site) throws Exception {
        this.appService = appService;
        this.con = con;
        this.site = site;
        sampleImportCount = 0;
        doImport();
    }

    public void doImport() throws Exception {
        logger.info("importing scan assigned only aliquots ...");

        String freezerLinkTable = "freezer_link";

        String qryPart = "from " + freezerLinkTable
            + " join sample_list on sample_list.sample_nr=" + freezerLinkTable
            + ".sample_nr join patient_visit on patient_visit.visit_nr="
            + freezerLinkTable + ".visit_nr "
            + "join patient on patient.patient_nr=patient_visit.patient_nr "
            + "join study_list on study_list.study_nr=patient_visit.study_nr "
            + "left join freezer on freezer.inventory_id=" + freezerLinkTable
            + ".inventory_id where fnum is null order by link_date desc";

        Statement s = con.createStatement();
        s.execute("select count(*) " + qryPart);
        ResultSet rs = s.getResultSet();
        rs.next();
        int numSamples = rs.getInt(1);

        s.execute("select study_name_short,dec_chr_nr,"
            + "patient_visit.bb2_pv_id,"
            + "patient_visit.date_received,patient_visit.date_taken,"
            + "sample_name_short," + freezerLinkTable + ".link_date,"
            + freezerLinkTable + ".inventory_id " + qryPart);

        rs = s.getResultSet();
        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        String studyNameShort;
        String patientNr;
        int visitId;
        String dateProcessedStr;
        String dateTakenStr;
        String sampleTypeNameShort;
        String linkDateStr;
        String inventoryId;

        int count = 1;
        while (rs.next()) {
            studyNameShort = rs.getString(1);
            patientNr = rs.getString(2);
            visitId = rs.getInt(3);
            dateProcessedStr = rs.getString(4);
            dateTakenStr = rs.getString(5);
            sampleTypeNameShort = rs.getString(6);
            linkDateStr = rs.getString(7);
            inventoryId = rs.getString(8);

            if (inventoryId.equals("NUAW522752")) {
                System.out.println("here");
            }

            if (invIdNotImported.contains(inventoryId)) {
                logger.info("duplicate kit, not importing inventory id: "
                    + "inventoryId/" + inventoryId);
                continue;
            }

            String dupInvIdPnumberErr = dupInvIdPnumberErrFix.get(inventoryId);
            if ((dupInvIdPnumberErr != null)
                && !dupInvIdPnumberErr.equals(patientNr)) {
                logger.info("ignoring duplicate inventory id: "
                    + "inventoryId/" + inventoryId + " visitId/" + visitId
                    + " patientNr/" + patientNr + " sampleTypeNameShort/"
                    + sampleTypeNameShort);
                continue;
            }

            String dupInvIdSampleTypeErr = dupInvIdSampleTypeErrFix
                .get(inventoryId);
            if ((dupInvIdSampleTypeErr != null)
                && !dupInvIdSampleTypeErr.equals(sampleTypeNameShort)) {
                logger.info("ignoring duplicate inventory id: "
                    + "inventoryId/" + inventoryId + " visitId/" + visitId
                    + " patientNr/" + patientNr + " sampleTypeNameShort/"
                    + sampleTypeNameShort);
                continue;
            }

            AliquotWrapper aliquot = isDuplicateInventoryId(inventoryId,
                visitId, patientNr, sampleTypeNameShort);

            if (aliquot != null) {
                aliquot.setActivityStatus(ActivityStatusWrapper
                    .getActivityStatus(appService, "Flagged"));
                logger.info("flagging duplicate inventory id: "
                    + "inventoryId/" + inventoryId + " visitId/" + visitId
                    + " patientNr/" + patientNr + " sampleTypeNameShort/"
                    + sampleTypeNameShort);

                aliquot.persist();
                continue;
            }

            aliquot = Importer.createAliquot(site, studyNameShort, patientNr,
                visitId, dateProcessedStr, dateTakenStr, inventoryId,
                sampleTypeNameShort, linkDateStr);

            if (aliquot == null) {
                logger
                    .error("could not add aliquot: inventoryId/" + inventoryId
                        + " visitId/" + visitId + " patientNr/" + patientNr
                        + " sampleTypeNameShort/" + sampleTypeNameShort);
                continue;
            }

            if ((dupInvIdPnumberErr != null) || (dupInvIdSampleTypeErr != null)) {
                aliquot.setActivityStatus(ActivityStatusWrapper
                    .getActivityStatus(appService, "Flagged"));
                logger.info("flagging duplicate inventory id: "
                    + "inventoryId/" + inventoryId + " visitId/" + visitId
                    + " patientNr/" + patientNr + " sampleTypeNameShort/"
                    + sampleTypeNameShort);
            }

            ++count;

            logger.debug(String.format(
                "importing scan linked only aliquot %s (%d/%d)", inventoryId,
                count, numSamples));
            ++sampleImportCount;

            aliquot.persist();
        }
    }

    private AliquotWrapper isDuplicateInventoryId(String inventoryId,
        int visitId, String patientNr, String sampleTypeNameShort)
        throws Exception {
        List<AliquotWrapper> aliquots = AliquotWrapper.getAliquotsInSite(
            appService, inventoryId, site);
        sampleTypeNameShort = Importer.getSampleType(sampleTypeNameShort)
            .getNameShort();
        if (aliquots.size() > 0) {
            // check if this is a duplicate
            for (AliquotWrapper a : aliquots) {
                if (a.getPatientVisit().getId().equals(visitId)
                    && a.getPatientVisit().getPatient().getPnumber().equals(
                        patientNr)
                    && a.getSampleType().getNameShort().equals(
                        sampleTypeNameShort)) {
                    return a;
                }
            }
            logger.error("duplicate inventory id found: inventoryId/"
                + inventoryId + " visitId/" + visitId + " patientNr/"
                + patientNr + " sampleTypeNameShort/" + sampleTypeNameShort);
        }
        return null;
    }

    public int getSamplesImported() {
        return sampleImportCount;
    }

}
