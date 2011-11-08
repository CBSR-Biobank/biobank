package edu.ualberta.med.biobank.common.action.study;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.CollectionUtils;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
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

    public static class StudyEventAttrSaveInfo implements Serializable,
        NotAProxy {
        private static final long serialVersionUID = 1L;
        public Integer globalEventAttrId;
        public EventAttrTypeEnum type;
        public Boolean required;
        public String permissible;
        public Integer aStatusId;
    }

    private List<StudyEventAttrSaveInfo> studyEventAttrInfos;

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

    public void setStudyEventAttrSaveInfo(List<StudyEventAttrSaveInfo> infos) {
        this.studyEventAttrInfos = infos;
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
            sessionUtil.get(ActivityStatus.class, aStatusId);
        study.setActivityStatus(aStatus);

        // TODO: set collections based on diffs

        Map<Integer, Site> sites =
            sessionUtil.get(Site.class, siteIds);

        // TODO: check for null in sites

        SetDifference<Site> siteDiff =
            new SetDifference<Site>(study.getSiteCollection(), sites.values());

        study.setSiteCollection(sites.values());

        // remove this study from sites in removed list
        for (Site site : siteDiff.getRemoveSet()) {
            Collection<Study> siteStudies = site.getStudyCollection();
            if (siteStudies.remove(study)) {
                site.setStudyCollection(siteStudies);
            } else {
                throw new ActionException(
                    "study not found in removed site's collection");
            }
        }

        Map<Integer, Contact> contacts =
            sessionUtil.get(Contact.class, contactIds);
        study.setContactCollection(new HashSet<Contact>(contacts.values()));

        Map<Integer, AliquotedSpecimen> aliquotedSpcs =
            sessionUtil.get(AliquotedSpecimen.class, aliquotSpcIds);
        study
            .setAliquotedSpecimenCollection(new HashSet<AliquotedSpecimen>(
                aliquotedSpcs.values()));

        Map<Integer, SourceSpecimen> sourceSpcs =
            sessionUtil.get(SourceSpecimen.class, sourceSpcIds);
        study.setSourceSpecimenCollection(new HashSet<SourceSpecimen>(
            sourceSpcs.values()));

        setStudyEventAttrs(user, session, sessionUtil, study);

        session.saveOrUpdate(study);
        session.flush();

        return study.getId();
    }

    private void setStudyEventAttrs(User user, Session session,
        SessionUtil sessionUtil, Study study) {
        Map<Integer, GlobalEventAttrInfo> globalEventAttrInfoList =
            new GlobalEventAttrInfoGetAction().run(user, session);

        if (studyEventAttrInfos == null) {
            // remove existing study event attrs?
        } else {

            for (StudyEventAttrSaveInfo info : studyEventAttrInfos) {
                GlobalEventAttrInfo globalAttrInfo =
                    globalEventAttrInfoList.get(info.globalEventAttrId);

                StudyEventAttr seAttr =
                    sessionUtil.get(StudyEventAttr.class, null,
                        new StudyEventAttr());

                seAttr.setLabel(globalAttrInfo.attr.getLabel());
                seAttr.setPermissible(info.permissible);
                seAttr.setRequired(info.required);
                seAttr.setEventAttrType(globalAttrInfo.attr.getEventAttrType());

                ActivityStatus aStatus =
                    sessionUtil.get(ActivityStatus.class, aStatusId);
                seAttr.setActivityStatus(aStatus);

                seAttr.setStudy(study);

                CollectionUtils.getCollection(study,
                    StudyPeer.STUDY_EVENT_ATTR_COLLECTION).add(seAttr);
            }
        }
    }
}
