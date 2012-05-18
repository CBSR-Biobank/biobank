package edu.ualberta.med.biobank.tools.freezer_link;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientGetCollectionEventInfosAction.PatientCEventInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction.PatientInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientSearchAction;
import edu.ualberta.med.biobank.common.action.patient.PatientSearchAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.common.action.search.SpecimenByInventorySearchAction;
import edu.ualberta.med.biobank.common.action.site.SiteGetAllAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction.AliquotedSpecimenInfo;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeGetAllAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetAllAction;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.tools.GenericAppArgs;
import edu.ualberta.med.biobank.tools.utils.HostUrl;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Fixes issue #520.
 * 
 * Some aliquots in the freezer_link table of the BBPDB database were not
 * imported into BioBank.
 * 
 * Note: time zone conversions are not required. The BBPDB database has times
 * stored as Canada Mountain time. Biobank is accessed via the JBoss server
 * which converts the times to Canada Mountain time for us.
 * 
 * RUN AS: java -Dlog4j.configuration=file:///apth/log4j.properties -jar
 * bbpdb_freezer_link.jar OPTIONS
 * 
 * @author Nelson
 * 
 */
@SuppressWarnings("nls")
public class FreezerLinkImport {

    private static final Logger log = LoggerFactory
        .getLogger(FreezerLinkImport.class.getName());

    private static String USAGE =
        "Usage: bbpdb_freezer_link [options]\n\n"
            + "Options\n"
            + "  -H, --host       hostname for BioBank server and MySQL server\n"
            + "  -p, --port       port number for BioBank server\n"
            + "  -u, --user       user name to log into BioBank server\n"
            + "  -w, --password   password to log into BioBank server\n"
            + "  -v, --verbose    shows verbose output\n"
            + "  -h, --help       shows this text\n";

    public static final String BBPDB_QRY =
        "SELECT stl.study_name_short,clinics.clinic_name,patient.dec_chr_nr,"
            + "pv.date_taken,pv.date_received,fl.link_date,"
            + "pv.worksheet,fl.inventory_id,sl.sample_name,"
            + "pv.phlebotomist_id,pv.consent_surveillance,pv.consent_genetics "
            + "FROM freezer_link fl "
            + "join patient on patient.patient_nr=fl.patient_nr "
            + "join patient_visit pv on pv.visit_nr=fl.visit_nr "
            + "join sample_list sl on sl.sample_nr=fl.sample_nr "
            + "join study_list stl on stl.study_nr=pv.study_nr "
            + "left join clinics on clinics.clinic_site=pv.clinic_site and clinics.study_nr=pv.study_nr "
            + "left join freezer on freezer.inventory_id=fl.inventory_id "
            + "where freezer.inventory_id is null "
            + "order by patient.dec_chr_nr,pv.date_taken desc";

    private final Connection bbpdbCon;

    private final BiobankApplicationService appService;

    private final Set<BbpdbSpecimenInfo> bbpdbSpcInfos =
        new HashSet<BbpdbSpecimenInfo>();

    private final Set<BbpdbSpecimenInfo> bbpdbSpecimensToAdd =
        new HashSet<BbpdbSpecimenInfo>();

    private final Site cbsrSite;

    private Map<String, Study> studiesMap = null;

    private Map<String, SpecimenType> specimenTypesMap = null;

    public static final Map<String, String> NEW_SAMPLE_TYPE_NAME;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("DNA(WBC)", "DNA (WBC)");
        aMap.put("PFP", "PF Plasma");
        aMap.put("Plasma LH", "Lith Hep Plasma");
        aMap.put("RNA Later", "Biopsy, RNA later");
        aMap.put("CDPA Plas", "CDPA Plasma");
        aMap.put("Sodium Azide Urine", "SodiumAzideUrine");
        aMap.put("Paxgene", "Paxgene800");
        NEW_SAMPLE_TYPE_NAME = Collections.unmodifiableMap(aMap);
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
            new FreezerLinkImport(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private FreezerLinkImport(GenericAppArgs args) throws Exception {
        bbpdbCon = DriverManager.getConnection("jdbc:mysql://" + args.hostname
            + ":3306/bbpdb", "dummy", "ozzy498");

        String hostUrl = HostUrl.getHostUrl(args.hostname, args.port);

        log.info("host url is {}", hostUrl);

        appService = ServiceConnection.getAppService(hostUrl, args.username,
            args.password);

        cbsrSite = getCbsrSiteByNameShort("CBSR");

        getBbpdbUnlinkedSpecimens();
        getSpecimensToAdd();

        if (bbpdbSpecimensToAdd.size() == 0) {
            log.info("no specimens to add");
        }
    }

    private void getBbpdbUnlinkedSpecimens() throws SQLException {
        PreparedStatement ps = bbpdbCon.prepareStatement(BBPDB_QRY);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            BbpdbSpecimenInfo bbpdbSpcInfo =
                getSpecimenInfoFromResultSet(rs);
            bbpdbSpcInfos.add(bbpdbSpcInfo);
        }

        log.debug("got {} specimens from the freezer link table",
            bbpdbSpcInfos.size());
    }

    private BbpdbSpecimenInfo getSpecimenInfoFromResultSet(ResultSet rs)
        throws SQLException {
        BbpdbSpecimenInfo bbpdbSpcInfo = new BbpdbSpecimenInfo();
        bbpdbSpcInfo.setStudyNameShort(rs.getString("study_name_short"));
        bbpdbSpcInfo.setClinicName(rs.getString("clinic_name"));
        bbpdbSpcInfo.setChrNr(rs.getString("dec_chr_nr"));
        bbpdbSpcInfo.setDateTaken(rs.getString("date_taken"));
        bbpdbSpcInfo.setDateReceived(rs.getString("date_received"));
        bbpdbSpcInfo.setWorksheet(rs.getString("worksheet"));
        bbpdbSpcInfo.setInventoryId(rs.getString("inventory_id"));
        bbpdbSpcInfo.setLinkDate(rs.getString("link_date"));
        bbpdbSpcInfo.setSampleName(rs.getString("sample_name"));
        bbpdbSpcInfo.setPhlebotomistId(rs.getString("phlebotomist_id"));
        bbpdbSpcInfo.setConsentSurveillance(rs
            .getString("consent_surveillance"));
        bbpdbSpcInfo.setConsentGenetics(rs.getString("consent_genetics"));

        return bbpdbSpcInfo;
    }

    private Site getCbsrSiteByNameShort(String nameShort) throws Exception {
        List<Site> sites =
            appService.doAction(new SiteGetAllAction()).getList();

        for (Site site : sites) {
            if (site.getNameShort().equals(nameShort)) {
                return site;
            }
        }

        throw new Exception("Site " + nameShort + " not found");
    }

    private void getStudies() throws Exception {
        if (studiesMap != null) return;

        studiesMap = new HashMap<String, Study>();

        List<Study> studies =
            appService.doAction(new StudyGetAllAction()).getStudies();

        for (Study study : studies) {
            studiesMap.put(study.getNameShort(), study);
        }
    }

    private Study getBiobankStudy(String bbpdbStudyName) throws Exception {
        getStudies();
        Study study = null;

        if (bbpdbStudyName.equals("BBP")) {
            study = studiesMap.get("BBPSP");
        } else {
            study = studiesMap.get(bbpdbStudyName);
        }

        if (study == null) {
            throw new Exception("Study " + bbpdbStudyName + " not found");
        }

        return study;
    }

    private void getSpecimenTypes() throws ApplicationException {
        if (specimenTypesMap != null) return;

        specimenTypesMap = new HashMap<String, SpecimenType>();

        List<SpecimenType> specimenTypes =
            appService.doAction(new SpecimenTypeGetAllAction()).getList();

        for (SpecimenType specimenType : specimenTypes) {
            specimenTypesMap.put(specimenType.getName(), specimenType);
        }
    }

    private SpecimenType getBiobankSpecimenType(String bbpdbSpcTypeName)
        throws Exception {
        getSpecimenTypes();
        SpecimenType spcType = specimenTypesMap.get(bbpdbSpcTypeName);

        if (spcType == null) {
            spcType = specimenTypesMap
                .get(NEW_SAMPLE_TYPE_NAME.get(bbpdbSpcTypeName));
        }

        if (spcType == null) {
            log.error("SpecimenType " + bbpdbSpcTypeName + " not found");
            return null;
        }

        return spcType;
    }

    /*
     * Stores specimens to add in bbpdbSpecimensToAdd.
     */
    private void getSpecimensToAdd() throws Exception {
        for (BbpdbSpecimenInfo bbpdbSpcInfo : bbpdbSpcInfos) {
            List<Integer> specimenIds = appService.doAction(
                new SpecimenByInventorySearchAction(bbpdbSpcInfo
                    .getInventoryId(), cbsrSite.getId())).getList();

            if (specimenIds.size() > 1) {
                throw new Exception("inventory id " + bbpdbSpcInfo
                    .getInventoryId() + " is in the database multiple times");
            } else if (specimenIds.size() == 1) {
                log.debug("inventory id {} is already in the database",
                    bbpdbSpcInfo.getInventoryId());
            } else {
                processSpecimensToAdd(bbpdbSpcInfo);
            }
        }
    }

    /*
     * Assumes specimens to add are already in bbpdbSpecimensToAdd.
     */
    private void processSpecimensToAdd(BbpdbSpecimenInfo bbpdbSpcInfo)
        throws Exception {
        bbpdbSpecimensToAdd.add(bbpdbSpcInfo);

        Date bbpdbDateTaken =
            DateFormatter.parseToDateTime(bbpdbSpcInfo.getDateTaken());

        SearchedPatientInfo searchedPatientInfo = appService.doAction(
            new PatientSearchAction(bbpdbSpcInfo.getChrNr()));

        PatientInfo patientInfo = appService.doAction(
            new PatientGetInfoAction(searchedPatientInfo.patient.getId()));

        boolean sourceSpecimenFound = false;

        for (PatientCEventInfo ceventInfo : patientInfo.ceventInfos) {
            CEventInfo cEventInfo = appService.doAction(
                new CollectionEventGetInfoAction(ceventInfo.cevent.getId()));

            for (SpecimenInfo spcInfo : cEventInfo.sourceSpecimenInfos) {
                if (DateFormatter.compareDatesToMinutes(bbpdbDateTaken,
                    spcInfo.specimen.getCreatedAt())
                    && spcInfo.specimen.getInventoryId().startsWith(
                        "sw upgrade")) {
                    addSpecimens(bbpdbSpcInfo, spcInfo.specimen,
                        patientInfo.patient.getStudy());
                    sourceSpecimenFound = true;
                    // break here since we do not want to add the specimen
                    // to more than one source specimen with the same date drawn
                    break;
                }
            }

            if (sourceSpecimenFound) break;
        }

        if (!sourceSpecimenFound) {
            log.error(
                "source specimen not found inventory_id={} patient={} date_taken={}",
                new Object[] { bbpdbSpcInfo.getInventoryId(),
                    bbpdbSpcInfo.getChrNr(), bbpdbSpcInfo.getDateTaken() });
        }
    }

    private void addSpecimens(BbpdbSpecimenInfo bbpdbSpcInfo,
        Specimen bbSourceSpecimen, Study study) throws Exception {

        getBiobankStudy(bbpdbSpcInfo.getStudyNameShort());
        SpecimenType bbSpecimenType =
            getBiobankSpecimenType(bbpdbSpcInfo.getSampleName());

        if (bbSpecimenType == null) return;

        AliquotedSpecimenInfo aqSpcInfo = new AliquotedSpecimenInfo();
        aqSpcInfo.inventoryId = bbpdbSpcInfo.getInventoryId();
        aqSpcInfo.typeId = bbSpecimenType.getId();
        aqSpcInfo.activityStatus = ActivityStatus.ACTIVE;
        aqSpcInfo.parentSpecimenId = bbSourceSpecimen.getId();

        appService.doAction(new SpecimenLinkSaveAction(cbsrSite.getId(),
            study.getId(), Arrays.asList(aqSpcInfo)));

        log.info(
            "inventory id {} was added to source specimen '{}' for patient {}",
            new Object[] { bbpdbSpcInfo.getInventoryId(),
                bbSourceSpecimen.getInventoryId(),
                bbSourceSpecimen.getCollectionEvent().getPatient().getPnumber() });
    }
}
