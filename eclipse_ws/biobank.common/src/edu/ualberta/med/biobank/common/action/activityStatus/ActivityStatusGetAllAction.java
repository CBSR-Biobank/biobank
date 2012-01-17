package edu.ualberta.med.biobank.common.action.activityStatus;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.MapResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.ActivityStatus;

public class ActivityStatusGetAllAction implements
    Action<MapResult<Integer, ActivityStatus>> {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @Override
    public MapResult<Integer, ActivityStatus> run(ActionContext context)
        throws ActionException {
        HashMap<Integer, ActivityStatus> map =
            new HashMap<Integer, ActivityStatus>();

        Criteria criteria = context.getSession()
            .createCriteria(ActivityStatus.class);

        @SuppressWarnings("unchecked")
        List<ActivityStatus> rows = criteria.list();
        for (ActivityStatus st : rows) {
            map.put(st.getId(), st);
        }

        return new MapResult<Integer, ActivityStatus>(map);
    }

}
