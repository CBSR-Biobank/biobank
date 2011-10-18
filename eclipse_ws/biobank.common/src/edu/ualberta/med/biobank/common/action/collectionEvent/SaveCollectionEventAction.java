package edu.ualberta.med.biobank.common.action.collectionEvent;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.User;

public class SaveCollectionEventAction implements Action<Integer> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {
        // TODO Auto-generated method stub
        return null;
    }
}
