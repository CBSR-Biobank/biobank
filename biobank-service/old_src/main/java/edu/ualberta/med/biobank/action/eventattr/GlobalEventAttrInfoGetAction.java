package edu.ualberta.med.biobank.action.eventattr;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.MapResult;
import edu.ualberta.med.biobank.action.collectionEvent.EventAttrTypeEnum;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.model.GlobalEventAttr;

public class GlobalEventAttrInfoGetAction implements
    Action<MapResult<Integer, GlobalEventAttrInfo>> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String GLOBAL_EVENT_ATTR_QRY =
        "SELECT attr,type.name"
            + " FROM " + GlobalEventAttr.class.getName() + " attr"
            + " LEFT JOIN FETCH attr.eventAttrType type";

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @Override
    public MapResult<Integer, GlobalEventAttrInfo> run(ActionContext context)
        throws ActionException {
        HashMap<Integer, GlobalEventAttrInfo> attrInfos =
            new HashMap<Integer, GlobalEventAttrInfo>();

        Query query = context.getSession().createQuery(GLOBAL_EVENT_ATTR_QRY);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        for (Object[] row : rows) {
            GlobalEventAttrInfo attrInfo = new GlobalEventAttrInfo();
            attrInfo.attr = (GlobalEventAttr) row[0];
            attrInfo.type = EventAttrTypeEnum.getEventAttrType((String) row[1]);
            attrInfos.put(attrInfo.attr.getId(), attrInfo);
        }
        return new MapResult<Integer, GlobalEventAttrInfo>(attrInfos);
    }

}
