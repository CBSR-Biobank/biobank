package edu.ualberta.med.biobank.common.action.study;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.study.StudyCreatePermission;
import edu.ualberta.med.biobank.common.permission.study.StudyUpdatePermission;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.GlobalEventAttr;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;

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
    public boolean isAllowed(ActionContext context) throws ActionException {
        Permission permission;
        if (studyId == null)
            permission = new StudyCreatePermission();
        else
            permission = new StudyUpdatePermission(studyId);
        return permission.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        if (globalEventAttrId == null) {
            throw new NullPointerException("globalEventAttrId cannot be null");
        }
        if (required == null) {
            throw new NullPointerException("required field cannot be null");
        }
        if (aStatusId == null) {
            throw new NullPointerException("activity status cannot be null");
        }

        StudyEventAttr attr = context.get(StudyEventAttr.class, id,
            new StudyEventAttr());

        GlobalEventAttr globalAttr =
            context.load(GlobalEventAttr.class, globalEventAttrId);

        attr.setGlobalEventAttr(globalAttr);
        attr.setPermissible(permissible);
        attr.setRequired(required);
        attr.setActivityStatus(context.load(ActivityStatus.class, aStatusId));
        attr.setStudy(context.load(Study.class, studyId));

        context.getSession().saveOrUpdate(attr);
        context.getSession().flush();

        return new IdResult(attr.getId());
    }

}
