package edu.ualberta.med.biobank.common.action.study;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.study.StudyDeletePermission;
import edu.ualberta.med.biobank.model.Study;

public class StudyDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected final Integer studyId;

    public StudyDeleteAction(Study study) {
        if (study == null) {
            throw new IllegalArgumentException();
        }
        this.studyId = study.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new StudyDeletePermission(studyId).isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Study study = context.load(Study.class, studyId);

        // cascades delete all source specimens, aliquoted specimens and
        // study event attributes

        context.getSession().delete(study);
        return new EmptyResult();
    }
}
