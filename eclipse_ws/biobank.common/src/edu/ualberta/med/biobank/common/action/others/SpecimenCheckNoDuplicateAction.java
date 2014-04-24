package edu.ualberta.med.biobank.common.action.others;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;

public class SpecimenCheckNoDuplicateAction implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;

    private final String inventoryId;
    private final Integer studyId;

    @SuppressWarnings("nls")
    public SpecimenCheckNoDuplicateAction(String inventoryId, Study study) {
        if (inventoryId == null) {
            throw new NullPointerException("inventory id is null");
        }

        if (inventoryId.isEmpty()) {
            throw new IllegalArgumentException("inventory id is empty");
        }

        if (study == null) {
            throw new NullPointerException("study is null");
        }

        this.inventoryId = inventoryId;
        this.studyId = study.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @SuppressWarnings("nls")
    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        @SuppressWarnings("unchecked")
        List<Specimen> specimens = context.getSession().createCriteria(Specimen.class, "specimen")
            .createAlias("specimen.study", "study")
            .add(Restrictions.eq("specimen.inventoryId", inventoryId))
            .add(Restrictions.eq("study.id", studyId)).list();
        return new BooleanResult(specimens.size() == 0);
    }
}
