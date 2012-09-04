package edu.ualberta.med.biobank.common.action.specimen;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Specimen;

/**
 * Meant to be used as a sub-action.
 * 
 */
public abstract class SpecimenListGetInfoAction implements
    Action<ListResult<SpecimenInfo>> {
    private static final long serialVersionUID = 1L;

    // can't use distinct because we are not selecting an object
    @SuppressWarnings("nls")
    protected static final String SPEC_BASE_QRY =
        "SELECT spec,parent.label,pos.positionString,toptype.nameShort"
            + " FROM " + Specimen.class.getName() + " spec"
            + " INNER JOIN FETCH spec.specimenType"
            + " INNER JOIN FETCH spec.currentCenter"
            + " LEFT JOIN spec.specimenPosition pos"
            + " LEFT JOIN pos.container parent"
            + " LEFT JOIN parent.topContainer topparent"
            + " LEFT JOIN topparent.containerType toptype"
            + " INNER JOIN FETCH spec.collectionEvent cevent"
            + " INNER JOIN FETCH spec.originInfo originInfo"
            + " INNER JOIN FETCH originInfo.center"
            + " INNER JOIN FETCH cevent.patient patient"
            + " INNER JOIN FETCH patient.study study";

    @SuppressWarnings("nls")
    protected static final String SPEC_COMMENT_CNT_QRY =
        "SELECT count(comments) "
            + "FROM " + Specimen.class.getName() + " specimen "
            + "LEFT JOIN specimen.comments comments "
            + "WHERE specimen.id=? "
            + "GROUP BY specimen.id";

    @Override
    public boolean isAllowed(ActionContext context) {
        // this is only called as a sub-action
        return false;
    }

    @SuppressWarnings("nls")
    public ListResult<SpecimenInfo> run(ActionContext context, String queryStr,
        Integer idParameter) throws ActionException {
        ArrayList<SpecimenInfo> infos = new ArrayList<SpecimenInfo>();

        Query query = context.getSession().createQuery(queryStr);
        query.setParameter(0, idParameter);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        for (Object[] row : rows) {
            SpecimenInfo specInfo = new SpecimenInfo();
            specInfo.specimen = (Specimen) row[0];
            specInfo.parentLabel = (String) row[1];
            specInfo.positionString = (String) row[2];
            specInfo.topContainerTypeNameShort = (String) row[3];
            infos.add(specInfo);
        }

        for (SpecimenInfo info : infos) {
            query = context.getSession().createQuery(SPEC_COMMENT_CNT_QRY);
            query.setParameter(0, info.specimen.getId());
            Long numComments = (Long) query.uniqueResult();

            info.comment = (numComments > 0) ? "Y" : "N";
        }

        return new ListResult<SpecimenInfo>(infos);
    }
}
