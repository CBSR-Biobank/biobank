package edu.ualberta.med.biobank.common.action.specimen;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.check.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    private Integer specimenId = null;

    public SpecimenDeleteAction(Integer id) {
        this.specimenId = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new SpecimenDeletePermission(specimenId)
            .isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Specimen specimen = context.load(Specimen.class, specimenId);

        new CollectionIsEmptyCheck<Specimen>(
            Specimen.class, specimen, SpecimenPeer.CHILD_SPECIMEN_COLLECTION,
            specimen.getInventoryId(), null).run(context);

        context.getSession().delete(specimen);
        return new EmptyResult();
    }
}
