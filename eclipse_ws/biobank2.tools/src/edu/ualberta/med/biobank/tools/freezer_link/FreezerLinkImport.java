package edu.ualberta.med.biobank.tools.freezer_link;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.action.search.SpecimenByInventorySearchAction;
import edu.ualberta.med.biobank.common.action.site.SiteGetAllAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction.AliquotedSpecimenInfo;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeGetAllAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetAllAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.tools.GenericAppArgs;
import edu.ualberta.med.biobank.tools.bbpdbconsent.BbpdbSpecimenInfo;
import edu.ualberta.med.biobank.tools.utils.HostUrl;

/**
 * Fixes issue #520.
 * 
 * Some aliquots in the freezer_link table of the BBPDB database were not
 * imported into BioBank.
 * 
 * @author Nelson
 * 
 */
public class FreezerLinkImport {

    // @formatter:off
    private static String USAGE =
        "Usage: bbpdb_freezer_link [options]\n\n"
            + "Options\n"
            + "  -H, --host       hostname for BioBank server and MySQL server\n"
            + "  -p, --port       port number for BioBank server\n"
            + "  -u, --user       user name to log into BioBank server\n"
            + "  -w, --password   password to log into BioBank server\n"
            + "  -v, --verbose    shows verbose output\n"
            + "  -h, --help       shows this text\n"; 
    // @formatter:on

    private static final Logger log = LoggerFactory
        .getLogger(FreezerLinkImport.class.getName());

    public static final String BBPDB_QRY =
        "SELECT stl.study_name_short,clinics.clinic_name,patient.dec_chr_nr,"
            + "pv.date_taken,pv.date_received,pv.worksheet,fl.inventory_id,fl.link_date,sl.sample_name,"
            + "pv.phlebotomist_id,pv.consent_surveillance,pv.consent_genetics "
            + "FROM freezer_link fl "
            + "join patient on patient.patient_nr=fl.patient_nr "
            + "join patient_visit pv on pv.visit_nr=fl.visit_nr "
            + "join sample_list sl on sl.sample_nr=fl.sample_nr "
            + "join study_list stl on stl.study_nr=pv.study_nr "
            + "join clinics on clinics.clinic_site=pv.clinic_site "
            + "left join freezer on freezer.inventory_id=fl.inventory_id "
            + "where freezer.inventory_id is null "
            + "order by patient.dec_chr_nr,pv.date_taken desc";

    private final Connection bbpdbCon;

    private final BiobankApplicationService appService;

    private Set<BbpdbSpecimenInfo> bbpdbSpecimenInfos =
        new HashSet<BbpdbSpecimenInfo>();

    private Set<BbpdbSpecimenInfo> bbpdbSpecimensToAdd =
        new HashSet<BbpdbSpecimenInfo>();

    private Site cbsrSite;

    private List<Study> studies = null;

    private List<SpecimenType> specimenTypes = null;

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

        getBbpdbUnlinkedSpecimens();
        getSpecimensToAdd();
        cbsrSite = getCbsrSiteByNameShort("CBSR");

        if (bbpdbSpecimensToAdd.size() == 0) {
            log.info("no specimens to add");
        }

        addSpecimens();
    }

    private void getBbpdbUnlinkedSpecimens() throws SQLException {
        PreparedStatement ps = bbpdbCon.prepareStatement(BBPDB_QRY);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            BbpdbSpecimenInfo bbpdbSpecimenInfo =
                getSpecimenInfoFromResultSet(rs);
            bbpdbSpecimenInfos.add(bbpdbSpecimenInfo);
        }

        log.debug("got {} specimens from the freezer link table",
            bbpdbSpecimenInfos.size());
    }

    private BbpdbSpecimenInfo getSpecimenInfoFromResultSet(ResultSet rs)
        throws SQLException {
        BbpdbSpecimenInfo bbpdbSpecimenInfo = new BbpdbSpecimenInfo();
        bbpdbSpecimenInfo.setStudyNameShort(rs.getString("study_name_short"));
        bbpdbSpecimenInfo.setClinicName(rs.getString("clinic_name"));
        bbpdbSpecimenInfo.setChrNr(rs.getString("dec_chr_nr"));
        bbpdbSpecimenInfo.setDateTaken(rs.getString("date_taken"));
        bbpdbSpecimenInfo.setDateReceived(rs.getString("date_received"));
        bbpdbSpecimenInfo.setWorksheet(rs.getString("worksheet"));
        bbpdbSpecimenInfo.setInventoryId(rs.getString("inventory_id"));
        bbpdbSpecimenInfo.setLinkDate(rs.getString("link_date"));
        bbpdbSpecimenInfo.setSampleName(rs.getString("sample_name"));
        bbpdbSpecimenInfo.setPhlebotomistId(rs.getString("phlebotomist_id"));
        bbpdbSpecimenInfo.setConsentSurveillance(rs
            .getString("consent_surveillance"));
        bbpdbSpecimenInfo.setConsentGenetics(rs.getString("consent_genetics"));

        return bbpdbSpecimenInfo;
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

    private Study getStudyByNameShort(String nameShort) throws Exception {
        if (studies == null) {
            studies = appService.doAction(new StudyGetAllAction()).getList();
        }

        for (Study study : studies) {
            if (study.getNameShort().equals(nameShort)) {
                return study;
            }
        }

        throw new Exception("Study " + nameShort + " not found");
    }

    private SpecimenType getSpecimenTypeByName(String name)
        throws Exception {
        if (specimenTypes == null) {
            specimenTypes =
                appService.doAction(new SpecimenTypeGetAllAction()).getList();
        }

        for (SpecimenType specimenType : specimenTypes) {
            if (specimenType.getName().equals(name)) {
                return specimenType;
            }
        }

        throw new Exception("SpecimenType " + name + " not found");
    }

    /*
     * Stores specimens to add in bbpdbSpecimensToAdd.
     */
    private void getSpecimensToAdd() throws Exception {
        for (BbpdbSpecimenInfo bbpdbSpecimenInfo : bbpdbSpecimenInfos) {
            List<Integer> specimenIds = appService.doAction(
                new SpecimenByInventorySearchAction(bbpdbSpecimenInfo
                    .getInventoryId(), cbsrSite.getId())).getList();

            if (specimenIds.size() > 1) {
                throw new Exception("inventory id " + bbpdbSpecimenInfo
                    .getInventoryId() + " is in the database multiple times");
            } else if (specimenIds.size() == 1) {
                log.trace("inventory id {} is already in the database",
                    bbpdbSpecimenInfo.getInventoryId());
            } else {
                log.info("inventory id {} has to be added",
                    bbpdbSpecimenInfo.getInventoryId());
                bbpdbSpecimensToAdd.add(bbpdbSpecimenInfo);
            }
        }
    }

    /*
     * Assumes specimens to add are already in bbpdbSpecimensToAdd.
     */
    private void addSpecimens() throws Exception {
        Study study;

        for (BbpdbSpecimenInfo bbpdbSpecimenInfo : bbpdbSpecimensToAdd) {
            study = getStudyByNameShort(bbpdbSpecimenInfo.getStudyNameShort());

            AliquotedSpecimenInfo aqSpcInfo = new AliquotedSpecimenInfo();
            aqSpcInfo.inventoryId = bbpdbSpecimenInfo.getInventoryId();
            aqSpcInfo.typeId =
                getSpecimenTypeByName(bbpdbSpecimenInfo.getSampleName())
                    .getId();
            aqSpcInfo.activityStatus = ActivityStatus.ACTIVE;

        }
    }
}
