package edu.ualberta.med.biobank.common.action.activityStatus;

import java.util.ArrayList;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.ActivityStatusPeer;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.User;

public class GetAllActivityStatusesAction implements
    Action<ArrayList<ActivityStatus>> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ArrayList<ActivityStatus> run(User user, Session session)
        throws ActionException {
        Criteria criteria = session.createCriteria(ActivityStatus.class);
        criteria.addOrder(Order.asc(ActivityStatusPeer.NAME.getName()));

        ArrayList<ActivityStatus> allActivityStatuses = new ArrayList<ActivityStatus>();

        for (Object o : criteria.list()) {
            if (o instanceof ActivityStatus) {
                allActivityStatuses.add((ActivityStatus) o);
            }
        }

        return allActivityStatuses;
    }
}
