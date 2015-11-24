package edu.ualberta.med.biobank.tools.cli.command;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.Domain;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;

public class StudyDeleteUtils {

    private static final Logger LOG = LoggerFactory.getLogger(StudyDeleteUtils.class);

    public static void deleteStudy(Session session, String studyShortName) {
        Study study = (Study) session.createCriteria(Study.class)
            .add(Restrictions.eq("nameShort", studyShortName)).uniqueResult();
        if (study != null) {
            @SuppressWarnings("unchecked")
            List<Domain> domains = session.createCriteria(Domain.class, "domain")
                .createAlias("domain.studies", "study")
                .add(Restrictions.eq("study.nameShort", studyShortName)).list();

            LOG.debug("deletePatients: deleting study: {}", study.getName());

            session.beginTransaction();
            deleteDispatches(session, studyShortName);
            session.getTransaction().commit();

            deletePatients(session, study);

            session.beginTransaction();
            LOG.debug("deleteStudy: deleting study: {}", study.getName());
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

    public static void deletePatient(Session session, String pnumber) {
        Patient patient = (Patient) session.createCriteria(Patient.class)
            .add(Restrictions.eq("pnumber", pnumber)).uniqueResult();

        LOG.info("deleting patient: " + pnumber);

        if (patient != null) {
            session.beginTransaction();
            deleteCollectionEvents(session, patient);
            session.getTransaction().commit();
        } else {
            System.out.println("Error: patient " + pnumber + " not found in database.");
        }
        session.beginTransaction();
        session.delete(patient);
        session.getTransaction().commit();
    }

    private static void deletePatients(Session session, Study study) {
        session.beginTransaction();
        for (Patient patient : study.getPatients()) {
            deleteCollectionEvents(session, patient);
        }

        for (Patient patient : study.getPatients()) {
            LOG.debug("deletePatients: deleting patient: {}", patient.getPnumber());
            session.delete(patient);
        }

        study.getPatients().clear();
        session.getTransaction().commit();
    }

    /**
     * Deletes the dispatches, dispatch specimens and any comments.
     */
    @SuppressWarnings("unchecked")
    private static void deleteDispatches(Session session, String studyShortName) {
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

        for (Integer dispatchId : dispatchIds) {
            LOG.debug("deleteDispatches: dipatch: {}", dispatchId);
            Dispatch dispatch = (Dispatch) session.load(Dispatch.class, dispatchId);
            session.delete(dispatch);
        }
        session.flush();
    }

    private static void deleteChildSpecimens(Session session, Specimen parentSpecimen) {
        LOG.debug("deleteChildSpecimens: deleting child specimens for parent specimen: {}", parentSpecimen.getInventoryId());
        for (Specimen specimen : parentSpecimen.getChildSpecimens()) {
            LOG.debug("deleteSpecimens: specimen: {}", specimen.getInventoryId());
            session.delete(specimen);
        }
        parentSpecimen.getChildSpecimens().clear();
    }

    private static void deleteParentSpecimens(Session session, CollectionEvent cevent) {
        for (Specimen specimen : cevent.getOriginalSpecimens()) {
            deleteChildSpecimens(session, specimen);
        }
        session.flush();

        for (Specimen specimen : cevent.getOriginalSpecimens()) {
            LOG.debug("deleteParentSpecimens: specimen: {}", specimen.getInventoryId());
            session.delete(specimen);
        }
        cevent.getOriginalSpecimens().clear();
        session.flush();
    }

    private static void deleteCollectionEvents(Session session, Patient patient) {
        LOG.info("deleteCollectionEvents: patient: {}", patient.getPnumber());
        for (CollectionEvent cevent : patient.getCollectionEvents()) {
            deleteParentSpecimens(session, cevent);
        }
        session.flush();

        for (CollectionEvent cevent : patient.getCollectionEvents()) {
            LOG.debug("deleteCollectionEvents: deleting collection event: {}",
                cevent.getVisitNumber());

            for (EventAttr eventAttr : cevent.getEventAttrs()) {
                LOG.debug("deleteCollectionEvents: deleting event attr: {} - {}",
                    eventAttr.getId(),
                    eventAttr.getStudyEventAttr().getGlobalEventAttr().getLabel());
                session.delete(eventAttr);
            }

            cevent.setPatient(null);
            cevent.getEventAttrs().clear();
            session.delete(cevent);
        }
        patient.getCollectionEvents().clear();
        session.flush();
    }
}
