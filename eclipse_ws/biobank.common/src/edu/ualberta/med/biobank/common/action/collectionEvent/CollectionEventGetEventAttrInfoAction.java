package edu.ualberta.med.biobank.common.action.collectionEvent;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.MapResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.model.EventAttr;

public class CollectionEventGetEventAttrInfoAction implements
    Action<MapResult<Integer, EventAttrInfo>> {

    private static final long serialVersionUID = 1L;
    private final Integer ceventId;

    @SuppressWarnings("nls")
    private static final String EVENT_ATTR_QRY =
        "SELECT eAttr,seAttr.id,attrType.name"
            + " FROM " + EventAttr.class.getName() + " as eAttr"
            + " LEFT JOIN FETCH eAttr.studyEventAttr as seAttr"
            + " LEFT JOIN seAttr.globalEventAttr as geAttr"
            + " LEFT JOIN geAttr.eventAttrType as attrType"
            + " WHERE eAttr.collectionEvent.id =?";

    public CollectionEventGetEventAttrInfoAction(Integer ceventId) {
        this.ceventId = ceventId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @Override
    public MapResult<Integer, EventAttrInfo> run(ActionContext context)
        throws ActionException {
        HashMap<Integer, EventAttrInfo> attrInfos =
            new HashMap<Integer, EventAttrInfo>();

        Query query = context.getSession().createQuery(EVENT_ATTR_QRY);
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
