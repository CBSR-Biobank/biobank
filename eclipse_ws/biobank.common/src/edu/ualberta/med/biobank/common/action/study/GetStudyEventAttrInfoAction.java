package edu.ualberta.med.biobank.common.action.study;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.EventAttrTypePeer;
import edu.ualberta.med.biobank.common.peer.StudyEventAttrPeer;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.User;

public class GetStudyEventAttrInfoAction implements
    Action<HashMap<Integer, StudyEventAttrInfo>> {

    private static final long serialVersionUID = 1L;
    private Integer studyId;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String STUDY_EVENT_ATTR_QRY =
        "select attr,"
            + " type." + EventAttrTypePeer.NAME.getName()
            + " from " + StudyEventAttr.class.getName() + " as attr"
            + " left join fetch attr."
            + StudyEventAttrPeer.EVENT_ATTR_TYPE.getName() + " as type"
            + " where attr."
            + Property.concatNames(StudyEventAttrPeer.STUDY, StudyPeer.ID)
            + " =?"
            + " group by attr";

    // @formatter:on

    public GetStudyEventAttrInfoAction(Integer studyId) {
        this.studyId = studyId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true;
    }

    /**
     * Action that return a map of [label=StudyEventAttrInfo]
     */
    @Override
    public HashMap<Integer, StudyEventAttrInfo> run(User user, Session session)
        throws ActionException {
        HashMap<Integer, StudyEventAttrInfo> attrInfos = new HashMap<Integer, StudyEventAttrInfo>();

        Query query = session.createQuery(STUDY_EVENT_ATTR_QRY);
        query.setParameter(0, studyId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        for (Object[] row : rows) {
            StudyEventAttrInfo attrInfo = new StudyEventAttrInfo();
            attrInfo.attr = (StudyEventAttr) row[0];
            // FIXME need status?
            attrInfo.type = EventAttrTypeEnum.getEventAttrType((String) row[1]);
            attrInfos.put(attrInfo.attr.getId(), attrInfo);
        }
        return attrInfos;
    }
}
