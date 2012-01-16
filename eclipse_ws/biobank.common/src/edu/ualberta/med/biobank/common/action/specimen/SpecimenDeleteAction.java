package edu.ualberta.med.biobank.common.action.specimen;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.check.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

public class SpecimenDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    private Integer specimenId = null;

    public SpecimenDeleteAction(Integer id) {
        this.specimenId = id;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new SpecimenDeletePermission(specimenId)
            .isAllowed(user, session);
    }

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        Specimen specimen =
            new ActionContext(user, session).load(Specimen.class, specimenId);

        new CollectionIsEmptyCheck<Specimen>(
            Specimen.class, specimen, SpecimenPeer.CHILD_SPECIMEN_COLLECTION,
            specimen.getInventoryId(), null).run(user, session);

        session.delete(specimen);
        return new EmptyResult();
    }
}
