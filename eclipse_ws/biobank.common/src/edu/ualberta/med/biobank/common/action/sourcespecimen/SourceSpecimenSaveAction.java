package edu.ualberta.med.biobank.common.action.sourcespecimen;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;

public class SourceSpecimenSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private Integer id = null;
    private Boolean needOriginalVolume;
    private Integer studyId;
    private Integer specimenTypeId;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNeedOriginalVolume(Boolean needOriginalVolume) {
        this.needOriginalVolume = needOriginalVolume;
    }

    public void setStudyId(Integer id) {
        this.studyId = id;
    }

    public void setSpecimenTypeId(Integer specimenTypeId) {
        this.specimenTypeId = specimenTypeId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        // FIXME: needs implementation
        return true;
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        SourceSpecimen srcSpc =
            context.get(SourceSpecimen.class, id, new SourceSpecimen());
        srcSpc.setNeedOriginalVolume(needOriginalVolume);

        Study study = context.get(Study.class, studyId, new Study());
        srcSpc.setStudy(study);

        SpecimenType specimenType =
            context.get(SpecimenType.class, specimenTypeId,
                new SpecimenType());
        srcSpc.setSpecimenType(specimenType);

        context.getSession().saveOrUpdate(srcSpc);
        context.getSession().flush();

        return new IdResult(srcSpc.getId());
    }
}
