package edu.ualberta.med.biobank.common.action.study;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.MapResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.User;

public class StudyGetEventAttrInfoAction implements
    Action<MapResult<Integer, StudyEventAttrInfo>> {

    private static final long serialVersionUID = 1L;
    private Integer studyId;

    @SuppressWarnings("nls")
    private static final String STUDY_EVENT_ATTR_QRY =
        "SELECT seAttr,attrType.name"
            + " FROM " + StudyEventAttr.class.getName() + " as seAttr"
            + " LEFT JOIN FETCH seAttr.globalEventAttr as geAttr"
            + " LEFT JOIN FETCH geAttr.eventAttrType as attrType"
            + " WHERE seAttr.study.id=?"
            + " GROUP BY seAttr";

    public StudyGetEventAttrInfoAction(Integer studyId) {
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
    public MapResult<Integer, StudyEventAttrInfo> run(User user, Session session)
        throws ActionException {
        HashMap<Integer, StudyEventAttrInfo> attrInfos =
            new HashMap<Integer, StudyEventAttrInfo>();

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
        return new MapResult<Integer, StudyEventAttrInfo>(attrInfos);
    }
}
