package edu.ualberta.med.biobank.tools.delete;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.tools.GenericAppArgs;
import edu.ualberta.med.biobank.tools.SessionProvider;
import edu.ualberta.med.biobank.tools.SessionProvider.Mode;

/**
 * Used to delete a study and all it's patients, collection events, and specimens.
 * 
 * @author loyola
 * 
 */
public class DbDeleteTool {

    private static final String APP_NAME = "biobank_delete_tool";

    private static String APP_DESCRIPTION =
        "Reads options from db.properties file or from system properties.";

    private static final Logger log = LoggerFactory.getLogger(DbDeleteTool.class);

    private static class AppArgs extends GenericAppArgs {

        public boolean queriesOnly = false;

        public AppArgs() {
            super();
            options.addOption("q", "queries", false, "Runs queries on database to get totals.");
        }

        @Override
        public void parse(String[] argv) {
            super.parse(argv);
            if (!error && line.hasOption("q")) {
                this.queriesOnly = true;
            }
        }
    }

    private final SessionProvider sessionProvider;

    private final Session session;

    private final String studyShortName;

    public static void main(String[] argv) {
        try {
            AppArgs args = new AppArgs();
            args.parse(argv);

            String[] remainingArgs = args.getRemainingArgs();
            if (remainingArgs.length < 1) {
                System.out.println("Error: study short name not specified.\n\n");
                System.out.println(APP_DESCRIPTION);
                System.exit(0);
            }

            if (args.help) {
                System.out.println(APP_NAME);
                System.out.println(APP_DESCRIPTION);
                args.printHelp(APP_NAME);
                System.exit(0);
            } else if (args.error) {
                System.out.println(args.errorMsg + "\n" + APP_DESCRIPTION);
                System.exit(-1);
            }
            new DbDeleteTool(remainingArgs[0], args);
        } catch (SQLGrammarException e) {
            System.out.println(e.getMessage());
        }
    }

    private DbDeleteTool(String studyShortName, AppArgs appArgs) {
        sessionProvider = new SessionProvider(Mode.RUN);
        session = sessionProvider.openSession();
        this.studyShortName = studyShortName;

        if (appArgs.queriesOnly) {
            getPatientCount();
            getCeventCount();
            getParentSpecimenCount();
            getChildSpecimenCount();
            getDispatchCount();
            getDispatchSpecimenCount();
        } else {
            StudyDelete.deleteStudy(session, studyShortName);
        }
    }

    private Number getParentSpecimenCount() {
        Number count = (Number) session.createCriteria(Specimen.class, "specimen")
            .createAlias("specimen.collectionEvent", "cevent")
            .createAlias("cevent.patient", "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .setProjection(Projections.rowCount())
            .uniqueResult();
        log.debug("getParentSpecimenCount: study: {}, count: {}", studyShortName, count);
        return count;
    }

    private Number getChildSpecimenCount() {
        Number count = (Number) session.createCriteria(Specimen.class, "specimen")
            .createAlias("specimen.parentSpecimen", "pspecimen")
            .createAlias("pspecimen.collectionEvent", "cevent")
            .createAlias("cevent.patient", "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .setProjection(Projections.rowCount())
            .uniqueResult();
        log.debug("getChildSpecimenCount: study: {}, count: {}", studyShortName, count);
        return count;
    }

    private Number getDispatchCount() {
        @SuppressWarnings("unchecked")
        List<Dispatch> dispatches = session.createCriteria(Dispatch.class, "dispatch")
            .createAlias("dispatch.dispatchSpecimens", "dspecimens")
            .createAlias("dspecimens.specimen", "specimen")
            .createAlias("specimen.parentSpecimen", "pspecimen")
            .createAlias("pspecimen.collectionEvent", "cevent")
            .createAlias("cevent.patient", "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .setProjection(Projections.distinct(Projections.property("dispatch.id")))
            .list();
        log.debug("getDispatchCount: study: {}, count: {}", studyShortName, dispatches.size());
        return dispatches.size();
    }

    private Number getDispatchSpecimenCount() {
        Number count = (Number) session.createCriteria(DispatchSpecimen.class, "dspecimen")
            .createAlias("dspecimen.specimen", "specimen")
            .createAlias("specimen.parentSpecimen", "pspecimen")
            .createAlias("pspecimen.originalCollectionEvent", "ocevent")
            .createAlias("ocevent.patient", "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .setProjection(Projections.rowCount())
            .uniqueResult();
        log.debug("getDispatchSpecimenCount: study: {}, count: {}", studyShortName, count);
        return count;
    }

    private Number getCeventCount() {
        Number count = (Number) session.createCriteria(CollectionEvent.class, "cevent")
            .createAlias("cevent.patient", "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .setProjection(Projections.rowCount())
            .uniqueResult();
        log.debug("getCeventCount: study: {}, count: {}", studyShortName, count);
        return count;
    }

    private Number getPatientCount() {
        Number count = (Number) session.createCriteria(Patient.class, "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .setProjection(Projections.rowCount())
            .uniqueResult();
        log.debug("getPatientCount: study: {}, count: {}", studyShortName, count);
        return count;
    }

}
