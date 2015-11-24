package edu.ualberta.med.biobank.tools.cli.command;

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
import edu.ualberta.med.biobank.tools.cli.CliProvider;

public class StudyCountsCommand extends Command {

    private static final Logger LOG = LoggerFactory.getLogger(StudyCountsCommand.class);

    protected static final String NAME = "study_counts";

    protected static final String USAGE = NAME + " SITE_NAME_SHORT";

    protected static final String HELP = "displays counts associated with a study.";

    private Session session;

    public StudyCountsCommand(CliProvider cliProvider) {
        super(cliProvider, NAME, HELP, USAGE);
    }

    @Override
    public boolean runCommand(String[] args) {
        if (args.length != 2) {
            System.out.println("Error: " + USAGE);
            return false;
        }

        final String studyShortName = args[1];
        session = cliProvider.getSessionProvider().openSession();

        getPatientCount(studyShortName);
        getCeventCount(studyShortName);
        getParentSpecimenCount(studyShortName);
        getChildSpecimenCount(studyShortName);
        getDispatchCount(studyShortName);
        getDispatchSpecimenCount(studyShortName);
        return true;
    }

    public Number getParentSpecimenCount(String studyShortName) {
        Number count = (Number) session.createCriteria(Specimen.class, "specimen")
            .createAlias("specimen.collectionEvent", "cevent")
            .createAlias("cevent.patient", "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .setProjection(Projections.rowCount())
            .uniqueResult();
        LOG.info("getParentSpecimenCount: study: {}, count: {}", studyShortName, count);
        return count;
    }

    public Number getChildSpecimenCount(String studyShortName) {
        Number count = (Number) session.createCriteria(Specimen.class, "specimen")
            .createAlias("specimen.parentSpecimen", "pspecimen")
            .createAlias("pspecimen.collectionEvent", "cevent")
            .createAlias("cevent.patient", "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .setProjection(Projections.rowCount())
            .uniqueResult();
        LOG.info("getChildSpecimenCount: study: {}, count: {}", studyShortName, count);
        return count;
    }

    public Number getDispatchCount(String studyShortName) {
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
        LOG.info("getDispatchCount: study: {}, count: {}", studyShortName, dispatches.size());
        return dispatches.size();
    }

    public Number getDispatchSpecimenCount(String studyShortName) {
        Number count = (Number) session.createCriteria(DispatchSpecimen.class, "dspecimen")
            .createAlias("dspecimen.specimen", "specimen")
            .createAlias("specimen.parentSpecimen", "pspecimen")
            .createAlias("pspecimen.originalCollectionEvent", "ocevent")
            .createAlias("ocevent.patient", "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .setProjection(Projections.rowCount())
            .uniqueResult();
        LOG.info("getDispatchSpecimenCount: study: {}, count: {}", studyShortName, count);
        return count;
    }

    public Number getCeventCount(String studyShortName) {
        Number count = (Number) session.createCriteria(CollectionEvent.class, "cevent")
            .createAlias("cevent.patient", "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .setProjection(Projections.rowCount())
            .uniqueResult();
        LOG.info("getCeventCount: study: {}, count: {}", studyShortName, count);
        return count;
    }

    public Number getPatientCount(String studyShortName) {
        Number count = (Number) session.createCriteria(Patient.class, "patient")
            .createAlias("patient.study", "study")
            .add(Restrictions.eq("study.nameShort", studyShortName))
            .setProjection(Projections.rowCount())
            .uniqueResult();
        LOG.info("getPatientCount: study: {}, count: {}", studyShortName, count);
        return count;
    }

}
