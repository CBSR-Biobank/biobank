package edu.ualberta.med.biobank.tools.heartproblem;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import jargs.gnu.CmdLineParser.OptionException;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.helpers.SiteQuery;
import edu.ualberta.med.biobank.common.wrappers.util.WrapperUtil;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

/**
 * Fixes Issue #1487 - the HEART study problem where the tech's in Calgary
 * entered processing information into the cbsr-training server instead of the
 * production server.
 * 
 * See the PNUMBERS array below for the patient numbers who's information must
 * be copied over.
 * 
 */
public class ProblemFixer {

    private static String USAGE = "Usage: heartprobfix [options]\n\n"
        + "Options\n" + "  -v, --verbose    Shows verbose output";

    private static final Logger LOGGER = Logger.getLogger(ProblemFixer.class
        .getName());

    private static class AppArgs {
        public boolean verbose = false;
        public String username = null;
        public String password = null;
    }

    public static final String[] PNUMBERS = { "DB0026", "DB0074", "DB0093",
        "DB0094", "DB0095", "DB0102" };

    public static final String CEVENTS_HQL_QUERY = "select cevents" + " from "
        + CollectionEvent.class.getName() + " cevents"
        + " inner join cevents.patient patients "
        + " inner join fetch cevents.allSpecimenCollection as spcs"
        + " left outer join fetch spcs.processingEvent pevents "
        + " left outer join fetch spcs.childSpecimenCollection childSpcs "
        + " inner join fetch spcs.activityStatus "
        + " inner join fetch spcs.specimenType "
        + " where spcs.createdAt > '2011-06-18' and patients.pnumber=?";

    private Map<String, SpecimenTypeWrapper> specimenTypes;

    private BiobankApplicationService tsAppService;

    private BiobankApplicationService appService;

    private StudyWrapper heartStudy;

    private SiteWrapper calgarySiteOnProduction;

    private String heartEventAttrLabel;

    public ProblemFixer(AppArgs appArgs) throws Exception {
        LOGGER.debug("username: " + appArgs.username);

        specimenTypes = new HashMap<String, SpecimenTypeWrapper>();

        tsAppService = ServiceConnection.getAppService(
            "https://cbsr-training.med.ualberta.ca/biobank", appArgs.username,
            appArgs.password);

        appService = ServiceConnection.getAppService(
            "https://10.8.31.50/biobank", appArgs.username, appArgs.password);

        // appService = ServiceConnection
        // .getAppService("http://localhost:8080/biobank", appArgs.username,
        // appArgs.password);

        calgarySiteOnProduction = null;
        for (SiteWrapper site : SiteQuery.getSites(appService)) {
            if (site.getName().equals("Calgary Foothills")) {
                calgarySiteOnProduction = site;
            }
        }

        if (calgarySiteOnProduction == null) {
            throw new Exception("could not find calgary site on main server");
        }

        heartStudy = null;
        for (StudyWrapper study : calgarySiteOnProduction.getStudyCollection()) {
            if (study.getNameShort().equals("HEART")) {
                heartStudy = study;
            }
        }

        if (heartStudy == null) {
            throw new Exception("could not find HEART study on main server");
        }

        if (heartStudy.getStudyEventAttrLabels().length != 1) {
            throw new Exception(
                "unexpected number of event attrs in HEART study");
        }

        for (SpecimenTypeWrapper spcType : SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true)) {
            specimenTypes.put(spcType.getName(), spcType);
        }

        heartEventAttrLabel = heartStudy.getStudyEventAttrLabels()[0];

        processPatients();
    }

    private void processPatients() throws Exception {

        for (String pnumber : PNUMBERS) {
            HQLCriteria c = new HQLCriteria(CEVENTS_HQL_QUERY,
                Arrays.asList(new Object[] { pnumber }));
            List<PatientWrapper> patients = new ArrayList<PatientWrapper>();

            LOGGER.info("processing patient " + pnumber);

            for (Object raw : tsAppService.query(c)) {
                CollectionEventWrapper ceventOnTraining = new CollectionEventWrapper(
                    tsAppService, (CollectionEvent) raw);

                String firstSourceSpecimenInvId = ceventOnTraining
                    .getOriginalSpecimenCollection(false).get(0)
                    .getInventoryId();

                CollectionEventWrapper ceventOnProduction = collectionEventExists(
                    appService, firstSourceSpecimenInvId);

                if (ceventOnProduction == null) {

                    PatientWrapper patient = PatientWrapper.getPatient(
                        appService, pnumber);
                    if (patient == null) {
                        LOGGER.info("creating patient " + pnumber);
                        patient = new PatientWrapper(appService);
                        patient.setPnumber(pnumber);
                        patient.setCreatedAt(ceventOnTraining.getPatient()
                            .getCreatedAt());
                        patient.setStudy(heartStudy);
                        patient.persist();
                        patient.reload();
                    } else {
                        LOGGER.info("patient " + pnumber + " already exists");
                    }

                    patients.add(ceventOnTraining.getPatient());

                    ceventOnProduction = new CollectionEventWrapper(appService);
                    ceventOnProduction.setPatient(patient);
                    ceventOnProduction.setVisitNumber(PatientWrapper
                        .getNextVisitNumber(appService, patient));
                    ceventOnProduction
                        .setComment(ceventOnTraining.getComment());
                    ceventOnProduction
                        .setEventAttrValue(heartEventAttrLabel,
                            ceventOnTraining
                                .getEventAttrValue(heartEventAttrLabel));
                    ceventOnProduction.setActivityStatus(ActivityStatusWrapper
                        .getActivityStatus(appService, ceventOnTraining
                            .getActivityStatus().getName()));
                    ceventOnProduction.persist();
                    ceventOnProduction.reload();

                    LOGGER.info("collection event created: patient/" + pnumber
                        + " visit number/" + ceventOnTraining.getVisitNumber());
                } else {
                    LOGGER.info("collection event for "
                        + firstSourceSpecimenInvId + " already in database");
                }

                for (SpecimenWrapper spc : ceventOnTraining
                    .getOriginalSpecimenCollection(false)) {
                    if (!spc.getCurrentCenter().getNameShort()
                        .equals("Calgary-F")) {
                        throw new Exception("invalid center for specimen: "
                            + spc.getCurrentCenter().getNameShort());
                    }

                    if (spc.getSpecimenPosition() != null) {
                        throw new Exception("specimen already has a position: "
                            + spc.getInventoryId());
                    }

                    if (!spc.getDispatchSpecimenCollection().isEmpty()) {
                        throw new Exception(
                            "specimen has dispatch information: "
                                + spc.getInventoryId());
                    }

                    processSourceSpecimen(spc, ceventOnProduction);
                }
            }
        }
    }

    private void processSourceSpecimen(SpecimenWrapper spcOnTraining,
        CollectionEventWrapper ceventOnProduction) throws Exception {
        // ensure specimen is not present on production server
        SpecimenWrapper spcOnProduction = SpecimenWrapper.getSpecimen(
            appService, spcOnTraining.getInventoryId());

        if (spcOnProduction == null) {
            OriginInfoWrapper oi = new OriginInfoWrapper(appService);
            oi.setCenter(calgarySiteOnProduction);
            oi.persist();

            spcOnProduction = new SpecimenWrapper(appService);
            spcOnProduction.setInventoryId(spcOnTraining.getInventoryId());
            spcOnProduction.setComment(spcOnTraining.getComment());
            spcOnProduction.setQuantity(spcOnTraining.getQuantity());
            spcOnProduction.setCreatedAt(spcOnTraining.getCreatedAt());
            spcOnProduction.setCollectionEvent(ceventOnProduction);
            spcOnProduction.setOriginalCollectionEvent(ceventOnProduction);
            spcOnProduction.setCurrentCenter(calgarySiteOnProduction);
            spcOnProduction.setActivityStatus(ActivityStatusWrapper
                .getActivityStatus(appService, spcOnTraining
                    .getActivityStatus().getName()));
            spcOnProduction.setSpecimenType(specimenTypes.get(spcOnTraining
                .getSpecimenType().getName()));
            spcOnProduction.setOriginInfo(oi);
            spcOnProduction.persist();
            spcOnProduction.reload();

            LOGGER.info("  created source specimen: inventory_id/"
                + spcOnTraining.getInventoryId() + " created_at/"
                + spcOnTraining.getCreatedAt());
        } else {
            LOGGER.info("  source specimen exists: inventory_id/"
                + spcOnTraining.getInventoryId() + " created_at/"
                + spcOnTraining.getCreatedAt());
        }

        ProcessingEventWrapper peventOnTraining = spcOnTraining
            .getProcessingEvent();

        if (peventOnTraining != null) {
            if (!peventOnTraining.getCenter().getNameShort()
                .equals("Calgary-F")) {
                throw new Exception("invalid center for processing event: "
                    + peventOnTraining.getCenter().getNameShort());
            }

            ProcessingEventWrapper peventOnProduction;
            List<ProcessingEventWrapper> peventsOnProduction = ProcessingEventWrapper
                .getProcessingEventsWithDateForCenter(appService,
                    peventOnTraining.getCreatedAt(), calgarySiteOnProduction);

            if (peventsOnProduction.isEmpty()) {
                peventOnProduction = new ProcessingEventWrapper(appService);
                peventOnProduction
                    .setWorksheet(peventOnTraining.getWorksheet());
                peventOnProduction
                    .setCreatedAt(peventOnTraining.getCreatedAt());
                peventOnProduction.setComment(peventOnTraining.getComment());
                peventOnProduction.setCenter(calgarySiteOnProduction);
                peventOnProduction.setActivityStatus(ActivityStatusWrapper
                    .getActivityStatus(appService, peventOnTraining
                        .getActivityStatus().getName()));
                peventOnProduction.persist();
                peventOnProduction.reload();

                LOGGER.info("  created processing event: createdAt/"
                    + peventOnTraining.getCreatedAt() + " worksheet/"
                    + peventOnTraining.getWorksheet());
            } else {
                if (peventsOnProduction.size() > 1) {
                    throw new Exception(
                        "more than one processing event with date "
                            + peventOnTraining.getCreatedAt());
                }
                peventOnProduction = peventsOnProduction.get(0);

                LOGGER.info("  found processing event: createdAt/"
                    + peventOnTraining.getCreatedAt() + " worksheet/"
                    + peventOnTraining.getWorksheet());
            }

            if (spcOnProduction.getProcessingEvent() == null) {
                spcOnProduction.setProcessingEvent(peventOnProduction);
                spcOnProduction.persist();
                spcOnProduction.reload();
            } else if (!spcOnProduction.getProcessingEvent().getCreatedAt()
                .equals(peventOnTraining.getCreatedAt())) {
                throw new Exception("specimen has an invalid processing event"
                    + spcOnProduction.getProcessingEvent().getCreatedAt());
            }

            for (SpecimenWrapper childSpcOnTraining : spcOnTraining
                .getChildSpecimenCollection(false)) {
                if (!childSpcOnTraining.getCurrentCenter().getNameShort()
                    .equals("Calgary-F")) {
                    throw new Exception("invalid center for specimen: "
                        + childSpcOnTraining.getCurrentCenter().getNameShort());
                }

                processAliquotedSpecimen(childSpcOnTraining, spcOnProduction,
                    ceventOnProduction);
            }
        }
    }

    private void processAliquotedSpecimen(SpecimenWrapper spcOnTraining,
        SpecimenWrapper parentSpc, CollectionEventWrapper ceventOnProduction)
        throws Exception {
        SpecimenWrapper spcOnProduction = SpecimenWrapper.getSpecimen(
            appService, spcOnTraining.getInventoryId());

        if (spcOnProduction != null) {
            if (!spcOnProduction.getParentSpecimen().getInventoryId()
                .equals(parentSpc.getInventoryId())) {
                throw new Exception("parent specimen mismatch: "
                    + spcOnProduction.getInventoryId());
            }

            if (!spcOnProduction.getCollectionEvent().getVisitNumber()
                .equals(ceventOnProduction.getVisitNumber())) {
                throw new Exception("parent specimen cevent mismatch: "
                    + spcOnProduction.getInventoryId());
            }

            LOGGER.info("  aliquoted specimen exists: inventory_id/"
                + spcOnTraining.getInventoryId() + " created_at/"
                + spcOnTraining.getCreatedAt());
            return;
        }

        OriginInfoWrapper oi = new OriginInfoWrapper(appService);
        oi.setCenter(calgarySiteOnProduction);
        oi.persist();

        spcOnProduction = new SpecimenWrapper(appService);
        spcOnProduction.setInventoryId(spcOnTraining.getInventoryId());
        spcOnProduction.setComment(spcOnTraining.getComment());
        spcOnProduction.setQuantity(spcOnTraining.getQuantity());
        spcOnProduction.setCreatedAt(spcOnTraining.getCreatedAt());
        spcOnProduction.setCollectionEvent(ceventOnProduction);
        spcOnProduction.setCollectionEvent(ceventOnProduction);
        // TODO just to be sure, check that the spcOnTraining current center is
        // calgary
        spcOnProduction.setCurrentCenter(calgarySiteOnProduction);
        spcOnProduction.setParentSpecimen(parentSpc);
        spcOnProduction.setActivityStatus(ActivityStatusWrapper
            .getActivityStatus(appService, spcOnTraining.getActivityStatus()
                .getName()));
        spcOnProduction.setSpecimenType(specimenTypes.get(spcOnTraining
            .getSpecimenType().getName()));
        spcOnProduction.setOriginInfo(oi);
        spcOnProduction.persist();
        spcOnProduction.reload();

        LOGGER.info("    created aliquoted specimen: inventory_id/"
            + spcOnTraining.getInventoryId() + " created_at/"
            + spcOnTraining.getCreatedAt());

    }

    public static final String CEVENT_HQL_QUERY = "select cevents"
        + " from edu.ualberta.med.biobank.model.CollectionEvent cevents"
        + " inner join cevents.allSpecimenCollection as spcs"
        + " where spcs.inventoryId=?";

    private CollectionEventWrapper collectionEventExists(
        BiobankApplicationService appService, String inventoryId)
        throws Exception {
        List<CollectionEvent> rawList = appService.query(new HQLCriteria(
            CEVENT_HQL_QUERY, Arrays.asList(new Object[] { inventoryId })));
        if ((rawList == null) || rawList.isEmpty()) {
            return null;
        }
        if (rawList.size() > 1) {
            throw new Exception(
                "more than one collection event with inventory id "
                    + inventoryId);
        }
        return WrapperUtil.wrapModel(appService, rawList.get(0),
            CollectionEventWrapper.class);
    }

    /*
     * Parses the command line arguments and returns them in an AppArgs object.
     */
    private static AppArgs parseCommandLine(String argv[])
        throws URISyntaxException {
        AppArgs appArgs = new AppArgs();

        CmdLineParser parser = new CmdLineParser();
        Option verboseOpt = parser.addBooleanOption('v', "verbose");

        try {
            parser.parse(argv);
        } catch (OptionException e) {
            LOGGER.info(e.getMessage());
            System.exit(-1);
        }

        Boolean verbose = (Boolean) parser.getOptionValue(verboseOpt);
        if (verbose != null) {
            appArgs.verbose = verbose.booleanValue();
        }

        String[] args = parser.getRemainingArgs();
        if (args.length != 2) {
            LOGGER.info("Error: invalid arguments\n" + USAGE);
            System.exit(-1);
        }

        appArgs.username = args[0];
        appArgs.password = args[1];

        return appArgs;
    }

    public static void main(String[] args) {
        try {
            new ProblemFixer(parseCommandLine(args));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
