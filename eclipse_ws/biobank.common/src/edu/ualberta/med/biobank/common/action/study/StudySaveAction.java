package edu.ualberta.med.biobank.common.action.study;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.check.UniquePreCheck;
import edu.ualberta.med.biobank.common.action.check.ValueProperty;
import edu.ualberta.med.biobank.common.action.comment.CommentUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.study.StudyCreatePermission;
import edu.ualberta.med.biobank.common.permission.study.StudyUpdatePermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.GlobalEventAttr;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;

public class StudySaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    public static class SourceSpecimenSaveInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public Integer id = null;
        public Boolean needOriginalVolume;
        public Integer specimenTypeId;

        public SourceSpecimenSaveInfo() {

        }

        public SourceSpecimenSaveInfo(SourceSpecimen sourceSpecimen) {
            this.id = sourceSpecimen.getId();
            this.needOriginalVolume = sourceSpecimen.getNeedOriginalVolume();
            this.specimenTypeId = sourceSpecimen.getSpecimenType().getId();
        }

        public SourceSpecimen populateSourceSpecimen(Study study,
            SourceSpecimen sourceSpecimen,
            SpecimenType specimenType) {
            sourceSpecimen.setId(this.id);
            sourceSpecimen.setNeedOriginalVolume(this.needOriginalVolume);
            sourceSpecimen.setSpecimenType(specimenType);
            sourceSpecimen.setStudy(study);
            return sourceSpecimen;
        }
    }

    public static class AliquotedSpecimenSaveInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public Integer id = null;
        public Integer quantity;
        public BigDecimal volume;
        public ActivityStatus activityStatus;
        public Integer specimenTypeId;

        public AliquotedSpecimenSaveInfo() {

        }

        public AliquotedSpecimenSaveInfo(AliquotedSpecimen aliquotedSpecimen) {
            this.id = aliquotedSpecimen.getId();
            this.quantity = aliquotedSpecimen.getQuantity();
            this.volume = aliquotedSpecimen.getVolume();
            this.activityStatus = aliquotedSpecimen.getActivityStatus();
            this.specimenTypeId = aliquotedSpecimen.getSpecimenType().getId();
        }

        public AliquotedSpecimen populateAliquotedSpecimen(Study study,
            AliquotedSpecimen aliquotedSpecimen, ActivityStatus activityStatus,
            SpecimenType specimenType) {
            aliquotedSpecimen.setId(this.id);
            aliquotedSpecimen.setQuantity(this.quantity);
            aliquotedSpecimen.setVolume(this.volume);
            aliquotedSpecimen.setActivityStatus(activityStatus);
            aliquotedSpecimen.setSpecimenType(specimenType);
            aliquotedSpecimen.setStudy(study);
            return aliquotedSpecimen;
        }
    }

    public static class StudyEventAttrSaveInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public Integer id = null;
        public Integer globalEventAttrId;
        public Boolean required;
        public String permissible;
        public ActivityStatus activityStatus;

        public StudyEventAttrSaveInfo() {

        }

        public StudyEventAttrSaveInfo(StudyEventAttr studyEventAttr) {
            this.id = studyEventAttr.getId();
            this.globalEventAttrId =
                studyEventAttr.getGlobalEventAttr().getId();
            this.required = studyEventAttr.getRequired();
            this.permissible = studyEventAttr.getPermissible();
            this.activityStatus = studyEventAttr.getActivityStatus();
        }

        public StudyEventAttr populateStudyEventAttr(Study study,
            StudyEventAttr studyEventAttr,
            GlobalEventAttr globalEventAttr, ActivityStatus activityStatus) {
            studyEventAttr.setId(this.id);
            studyEventAttr.setGlobalEventAttr(globalEventAttr);
            studyEventAttr.setRequired(this.required);
            studyEventAttr.setPermissible(this.permissible);
            studyEventAttr.setActivityStatus(activityStatus);
            studyEventAttr.setStudy(study);
            return studyEventAttr;
        }
    }

    private Integer id = null;
    private String name;
    private String nameShort;
    private ActivityStatus activityStatus;
    private Set<Integer> siteIds;
    private Set<Integer> contactIds;
    private Collection<SourceSpecimenSaveInfo> sourceSpecimenSaveInfos;
    private Collection<AliquotedSpecimenSaveInfo> aliquotSpecimenSaveInfos;
    private Collection<StudyEventAttrSaveInfo> studyEventAttrSaveInfos;
    private String commentText;
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

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    public void setSiteIds(Set<Integer> siteIds) {
        this.siteIds = siteIds;
    }

    public void setContactIds(Set<Integer> contactIds) {
        this.contactIds = contactIds;
    }

    public void setSourceSpecimenSaveInfo(
        Collection<SourceSpecimenSaveInfo> sourceSpecimenSaveInfos) {
        this.sourceSpecimenSaveInfos = sourceSpecimenSaveInfos;
    }

    public void setAliquotSpecimenSaveInfo(
        Collection<AliquotedSpecimenSaveInfo> aliquotSpecimenSaveInfos) {
        this.aliquotSpecimenSaveInfos = aliquotSpecimenSaveInfos;
    }

    public void setStudyEventAttrSaveInfo(
        Collection<StudyEventAttrSaveInfo> studyEventAttrSaveInfos) {
        this.studyEventAttrSaveInfos = studyEventAttrSaveInfos;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        Permission permission;
        if (id == null)
            permission = new StudyCreatePermission();
        else
            permission = new StudyUpdatePermission(id);
        return permission.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        if (name == null) {
            throw new NullPropertyException(Study.class, StudyPeer.NAME);
        }
        if (nameShort == null) {
            throw new NullPropertyException(Study.class, StudyPeer.NAME_SHORT);
        }
        if (activityStatus == null) {
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
        if (sourceSpecimenSaveInfos == null) {
            throw new NullPropertyException(Study.class,
                "specimen ids cannot be null");
        }
        if (aliquotSpecimenSaveInfos == null) {
            throw new NullPropertyException(Study.class,
                "aliquot ids cannot be null");
        }
        if (studyEventAttrSaveInfos == null) {
            throw new NullPropertyException(Study.class,
                "aliquot ids cannot be null");
        }

        study = context.get(Study.class, id, new Study());

        // check for duplicate name
        List<ValueProperty<Study>> uniqueValProps =
            new ArrayList<ValueProperty<Study>>();
        uniqueValProps.add(new ValueProperty<Study>(StudyPeer.NAME, name));
        new UniquePreCheck<Study>(Study.class, id, uniqueValProps).run(context);

        // check for duplicate name short
        uniqueValProps = new ArrayList<ValueProperty<Study>>();
        uniqueValProps.add(new ValueProperty<Study>(StudyPeer.NAME_SHORT,
            nameShort));
        new UniquePreCheck<Study>(Study.class, id, uniqueValProps).run(context);

        // TODO: version check?

        study.setId(id);
        study.setName(name);
        study.setNameShort(nameShort);
        study.setActivityStatus(activityStatus);

        saveSites(context);
        saveContacts(context);
        saveSourceSpecimens(context);
        saveAliquotedSpecimens(context);
        saveEventAttributes(context);
        saveComment(context);

        context.getSession().saveOrUpdate(study);
        context.getSession().flush();

        return new IdResult(study.getId());
    }

    private void saveSites(ActionContext context) {
        Map<Integer, Site> sites = context.load(Site.class, siteIds);

        SetDifference<Site> sitesDiff =
            new SetDifference<Site>(study.getSiteCollection(), sites.values());
        study.setSiteCollection(sitesDiff.getNewSet());

        // remove this study from sites in removed list
        for (Site site : sitesDiff.getRemoveSet()) {
            Set<Study> siteStudies = site.getStudyCollection();
            if (siteStudies.remove(study)) {
                site.setStudyCollection(siteStudies);
            } else {
                throw new ActionException(
                    "study not found in removed site's collection");
            }
        }
    }

    private void saveContacts(ActionContext context) {
        Map<Integer, Contact> contacts =
            context.load(Contact.class, contactIds);
        SetDifference<Contact> contactsDiff =
            new SetDifference<Contact>(study.getContactCollection(),
                contacts.values());
        study.setContactCollection(contactsDiff.getNewSet());

        for (Contact contact : contactsDiff.getAddSet()) {
            Set<Study> contactStudies = contact.getStudyCollection();
            contactStudies.add(study);
            contact.setStudyCollection(contactStudies);
        }

        // remove this study from contacts in removed list
        for (Contact contact : contactsDiff.getRemoveSet()) {
            Set<Study> contactStudies = contact.getStudyCollection();
            if (contactStudies.remove(study)) {
                contact.setStudyCollection(contactStudies);
            } else {
                throw new ActionException(
                    "study not found in removed site's collection");
            }
        }
    }

    private void saveSourceSpecimens(ActionContext context) {
        Set<SourceSpecimen> newSsCollection = new HashSet<SourceSpecimen>();
        for (SourceSpecimenSaveInfo ssSaveInfo : sourceSpecimenSaveInfos) {
            SourceSpecimen ss;
            if (ssSaveInfo.id == null) {
                ss = new SourceSpecimen();
            } else {
                ss = context.load(SourceSpecimen.class, ssSaveInfo.id);
            }

            newSsCollection.add(ssSaveInfo.populateSourceSpecimen(study, ss,
                context.load(SpecimenType.class,
                    ssSaveInfo.specimenTypeId)));
        }

        // delete source specimens no longer in use
        SetDifference<SourceSpecimen> srcSpcsDiff =
            new SetDifference<SourceSpecimen>(
                study.getSourceSpecimenCollection(), newSsCollection);
        study.setSourceSpecimenCollection(newSsCollection);
        for (SourceSpecimen srcSpc : srcSpcsDiff.getRemoveSet()) {
            context.getSession().delete(srcSpc);
        }
    }

    private void saveAliquotedSpecimens(ActionContext context) {
        Set<AliquotedSpecimen> newAsCollection =
            new HashSet<AliquotedSpecimen>();
        for (AliquotedSpecimenSaveInfo asSaveInfo : aliquotSpecimenSaveInfos) {
            AliquotedSpecimen as;
            if (asSaveInfo.id == null) {
                as = new AliquotedSpecimen();
            } else {
                as = context.load(AliquotedSpecimen.class,
                    asSaveInfo.id);
            }
            newAsCollection.add(asSaveInfo.populateAliquotedSpecimen(study, as,

                asSaveInfo.activityStatus,
                context.load(SpecimenType.class,
                    asSaveInfo.specimenTypeId)));
        }

        SetDifference<AliquotedSpecimen> aqSpcsDiff =
            new SetDifference<AliquotedSpecimen>(
                study.getAliquotedSpecimenCollection(), newAsCollection);
        study.setAliquotedSpecimenCollection(aqSpcsDiff.getNewSet());

        // delete aliquoted specimens no longer in use
        for (AliquotedSpecimen aqSpc : aqSpcsDiff.getRemoveSet()) {
            context.getSession().delete(aqSpc);
        }
    }

    private void saveEventAttributes(ActionContext context) {
        Set<Integer> geAttrIdsUsed = new HashSet<Integer>();
        Set<StudyEventAttr> newEAttrCollection = new HashSet<StudyEventAttr>();
        for (StudyEventAttrSaveInfo eAttrSaveInfo : studyEventAttrSaveInfos) {
            if (geAttrIdsUsed.contains(eAttrSaveInfo.globalEventAttrId)) {
                throw new ActionCheckException(
                    "canot add multiple study event attributes with same global id "
                        + eAttrSaveInfo.globalEventAttrId);
            }
            StudyEventAttr seAttr;
            if (eAttrSaveInfo.id == null) {
                seAttr = new StudyEventAttr();
            } else {
                seAttr = context.load(StudyEventAttr.class,
                    eAttrSaveInfo.id);
            }
            newEAttrCollection.add(eAttrSaveInfo.populateStudyEventAttr(study,
                seAttr, context.load(GlobalEventAttr.class,
                    eAttrSaveInfo.globalEventAttrId),
                eAttrSaveInfo.activityStatus));
            geAttrIdsUsed.add(eAttrSaveInfo.globalEventAttrId);
        }

        SetDifference<StudyEventAttr> attrsDiff =
            new SetDifference<StudyEventAttr>(
                study.getStudyEventAttrCollection(), newEAttrCollection);
        study.setStudyEventAttrCollection(attrsDiff.getNewSet());
        for (StudyEventAttr attr : attrsDiff.getRemoveSet()) {
            context.getSession().delete(attr);
        }
    }

    private void saveComment(ActionContext context) {
        Comment comment = CommentUtil.create(context.getUser(), commentText);
        if (comment != null) {
            context.getSession().save(comment);
            study.getCommentCollection().add(comment);
        }
    }
}
