package edu.ualberta.med.biobank.common.action.study;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.check.UniquePreCheck;
import edu.ualberta.med.biobank.common.action.check.ValueProperty;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.study.StudyCreatePermission;
import edu.ualberta.med.biobank.common.permission.study.StudyUpdatePermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.User;

public class StudySaveAction implements Action<IdResult> {
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
    private Session session = null;
    private SessionUtil sessionUtil = null;
    private Study study = null;

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

    public void setStudyEventAttrIds(Set<Integer> attrIds) {
        this.studyEventAttrIds = attrIds;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        Permission permission;
        if (id == null)
            permission = new StudyCreatePermission();
        else
            permission = new StudyUpdatePermission(id);
        return permission.isAllowed(user, session);
    }

    @Override
    public IdResult run(User user, Session session) throws ActionException {
        if (name == null) {
            throw new NullPropertyException(Study.class, StudyPeer.NAME);
        }
        if (nameShort == null) {
            throw new NullPropertyException(Study.class, StudyPeer.NAME_SHORT);
        }
        if (aStatusId == null) {
            throw new NullPropertyException(Study.class, "activity status id");
        }
        if (siteIds == null) {
            throw new NullPropertyException(Study.class,
                "site ids cannot be null");
        }
        if (contactIds == null) {
            throw new NullPropertyException(Study.class,
                "contact ids cannot be null");
        }
        if (sourceSpcIds == null) {
            throw new NullPropertyException(Study.class,
                "specimen ids cannot be null");
        }
        if (aliquotSpcIds == null) {
            throw new NullPropertyException(Study.class,
                "aliquot ids cannot be null");
        }
        if (studyEventAttrIds == null) {
            throw new NullPropertyException(Study.class,
                "aliquot ids cannot be null");
        }

        this.session = session;
        sessionUtil = new SessionUtil(session);
        study = sessionUtil.get(Study.class, id, new Study());

        // check for duplicate name
        List<ValueProperty<Study>> uniqueValProps =
            new ArrayList<ValueProperty<Study>>();
        uniqueValProps.add(new ValueProperty<Study>(StudyPeer.NAME, name));
        new UniquePreCheck<Study>(
            new ValueProperty<Study>(StudyPeer.ID, id), Study.class,
            uniqueValProps).run(user, session);

        // check for duplicate name short
        uniqueValProps = new ArrayList<ValueProperty<Study>>();
        uniqueValProps.add(new ValueProperty<Study>(StudyPeer.NAME_SHORT,
            nameShort));
        new UniquePreCheck<Study>(
            new ValueProperty<Study>(StudyPeer.ID, id), Study.class,
            uniqueValProps).run(user, session);

        // TODO: check permission? (can edit site?)
        // TODO: version check?

        saveContacts();
        saveSourceSpecimens();
        saveAliquotedSpecimens();
        saveEventAttributes();

        session.saveOrUpdate(study);
        session.flush();

        return new IdResult(study.getId());
    }

    private void saveContacts() {
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
    }

    private void saveSourceSpecimens() {
        // delete source specimens no longer in use
        Map<Integer, SourceSpecimen> sourceSpcs =
            sessionUtil.load(SourceSpecimen.class, sourceSpcIds);

        SetDifference<SourceSpecimen> srcSpcsDiff =
            new SetDifference<SourceSpecimen>(
                study.getSourceSpecimenCollection(),
                sourceSpcs.values());
        study.setSourceSpecimenCollection(srcSpcsDiff.getAddSet());
        for (SourceSpecimen srcSpc : srcSpcsDiff.getRemoveSet()) {
            session.delete(srcSpc);
        }
    }

    private void saveAliquotedSpecimens() {
        Map<Integer, AliquotedSpecimen> aliquotedSpcs =
            sessionUtil.load(AliquotedSpecimen.class, aliquotSpcIds);
        SetDifference<AliquotedSpecimen> aqSpcsDiff =
            new SetDifference<AliquotedSpecimen>(
                study.getAliquotedSpecimenCollection(),
                aliquotedSpcs.values());

        // delete aliquoted specimens no longer in use
        study.setAliquotedSpecimenCollection(aqSpcsDiff.getAddSet());
        for (AliquotedSpecimen aqSpc : aqSpcsDiff.getRemoveSet()) {
            session.delete(aqSpc);
        }
    }

    private void saveEventAttributes() {
        Map<Integer, StudyEventAttr> studyEventAttrs =
            sessionUtil.load(StudyEventAttr.class, studyEventAttrIds);
        SetDifference<StudyEventAttr> attrsDiff =
            new SetDifference<StudyEventAttr>(
                study.getStudyEventAttrCollection(),
                studyEventAttrs.values());

        study.setStudyEventAttrCollection(attrsDiff.getAddSet());
        for (StudyEventAttr attr : attrsDiff.getRemoveSet()) {
            session.delete(attr);
        }
    }
}
