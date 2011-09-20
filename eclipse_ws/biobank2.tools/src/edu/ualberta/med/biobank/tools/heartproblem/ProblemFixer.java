package edu.ualberta.med.biobank.tools.heartproblem;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import jargs.gnu.CmdLineParser.OptionException;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.tools.hbmpostproc.HbmPostProcess;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

/**
 * Fixes the HEAR study problem where the tech's in Calgary entered processing
 * information into the cbsr-training server instead of the real server.
 * 
 * select ce.* from collection_event ce join patient pt on pt.id=ce.patient_id
 * where pt.pnumber in ('DB0026','DB0074','DB0093','DB0094','DB0095','DB0102');
 * 
 * select spc.* from specimen spc join collection_event ce on
 * ce.id=spc.collection_event_id join patient pt on pt.id=ce.patient_id where
 * pt.pnumber in ('DB0026','DB0074','DB0093','DB0094','DB0095','DB0102') and
 * spc.created_at > '2011-06-18';
 * 
 * The extracted information is then hardcoded here and added to
 * 
 */
public class ProblemFixer {

    private static String USAGE = "Usage: probfix [options]\n\n" + "Options\n"
        + "  -v, --verbose    Shows verbose output";

    private static final Logger LOGGER = Logger.getLogger(HbmPostProcess.class
        .getName());

    private static class AppArgs {
        public boolean verbose = false;
        public String username = null;
        public String password = null;
    }

    public static final String[] PNUMBERS = { "DB0026", "DB0074", "DB0093",
        "DB0094", "DB0095", "DB0102" };

    public static final String CEVENTS_HQL_QUERY = "select cevents"
        + " from edu.ualberta.med.biobank.model.CollectionEvent cevents"
        + " inner join cevents.patient patients "
        + " inner join fetch cevents.allSpecimenCollection as spcs"
        + " inner join fetch spcs.processingEvent pevents "
        + " inner join fetch spcs.childSpecimenCollection childSpcs "
        + " where spcs.createdAt > '2011-06-18' and patients.pnumber=?";

    public ProblemFixer(AppArgs appArgs) throws Exception {

        BiobankApplicationService tsAppService = ServiceConnection
            .getAppService("https://cbsr-training.med.ualberta.ca/biobank",
                appArgs.username, appArgs.password);

        // BiobankApplicationService appService =
        // ServiceConnection.getAppService(
        // "https://cbsr.med.ualberta.ca/biobank", appArgs.username,
        // appArgs.password);

        BiobankApplicationService appService = ServiceConnection
            .getAppService("http://localhost:8080/biobank", appArgs.username,
                appArgs.password);

        SiteWrapper calgarySite = null;
        for (SiteWrapper site : SiteWrapper.getSites(appService)) {
            if (site.getName().equals("Calgary Foothills")) {
                calgarySite = site;
            }
        }

        if (calgarySite == null) {
            throw new Exception("could not find calgary site on main server");
        }

        StudyWrapper heartStudy = null;
        for (StudyWrapper study : calgarySite.getStudyCollection()) {
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

        String eventAttrLabel = heartStudy.getStudyEventAttrLabels()[0];

        for (String pnumber : PNUMBERS) {
            HQLCriteria c = new HQLCriteria(CEVENTS_HQL_QUERY,
                Arrays.asList(new Object[] { pnumber }));
            List<PatientWrapper> patients = new ArrayList<PatientWrapper>();

            for (Object raw : tsAppService.query(c)) {
                CollectionEventWrapper cevent = new CollectionEventWrapper(
                    tsAppService, (CollectionEvent) raw);

                String firstSourceSpecimenInvId = cevent
                    .getOriginalSpecimenCollection(false).get(0)
                    .getInventoryId();

                if (collectionEventExists(appService, firstSourceSpecimenInvId)) {
                    System.out.println("collection event for "
                        + firstSourceSpecimenInvId + " already in database");
                    continue;
                }

                PatientWrapper patient = PatientWrapper.getPatient(appService,
                    pnumber);
                if (patient == null) {
                    patient = new PatientWrapper(appService);
                    patient.setPnumber(pnumber);
                    patient.setCreatedAt(cevent.getPatient().getCreatedAt());
                    patient.persist();
                    patient.reload();
                }

                CollectionEventWrapper newCe = new CollectionEventWrapper(
                    appService);
                newCe.setPatient(patient);
                newCe.setVisitNumber(PatientWrapper.getNextVisitNumber(
                    appService, patient));
                newCe.setComment(cevent.getComment());
                newCe.setEventAttrValue(eventAttrLabel,
                    cevent.getEventAttrValue(eventAttrLabel));
                newCe.setActivityStatus(ActivityStatusWrapper
                    .getActivityStatus(appService, cevent.getActivityStatus()
                        .getName()));
                newCe.persist();
                newCe.reload();

                System.out.println("patient: " + pnumber + ", visit number: "
                    + cevent.getVisitNumber());

                for (SpecimenWrapper spc : cevent
                    .getAllSpecimenCollection(false)) {

                    SpecimenWrapper newSpc = new SpecimenWrapper(appService);
                    newSpc.setInventoryId(spc.getInventoryId());
                    newSpc.setComment(spc.getComment());
                    newSpc.setQuantity(spc.getQuantity());
                    newSpc.setCreatedAt(spc.getCreatedAt());
                    newSpc.setCollectionEvent(newCe);
                    newSpc.setCurrentCenter(calgarySite);
                    newSpc.persist();
                    newSpc.reload();

                    System.out.println("  inventory id: "
                        + spc.getInventoryId() + ", created at: "
                        + spc.getCreatedAt() + ", processing event: "
                        + spc.getProcessingEvent().getId() + " - "
                        + spc.getProcessingEvent().getCreatedAt());
                    for (SpecimenWrapper childSpc : spc
                        .getChildSpecimenCollection(false)) {
                        System.out.println("    child: inventory id: "
                            + childSpc.getInventoryId() + ", created at: "
                            + childSpc.getCreatedAt());
                    }
                }

                patients.add(cevent.getPatient());
            }
        }
    }

    public static final String CEVENT_HQL_QUERY = "select count(cevents)"
        + " from edu.ualberta.med.biobank.model.CollectionEvent cevents"
        + " inner join cevents.allSpecimenCollection as spcs"
        + " where spcs.inventoryId=?";

    private boolean collectionEventExists(BiobankApplicationService appService,
        String inventoryId) throws Exception {
        HQLCriteria c = new HQLCriteria(CEVENT_HQL_QUERY,
            Arrays.asList(new Object[] { inventoryId }));
        Long result = CollectionEventWrapper.getCountResult(appService, c);
        if (result > 1) {
            throw new Exception(
                "invalid count on collection event for specimen inv id "
                    + inventoryId);
        }
        return (result == 1);
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
