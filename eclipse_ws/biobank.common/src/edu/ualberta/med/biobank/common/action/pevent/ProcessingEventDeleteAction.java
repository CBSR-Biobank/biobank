package edu.ualberta.med.biobank.common.action.pevent;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.User;

public class ProcessingEventDeleteAction implements Action<Integer> {

    private static final long serialVersionUID = 1L;

    private Integer peventId;

    public ProcessingEventDeleteAction(Integer peventId) {
        this.peventId = peventId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Integer doAction(Session session) throws ActionException {
        ProcessingEvent pevent = (ProcessingEvent) session.load(
            ProcessingEvent.class, peventId);

        // FIXME delete checks?

        session.delete(pevent);

        return peventId;
    }

}
