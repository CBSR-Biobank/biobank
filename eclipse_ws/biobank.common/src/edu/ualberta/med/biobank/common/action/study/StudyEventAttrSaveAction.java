package edu.ualberta.med.biobank.common.action.study;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.study.StudyCreatePermission;
import edu.ualberta.med.biobank.common.permission.study.StudyUpdatePermission;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.EventAttrType;
import edu.ualberta.med.biobank.model.GlobalEventAttr;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.User;

public class StudyEventAttrSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private Integer id = null;
    public Integer globalEventAttrId;
    public Boolean required;
    public String permissible;
    public Integer aStatusId;
    public Integer studyId;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setGlobalEventAttrId(Integer globalEventAttrId) {
        this.globalEventAttrId = globalEventAttrId;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public void setPermissible(String permissible) {
        this.permissible = permissible;
    }

    public void setActivityStatusId(Integer aStatusId) {
        this.aStatusId = aStatusId;
    }

    public void setStudyId(Integer studyId) {
        this.studyId = studyId;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        Permission permission;
        if (studyId == null)
            permission = new StudyCreatePermission();
        else
            permission = new StudyUpdatePermission(studyId);
        return permission.isAllowed(user, session);
    }

    @Override
    public IdResult run(User user, Session session) throws ActionException {
        if (globalEventAttrId == null) {
            throw new NullPointerException("globalEventAttrId cannot be null");
        }
        if (required == null) {
            throw new NullPointerException("required field cannot be null");
        }
        if (aStatusId == null) {
            throw new NullPointerException("activity status cannot be null");
        }

        SessionUtil sessionUtil = new SessionUtil(session);
        StudyEventAttr attr = sessionUtil.get(StudyEventAttr.class, id,
            new StudyEventAttr());

        GlobalEventAttr globalAttr =
            sessionUtil.load(GlobalEventAttr.class, globalEventAttrId);

        attr.setLabel(globalAttr.getLabel());
        attr.setPermissible(permissible);
        attr.setRequired(required);
        attr.setEventAttrType(sessionUtil.load(EventAttrType.class, globalAttr
            .getEventAttrType().getId()));
        attr.setActivityStatus(sessionUtil
            .load(ActivityStatus.class, aStatusId));
        attr.setStudy(sessionUtil.load(Study.class, studyId));

        session.saveOrUpdate(attr);
        session.flush();

        return new IdResult(attr.getId());
    }

}
