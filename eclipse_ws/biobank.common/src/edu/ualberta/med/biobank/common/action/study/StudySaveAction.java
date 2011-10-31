package edu.ualberta.med.biobank.common.action.study;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.mapping.Collection;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class StudySaveAction implements Action<Integer> {
    private static final long serialVersionUID = 1L;

    private Integer id = null;
    private String name;
    private String nameShort;
    private Integer aStatusId;
    private Set<Integer> contactIds;
    private Set<Integer> sourceSpcIds;
    private Set<Integer> aliquotedSpcTypeIds;

    public static class StudyEventAttrSaveInfo implements Serializable,
        NotAProxy {
        private static final long serialVersionUID = 1L;
        public Integer studyEventAttrId;
        public EventAttrTypeEnum type;
        public String permissible;
    }

    private List<StudyEventAttrSaveInfo> studyEventAttrInfo;

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

    public void setContactIds(Set<Integer> contactIds) {
        this.contactIds = contactIds;
    }

    public void setSourceSpcIds(Set<Integer> sourceSpcIds) {
        this.sourceSpcIds = sourceSpcIds;
    }

    public void setAliquotedSpcTypeIds(Set<Integer> aliquotedSpcTypeIds) {
        this.aliquotedSpcTypeIds = aliquotedSpcTypeIds;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        // FIXME: needs implementation
        return false;
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {
        SessionUtil sessionUtil = new SessionUtil(session);
        Study study = sessionUtil.get(Study.class, id, new Study());

        // TODO: check permission? (can edit site?)

        // TODO: error checks
        // TODO: version check?

        // TODO: LocalizedMessage in Exception?

        study.setName(name);
        study.setNameShort(nameShort);

        ActivityStatus aStatus = sessionUtil.get(ActivityStatus.class,
            aStatusId);
        study.setActivityStatus(aStatus);

        // TODO: set collections based on diffs
        Map<Integer, Contact> contacts = sessionUtil.get(Contact.class,
            contactIds);
        study.setContactCollection(new HashSet<Contact>(contacts.values()));

        // TODO: set collections based on diffs
        Map<Integer, AliquotedSpecimen> aliquotedSpcs = sessionUtil.get(
            AliquotedSpecimen.class,
            aliquotedSpcTypeIds);
        study.setAliquotedSpecimenCollection(new HashSet<AliquotedSpecimen>(
            aliquotedSpcs.values()));

        // TODO: set collections based on diffs
        Map<Integer, SourceSpecimen> sourceSpcs = sessionUtil.get(
            SourceSpecimen.class,
            sourceSpcIds);
        study.setSourceSpecimenCollection(new HashSet<SourceSpecimen>(
            sourceSpcs.values()));

        setStudyEventAttrs(user, session, study);

        session.saveOrUpdate(study);
        session.flush();

        return study.getId();
    }

    private void setStudyEventAttrs(User user, Session session, Study study) {        
        Map<Integer, GlobalEventAttrInfo> studyEventList = new GlobalEventAttrInfoGetAction(
            ).run(user, session);
        
        if (studyEventAttrInfo == null) {
            // remove existing study event attrs?
        }
        
        study.setStudyEventAttrCollection(new Collection<>(studyEventAttrInfo));  
        
    }
}
