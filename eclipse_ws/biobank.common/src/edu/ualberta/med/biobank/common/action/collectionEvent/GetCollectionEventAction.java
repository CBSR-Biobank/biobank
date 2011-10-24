package edu.ualberta.med.biobank.common.action.collectionEvent;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.User;

public class GetCollectionEventAction implements Action<CollectionEvent> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public CollectionEvent run(User user, Session session)
        throws ActionException {
        // TODO Auto-generated method stub
        return null;
    }
}
