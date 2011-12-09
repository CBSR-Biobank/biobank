package edu.ualberta.med.biobank.common.action.study;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.ActionUtil;
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
import edu.ualberta.med.biobank.model.GlobalEventAttr;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.User;

public class StudySaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    public static class SourceSpecimenSaveInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public Integer id = null;
        public Boolean needOriginalVolume;
        public Integer specimenTypeId;
    }

    public static class AliquotedSpecimenSaveInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public Integer id = null;
        public Integer quantity;
        public Double volume;
        public Integer aStatusId;
        public Integer specimenTypeId;
    }

    public static class StudyEventAttrSaveInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public Integer id = null;
        public Integer globalEventAttrId;
        public Boolean required;
        public String permissible;
        public Integer aStatusId;
    }

    private Integer id = null;
    private String name;
    private String nameShort;
    private Integer aStatusId;
    private Set<Integer> siteIds;
    private Set<Integer> contactIds;
    private Collection<SourceSpecimenSaveInfo> sourceSpecimenSaveInfo;
    private Collection<AliquotedSpecimenSaveInfo> aliquotSpecimenSaveInfo;
    private Collection<StudyEventAttrSaveInfo> studyEventAttrSaveInfo;
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

    public void setSourceSpecimenSaveInfo(
        Collection<SourceSpecimenSaveInfo> sourceSpecimenSaveInfo) {
        this.sourceSpecimenSaveInfo = sourceSpecimenSaveInfo;
    }

    public void setAliquotSpecimenSaveInfo(
        Collection<AliquotedSpecimenSaveInfo> aliquotSpecimenSaveInfo) {
        this.aliquotSpecimenSaveInfo = aliquotSpecimenSaveInfo;
    }

    public void setStudyEventAttrSaveInfo(
        Collection<StudyEventAttrSaveInfo> studyEventAttrSaveInfo) {
        this.studyEventAttrSaveInfo = studyEventAttrSaveInfo;
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
        if (sourceSpecimenSaveInfo == null) {
            throw new NullPropertyException(Study.class,
                "specimen ids cannot be null");
        }
        if (aliquotSpecimenSaveInfo == null) {
            throw new NullPropertyException(Study.class,
                "aliquot ids cannot be null");
        }
        if (studyEventAttrSaveInfo == null) {
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
        new UniquePreCheck<Study>(Study.class, id, uniqueValProps).run(user,
            session);

        // check for duplicate name short
        uniqueValProps = new ArrayList<ValueProperty<Study>>();
        uniqueValProps.add(new ValueProperty<Study>(StudyPeer.NAME_SHORT,
            nameShort));
        new UniquePreCheck<Study>(Study.class, id, uniqueValProps).run(user,
            session);

        // TODO: version check?

        study.setId(id);
        study.setName(name);
        study.setNameShort(nameShort);
        study.setActivityStatus(ActionUtil.sessionGet(
            session, ActivityStatus.class, aStatusId));

        saveSites();
        saveContacts();
        saveSourceSpecimens();
        saveAliquotedSpecimens();
        saveEventAttributes();

        session.saveOrUpdate(study);
        session.flush();

        return new IdResult(study.getId());
    }

    private void saveSites() {
        Map<Integer, Site> sites = sessionUtil.load(Site.class, siteIds);

        SetDifference<Site> sitesDiff =
            new SetDifference<Site>(study.getSiteCollection(),
                sites.values());
        study.setSiteCollection(sitesDiff.getNewSet());

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
    }

    private void saveContacts() {
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
        Set<SourceSpecimen> newSsCollection = new HashSet<SourceSpecimen>();
        for (SourceSpecimenSaveInfo ssSaveInfo : sourceSpecimenSaveInfo) {
            SourceSpecimen ss;
            if (ssSaveInfo.id == null) {
                ss = new SourceSpecimen();
                ss.setNeedOriginalVolume(ssSaveInfo.needOriginalVolume);
            } else {
                ss = ActionUtil.sessionGet(session, SourceSpecimen.class,
                    ssSaveInfo.id);
            }
            ss.setStudy(study);
            ss.setSpecimenType(ActionUtil.sessionGet(session,
                SpecimenType.class, ssSaveInfo.specimenTypeId));
            newSsCollection.add(ss);
        }

        // delete source specimens no longer in use
        SetDifference<SourceSpecimen> srcSpcsDiff =
            new SetDifference<SourceSpecimen>(
                study.getSourceSpecimenCollection(),
                newSsCollection);
        study.setSourceSpecimenCollection(srcSpcsDiff.getAddSet());
        for (SourceSpecimen srcSpc : srcSpcsDiff.getRemoveSet()) {
            session.delete(srcSpc);
        }
    }

    private void saveAliquotedSpecimens() {
        Set<AliquotedSpecimen> newAsCollection =
            new HashSet<AliquotedSpecimen>();
        for (AliquotedSpecimenSaveInfo asSaveInfo : aliquotSpecimenSaveInfo) {
            AliquotedSpecimen as;
            if (asSaveInfo.id == null) {
                as = new AliquotedSpecimen();
            } else {
                as = ActionUtil.sessionGet(session, AliquotedSpecimen.class,
                    asSaveInfo.id);
            }
            as.setStudy(study);
            as.setQuantity(asSaveInfo.quantity);
            as.setVolume(asSaveInfo.volume);
            as.setActivityStatus(ActionUtil.sessionGet(session,
                ActivityStatus.class, asSaveInfo.aStatusId));
            as.setSpecimenType(ActionUtil.sessionGet(session,
                SpecimenType.class, asSaveInfo.specimenTypeId));
            newAsCollection.add(as);
        }

        SetDifference<AliquotedSpecimen> aqSpcsDiff =
            new SetDifference<AliquotedSpecimen>(
                study.getAliquotedSpecimenCollection(),
                newAsCollection);

        // delete aliquoted specimens no longer in use
        study.setAliquotedSpecimenCollection(aqSpcsDiff.getAddSet());
        for (AliquotedSpecimen aqSpc : aqSpcsDiff.getRemoveSet()) {
            session.delete(aqSpc);
        }
    }

    private void saveEventAttributes() {
        Set<StudyEventAttr> newEAttrCollection =
            new HashSet<StudyEventAttr>();
        for (StudyEventAttrSaveInfo eAttrSaveInfo : studyEventAttrSaveInfo) {
            StudyEventAttr eAttr;
            if (eAttrSaveInfo.id == null) {
                eAttr = new StudyEventAttr();
            } else {
                eAttr = ActionUtil.sessionGet(session, StudyEventAttr.class,
                    eAttrSaveInfo.id);
            }
            GlobalEventAttr gEAttr = ActionUtil.sessionGet(session,
                GlobalEventAttr.class, eAttrSaveInfo.globalEventAttrId);

            eAttr.setStudy(study);
            eAttr.setLabel(gEAttr.getLabel());
            eAttr.setPermissible(eAttrSaveInfo.permissible);
            eAttr.setRequired(eAttrSaveInfo.required);
            newEAttrCollection.add(eAttr);
        }

        SetDifference<StudyEventAttr> attrsDiff =
            new SetDifference<StudyEventAttr>(
                study.getStudyEventAttrCollection(),
                newEAttrCollection);

        study.setStudyEventAttrCollection(attrsDiff.getAddSet());
        for (StudyEventAttr attr : attrsDiff.getRemoveSet()) {
            session.delete(attr);
        }
    }
}
