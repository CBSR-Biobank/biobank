package edu.ualberta.med.biobank.common.action.study;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.check.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.permission.study.StudyDeletePermission;
import edu.ualberta.med.biobank.model.Study;

public class StudyDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected Integer studyId = null;

    public StudyDeleteAction(Integer id) {
        this.studyId = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new StudyDeletePermission(studyId).isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Study study = context.load(Study.class, studyId);

        new CollectionIsEmptyCheck<Study>(
            Study.class, study, StudyPeer.PATIENT_COLLECTION,
            study.getNameShort(), null).run(context);

        // cascades delete all source specimens, aliquoted specimens and
        // study event attributes

        context.getSession().delete(study);
        return new EmptyResult();
    }
}
