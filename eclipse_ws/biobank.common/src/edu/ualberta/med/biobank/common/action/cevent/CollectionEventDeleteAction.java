package edu.ualberta.med.biobank.common.action.cevent;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.User;

public class CollectionEventDeleteAction implements Action<Integer> {

    private static final long serialVersionUID = 1L;

    private Integer ceventId;

    public CollectionEventDeleteAction(Integer ceventId) {
        this.ceventId = ceventId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {
        CollectionEvent cevent = (CollectionEvent) session.load(
            CollectionEvent.class, ceventId);

        // FIXME delete checks?

        session.delete(cevent);

        return ceventId;
    }

}
