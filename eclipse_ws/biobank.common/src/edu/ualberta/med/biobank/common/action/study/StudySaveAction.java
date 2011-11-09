package edu.ualberta.med.biobank.common.action.study;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.User;

public class StudySaveAction implements Action<Integer> {
    private static final long serialVersionUID = 1L;

    private Integer id = null;
    private String name;
    private String nameShort;
    private Integer aStatusId;
    private Set<Integer> siteIds;
    private Set<Integer> contactIds;
    private Set<Integer> sourceSpcIds;
    private Set<Integer> aliquotSpcIds;
    private Set<Integer> studyEventAttrIds;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public void setActivityStatusId(Integer activityStatusId) {
        this.aStatusId = activityStatusId;
    }

    public void setSiteIds(Set<Integer> siteIds) {
        this.siteIds = siteIds;
    }

    public void setContactIds(Set<Integer> contactIds) {
        this.contactIds = contactIds;
    }

    public void setSourceSpcIds(Set<Integer> sourceSpcIds) {
        this.sourceSpcIds = sourceSpcIds;
    }

    public void setAliquotSpcIds(Set<Integer> aliquotSpcIds) {
        this.aliquotSpcIds = aliquotSpcIds;
    }

    public void setStudyEventAttrSaveIds(Set<Integer> attrIds) {
        this.studyEventAttrIds = attrIds;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        // FIXME: needs implementation
        return true;
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {
        if (siteIds == null) {
            throw new NullPointerException("site ids cannot be null");
        }

        if (contactIds == null) {
            throw new NullPointerException("contact ids cannot be null");
        }

        if (sourceSpcIds == null) {
            throw new NullPointerException("specimen ids cannot be null");
        }

        if (aliquotSpcIds == null) {
            throw new NullPointerException("aliquot ids cannot be null");
        }

        if (aStatusId == null) {
            throw new NullPointerException("activity status not specified");
        }

        SessionUtil sessionUtil = new SessionUtil(session);
        Study study = sessionUtil.get(Study.class, id, new Study());

        // TODO: check permission? (can edit site?)

        // TODO: error checks
        // TODO: version check?

        // TODO: LocalizedMessage in Exception?

        study.setId(id);
        study.setName(name);
        study.setNameShort(nameShort);

        ActivityStatus aStatus =
            sessionUtil.load(ActivityStatus.class, aStatusId);
        study.setActivityStatus(aStatus);

        Map<Integer, Site> sites =
            sessionUtil.load(Site.class, siteIds);

        study.setSiteCollection(new HashSet<Site>(sites.values()));
        SetDifference<Site> sitesDiff =
            new SetDifference<Site>(study.getSiteCollection(),
                sites.values());

        // remove this study from sites in removed list
        for (Site site : sitesDiff.getRemoveSet()) {
            Collection<Study> siteStudies = site.getStudyCollection();
            if (siteStudies.remove(study)) {
                site.setStudyCollection(siteStudies);
            } else {
                throw new ActionException(
                    "study not found in removed site's collection");
            }
        }

        Map<Integer, Contact> contacts =
            sessionUtil.load(Contact.class, contactIds);
        study.setContactCollection(new HashSet<Contact>(contacts.values()));
        SetDifference<Contact> contactsDiff =
            new SetDifference<Contact>(study.getContactCollection(),
                contacts.values());

        for (Contact contact : contactsDiff.getAddSet()) {
            Collection<Study> contactStudies = contact.getStudyCollection();
            contactStudies.add(study);
            contact.setStudyCollection(contactStudies);
        }

        // remove this study from contacts in removed list
        for (Contact contact : contactsDiff.getRemoveSet()) {
            Collection<Study> contactStudies = contact.getStudyCollection();
            if (contactStudies.remove(study)) {
                contact.setStudyCollection(contactStudies);
            } else {
                throw new ActionException(
                    "study not found in removed site's collection");
            }
        }

        Map<Integer, AliquotedSpecimen> aliquotedSpcs =
            sessionUtil.load(AliquotedSpecimen.class, aliquotSpcIds);
        study
            .setAliquotedSpecimenCollection(new HashSet<AliquotedSpecimen>(
                aliquotedSpcs.values()));
        SetDifference<AliquotedSpecimen> aqSpcsDiff =
            new SetDifference<AliquotedSpecimen>(
                study.getAliquotedSpecimenCollection(),
                aliquotedSpcs.values());

        // delete aliquoted specimens no longer in use
        for (AliquotedSpecimen aqSpc : aqSpcsDiff.getRemoveSet()) {
            session.delete(aqSpc);
        }

        Map<Integer, SourceSpecimen> sourceSpcs =
            sessionUtil.load(SourceSpecimen.class, sourceSpcIds);
        study.setSourceSpecimenCollection(new HashSet<SourceSpecimen>(
            sourceSpcs.values()));
        SetDifference<SourceSpecimen> srcSpcsDiff =
            new SetDifference<SourceSpecimen>(
                study.getSourceSpecimenCollection(),
                sourceSpcs.values());

        // delete source specimens no longer in use
        for (SourceSpecimen srcSpc : srcSpcsDiff.getRemoveSet()) {
            session.delete(srcSpc);
        }

        Map<Integer, StudyEventAttr> studyEventAttrs =
            sessionUtil.load(StudyEventAttr.class, studyEventAttrIds);
        study.setStudyEventAttrCollection(new HashSet<StudyEventAttr>(
            studyEventAttrs.values()));
        SetDifference<StudyEventAttr> attrsDiff =
            new SetDifference<StudyEventAttr>(
                study.getStudyEventAttrCollection(),
                studyEventAttrs.values());

        // delete study event attrs no longer in use
        for (StudyEventAttr attr : attrsDiff.getRemoveSet()) {
            session.delete(attr);
        }

        session.saveOrUpdate(study);
        session.flush();

        return study.getId();
    }
}
