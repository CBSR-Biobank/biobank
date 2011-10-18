package edu.ualberta.med.biobank.common.action.activity;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.User;

public class GetActivityStatusesAction implements
    Action<HashMap<Integer, ActivityStatus>> {

    private static final long serialVersionUID = 1L;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String STATUS_QRY = "from " + ActivityStatus.class.getName();
    // @formatter:on

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public HashMap<Integer, ActivityStatus> run(User user, Session session)
        throws ActionException {
        HashMap<Integer, ActivityStatus> map = new HashMap<Integer, ActivityStatus>();

        Query query = session.createQuery(STATUS_QRY);

        @SuppressWarnings("unchecked")
        List<ActivityStatus> rows = query.list();
        for (ActivityStatus st : rows) {
            map.put(st.getId(), st);
        }
        return map;
    }

}
