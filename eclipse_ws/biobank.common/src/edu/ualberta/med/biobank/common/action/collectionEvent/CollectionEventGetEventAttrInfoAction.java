package edu.ualberta.med.biobank.common.action.collectionEvent;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.MapResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.User;

public class CollectionEventGetEventAttrInfoAction implements
    Action<MapResult<Integer, EventAttrInfo>> {

    private static final long serialVersionUID = 1L;
    private Integer ceventId;

    @SuppressWarnings("nls")
    private static final String EVENT_ATTR_QRY =
        "SELECT eAttr,seAttr.id,attrType.name"
            + " FROM " + EventAttr.class.getName() + " as eAttr"
            + " LEFT JOIN FETCH eAttr.studyEventAttr as seAttr"
            + " LEFT JOIN seAttr.globalEventAttr as geAttr"
            + " LEFT JOIN geAttr.eventAttrType as attrType"
            + " WHERE eAttr.collectionEvent.id =?"
            + " GROUP BY eAttr";

    public CollectionEventGetEventAttrInfoAction(Integer ceventId) {
        this.ceventId = ceventId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true;
    }

    @Override
    public MapResult<Integer, EventAttrInfo> run(User user, Session session)
        throws ActionException {
        HashMap<Integer, EventAttrInfo> attrInfos =
            new HashMap<Integer, EventAttrInfo>();

        Query query = session.createQuery(EVENT_ATTR_QRY);
        query.setParameter(0, ceventId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        for (Object[] row : rows) {
            EventAttrInfo attrInfo = new EventAttrInfo();
            attrInfo.attr = (EventAttr) row[0];
            attrInfo.type = EventAttrTypeEnum.getEventAttrType((String) row[2]);
            attrInfos.put((Integer) row[1], attrInfo);
        }

        return new MapResult<Integer, EventAttrInfo>(attrInfos);
    }
}
