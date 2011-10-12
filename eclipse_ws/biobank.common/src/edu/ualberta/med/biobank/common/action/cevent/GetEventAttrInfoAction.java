package edu.ualberta.med.biobank.common.action.cevent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.EventAttrPeer;
import edu.ualberta.med.biobank.common.peer.EventAttrTypePeer;
import edu.ualberta.med.biobank.common.peer.StudyEventAttrPeer;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.User;

public class GetEventAttrInfoAction implements
    Action<Map<Integer, EventAttrInfo>> {

    private static final long serialVersionUID = 1L;
    private Integer ceventId;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String EVENT_ATTR_QRY = 
        "select attr,"
        +" sAttr." + StudyEventAttrPeer.ID.getName() 
        + ", attrType." + EventAttrTypePeer.NAME.getName()
        + " from " + EventAttr.class.getName() + " as attr"
        + " left join fetch attr." + EventAttrPeer.STUDY_EVENT_ATTR.getName() + " as sAttr"
        + " left join sAttr." + StudyEventAttrPeer.EVENT_ATTR_TYPE.getName() + " as attrType"
        + " where attr." + Property.concatNames(EventAttrPeer.COLLECTION_EVENT, CollectionEventPeer.ID) + " =?"
        + " group by attr";
    // @formatter:on

    public GetEventAttrInfoAction(Integer ceventId) {
        this.ceventId = ceventId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public Map<Integer, EventAttrInfo> doAction(Session session)
        throws ActionException {
        Map<Integer, EventAttrInfo> attrInfos = new HashMap<Integer, EventAttrInfo>();

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
        return attrInfos;
    }
}
