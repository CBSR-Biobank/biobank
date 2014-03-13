package edu.ualberta.med.biobank.tools.delete;

import jargs.gnu.CmdLineParser.Option;

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
import edu.ualberta.med.biobank.model.Domain;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
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
        + "\tReads options from db.properties file or from system properties.";

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
        } catch (SQLGrammarException e) {
            System.out.println(e.getMessage());
        }
    }

    private StudyDelete(String studyShortName, AppArgs appArgs) {
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
            deleteDispatches();
            deleteStudy();
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

    /**
     * Deletes the dispatches, dispatch specimens and any comments.
     */
    @SuppressWarnings("unchecked")
    private void deleteDispatches() {
        // get list of child specimens in a dispatch
        List<Integer> dispatchIds = session.createCriteria(Dispatch.class, "dispatch")
            .createAlias("dispatch.dispatchSpecimens", "dspecimens")
            .createAlias("dspecimens.specimen", "specimen")
            .createAlias("specimen.parentSpecimen", "pspecimen")
            .createAlias("pspecimen.collectionEvent", "cevent")
            .createAlias("cevent.patient", "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .setProjection(Projections.distinct(Projections.property("dispatch.id")))
            .list();

        // aggregate list of parentc specimens in a dispatch
        dispatchIds.addAll(
            session.createCriteria(Dispatch.class, "dispatch")
                .createAlias("dispatch.dispatchSpecimens", "dspecimens")
                .createAlias("dspecimens.specimen", "specimen")
                .createAlias("specimen.originalCollectionEvent", "cevent")
                .createAlias("cevent.patient", "patient")
                .createAlias("patient.study", "study")
                .add(Restrictions.eq("study.nameShort", studyShortName))
                .setProjection(Projections.distinct(Projections.property("dispatch.id")))
                .list());

        session.beginTransaction();
        for (Integer dispatchId : dispatchIds) {
            log.debug("deleteDispatches: dipatch: {}", dispatchId);
            Dispatch dispatch = (Dispatch) session.load(Dispatch.class, dispatchId);
            session.delete(dispatch);
        }
        session.getTransaction().commit();
    }

    private void deleteChildSpecimens(Specimen parentSpecimen) {
        log.debug("deleteChildSpecimens: deleting child specimens for parent specimen: {}", parentSpecimen.getInventoryId());
        for (Specimen specimen : parentSpecimen.getChildSpecimens()) {
            log.debug("deleteSpecimens: specimen: {}", specimen.getInventoryId());
            session.delete(specimen);
        }
        parentSpecimen.getChildSpecimens().clear();
    }

    private void deleteParentSpecimens(CollectionEvent cevent) {
        session.beginTransaction();
        for (Specimen specimen : cevent.getOriginalSpecimens()) {
            deleteChildSpecimens(specimen);
        }
        session.getTransaction().commit();

        session.beginTransaction();
        for (Specimen specimen : cevent.getOriginalSpecimens()) {
            log.debug("deleteParentSpecimens: specimen: {}", specimen.getInventoryId());
            session.delete(specimen);
        }
        cevent.getOriginalSpecimens().clear();
        session.getTransaction().commit();
    }

    private void deleteCollectionEvents(Patient patient) {
        for (CollectionEvent cevent : patient.getCollectionEvents()) {
            deleteParentSpecimens(cevent);
        }

        session.beginTransaction();
        for (CollectionEvent cevent : patient.getCollectionEvents()) {
            log.debug("deleteCollectionEvents: deleting collection event: {}",
                cevent.getVisitNumber());

            for (EventAttr eventAttr : cevent.getEventAttrs()) {
                log.debug("deleteCollectionEvents: deleting event attr: {} - {}",
                    eventAttr.getId(),
                    eventAttr.getStudyEventAttr().getGlobalEventAttr().getLabel());
                session.delete(eventAttr);
            }

            cevent.getEventAttrs().clear();
            session.delete(cevent);
        }
        patient.getCollectionEvents().clear();
        session.getTransaction().commit();
    }

    private void deletePatients(Study study) {
        for (Patient patient : study.getPatients()) {
            deleteCollectionEvents(patient);
        }

        session.beginTransaction();
        for (Patient patient : study.getPatients()) {
            log.debug("deletePatients: deleting patient: {}", patient.getPnumber());
            session.delete(patient);
        }
        study.getPatients().clear();
        session.getTransaction().commit();
    }

    private void deleteStudy() {
        Study study = (Study) session.createCriteria(Study.class)
            .add(Restrictions.eq("nameShort", studyShortName)).uniqueResult();
        if (study != null) {
            deletePatients(study);

            @SuppressWarnings("unchecked")
            List<Domain> domains = session.createCriteria(Domain.class, "domain")
                .createAlias("domain.studies", "study")
                .add(Restrictions.eq("study.nameShort", studyShortName)).list();

            log.debug("deletePatients: deleting study: {}", study.getName());
            session.beginTransaction();
            for (Site site : study.getSites()) {
                site.getStudies().remove(study);
            }

            for (StudyEventAttr studyEventAttrs : study.getStudyEventAttrs()) {
                session.delete(studyEventAttrs);
            }
            study.getStudyEventAttrs().clear();

            for (Domain domain : domains) {
                domain.getStudies().remove(study);
            }

            session.delete(study);
            session.getTransaction().commit();
        } else {
            System.out.println("Error: study " + studyShortName + " not found in database.");
        }

    }
}
