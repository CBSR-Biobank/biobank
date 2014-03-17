package edu.ualberta.med.biobank.tools.delete;

import java.util.List;

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

public class StudyCounts {

    private static final Logger log = LoggerFactory.getLogger(StudyCounts.class);

    public static Number getParentSpecimenCount(Session session, String studyShortName) {
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

    public static Number getChildSpecimenCount(Session session, String studyShortName) {
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

    public static Number getDispatchCount(Session session, String studyShortName) {
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

    public static Number getDispatchSpecimenCount(Session session, String studyShortName) {
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

    public static Number getCeventCount(Session session, String studyShortName) {
        Number count = (Number) session.createCriteria(CollectionEvent.class, "cevent")
            .createAlias("cevent.patient", "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .setProjection(Projections.rowCount())
            .uniqueResult();
        log.debug("getCeventCount: study: {}, count: {}", studyShortName, count);
        return count;
    }

    public static Number getPatientCount(Session session, String studyShortName) {
        Number count = (Number) session.createCriteria(Patient.class, "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .setProjection(Projections.rowCount())
            .uniqueResult();
        log.debug("getPatientCount: study: {}, count: {}", studyShortName, count);
        return count;
    }

}
