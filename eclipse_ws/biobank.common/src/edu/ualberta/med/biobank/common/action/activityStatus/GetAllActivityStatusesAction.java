package edu.ualberta.med.biobank.common.action.activityStatus;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.User;

public class GetAllActivityStatusesAction implements
    Action<HashMap<Integer, ActivityStatus>> {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(User user, Session session) {
        return true;
    }

    @Override
    public HashMap<Integer, ActivityStatus> run(User user, Session session)
        throws ActionException {
        HashMap<Integer, ActivityStatus> map = new HashMap<Integer, ActivityStatus>();

        Criteria criteria = session.createCriteria(ActivityStatus.class);

        @SuppressWarnings("unchecked")
        List<ActivityStatus> rows = criteria.list();
        for (ActivityStatus st : rows) {
            map.put(st.getId(), st);
        }
        return map;
    }

}
