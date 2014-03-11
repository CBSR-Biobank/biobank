package edu.ualberta.med.biobank.tools.delete;

import jargs.gnu.CmdLineParser.Option;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
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
public class StudyDelete {

    private static String USAGE = "Usage: study_delete [options] STUDY_SHORT_NAME\n\n"
        + "\tReads options from db.properties file.";

    private static final Logger log = LoggerFactory.getLogger(StudyDelete.class);

    private static class AppArgs extends GenericAppArgs {

        public boolean queriesOnly = false;

        private final Option queriesOpt;

        public AppArgs() {
            super();
            queriesOpt = parser.addBooleanOption('q', "queries");
        }

        @Override
        public void parse(String[] argv) {
            super.parse(argv);
            Boolean q = (Boolean) parser.getOptionValue(queriesOpt);
            if (q != null) {
                this.queriesOnly = q.booleanValue();
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
                System.out.println(USAGE);
                System.exit(0);
            }

            if (args.help) {
                System.out.println(USAGE);
                System.exit(0);
            } else if (args.error) {
                System.out.println(args.errorMsg + "\n" + USAGE);
                System.exit(-1);
            }
            new StudyDelete(remainingArgs[0], args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private StudyDelete(String studyShortName, AppArgs appArgs) throws Exception {
        sessionProvider = new SessionProvider(Mode.RUN);
        session = sessionProvider.openSession();
        this.studyShortName = studyShortName;

        if (appArgs.queriesOnly) {
            getPatientCount();
            getCeventCount();
            getSpecimenCount();
            getDispatchSpecimenCount();
        } else {
            deleteDispatches();
            deleteSpecimens();
        }
    }

    private Number getSpecimenCount() {
        Number count = (Number) session.createCriteria(Specimen.class, "specimen")
            .createAlias("specimen.collectionEvent", "cevent")
            .createAlias("cevent.patient", "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .setProjection(Projections.rowCount())
            .uniqueResult();
        log.debug("getSpecimenCount: study: {}, count: {}", studyShortName, count);
        return count;
    }

    private Number getDispatchSpecimenCount() {
        Number count = (Number) session.createCriteria(DispatchSpecimen.class, "dspecimen")
            .createAlias("dspecimen.specimen", "specimen")
            .createAlias("specimen.collectionEvent", "cevent")
            .createAlias("cevent.patient", "patient")
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

    /**
     * Deletes the dispatches, dispatch specimens and any comments.
     */
    private void deleteDispatches() {
        @SuppressWarnings("unchecked")
        List<Dispatch> dispatches = session.createCriteria(Dispatch.class, "dispatch")
            .createAlias("dispatch.dispatchSpecimens", "dspecimens")
            .createAlias("dspecimens.specimen", "specimen")
            .createAlias("specimen.collectionEvent", "cevent")
            .createAlias("cevent.patient", "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .list();

        session.beginTransaction();
        for (Dispatch dispatch : dispatches) {
            log.debug("deleteDispatches: dipatch: {}", dispatch.getId());
            session.delete(dispatch);
        }
        session.getTransaction().commit();
    }

    private void deleteSpecimens(Set<Specimen> specimens) {
        session.beginTransaction();
        for (Specimen specimen : specimens) {
            log.debug("deleteSpecimens: specimen: {}", specimen.getInventoryId());
            session.delete(specimen);
        }
        session.flush();
        session.getTransaction().commit();
    }

    /**
     * Deletes only the specimens.
     */
    private void deleteSpecimens() {
        @SuppressWarnings("unchecked")
        List<Specimen> allSpecimens = session.createCriteria(Specimen.class, "specimen")
            .createAlias("specimen.collectionEvent", "cevent")
            .createAlias("cevent.patient", "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .list();

        Set<Specimen> parentSpecimens = new HashSet<Specimen>();
        Set<Specimen> childSpecimens = new HashSet<Specimen>();

        for (Specimen specimen : allSpecimens) {
            if (specimen.getParentSpecimen() == null) {
                parentSpecimens.add(specimen);
            } else {
                childSpecimens.add(specimen);
            }
        }

        deleteSpecimens(childSpecimens);
        deleteSpecimens(parentSpecimens);
    }

}
