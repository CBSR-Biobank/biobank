package edu.ualberta.med.biobank.common.action.eventattr;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.EventAttrTypePeer;
import edu.ualberta.med.biobank.common.peer.GlobalEventAttrPeer;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.model.GlobalEventAttr;
import edu.ualberta.med.biobank.model.User;

public class GlobalEventAttrInfoGetAction implements
    Action<HashMap<Integer, GlobalEventAttrInfo>> {

    private static final long serialVersionUID = 1L;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String GLOBAL_EVENT_ATTR_QRY =
        "select attr,"
            + " type." + EventAttrTypePeer.NAME.getName()
            + " from " + GlobalEventAttr.class.getName() + " as attr"
            + " left join fetch attr."
            + GlobalEventAttrPeer.EVENT_ATTR_TYPE.getName() + " as type";

    // @formatter:on

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return true;
    }

    @Override
    public HashMap<Integer, GlobalEventAttrInfo> run(User user, Session session)
        throws ActionException {
        HashMap<Integer, GlobalEventAttrInfo> attrInfos = new HashMap<Integer, GlobalEventAttrInfo>();

        Query query = session.createQuery(GLOBAL_EVENT_ATTR_QRY);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        for (Object[] row : rows) {
            GlobalEventAttrInfo attrInfo = new GlobalEventAttrInfo();
            attrInfo.attr = (GlobalEventAttr) row[0];
            attrInfo.type = EventAttrTypeEnum.getEventAttrType((String) row[1]);
            attrInfos.put(attrInfo.attr.getId(), attrInfo);
        }
        return attrInfos;
    }

}
