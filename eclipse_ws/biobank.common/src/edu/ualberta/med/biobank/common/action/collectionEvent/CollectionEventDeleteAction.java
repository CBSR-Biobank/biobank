package edu.ualberta.med.biobank.common.action.collectionEvent;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.check.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.action.check.ValueProperty;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventDeletePermission;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.User;

public class CollectionEventDeleteAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    private static final String HAS_SPECIMENS_MSG = Messages
        .getString("CollectionEventDeleteAction.has_specimen_delete_msg"); //$NON-NLS-1$

    private Integer ceventId;

    public CollectionEventDeleteAction(Integer ceventId) {
        this.ceventId = ceventId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new CollectionEventDeletePermission(ceventId).isAllowed(user,
            session);
    }

    @Override
    public IdResult run(User user, Session session) throws ActionException {
        CollectionEvent cevent = (CollectionEvent) session.load(
            CollectionEvent.class, ceventId);

        new CollectionIsEmptyCheck<CollectionEvent>(
            new ValueProperty<CollectionEvent>(CollectionEventPeer.ID, ceventId),
            CollectionEvent.class, CollectionEventPeer.ALL_SPECIMEN_COLLECTION,
            cevent.getVisitNumber().toString(), HAS_SPECIMENS_MSG).run(user,
            session);

        session.delete(cevent);

        return new IdResult(ceventId);
    }

}
