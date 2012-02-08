package edu.ualberta.med.biobank.common.action.specimen;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Specimen;

public abstract class SpecimenGetInfoAction implements
    Action<ListResult<SpecimenInfo>> {
    private static final long serialVersionUID = 1L;

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
            + " LEFT JOIN FETCH spec.commentCollection"
            + " INNER JOIN FETCH cevent.patient patient"
            + " INNER JOIN FETCH patient.study study";

    @SuppressWarnings("nls")
    protected static final String SPEC_END_QRY = " GROUP BY spec";

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    public ListResult<SpecimenInfo> run(ActionContext context, String queryStr,
        Integer idParameter) throws ActionException {
        ArrayList<SpecimenInfo> specs = new ArrayList<SpecimenInfo>();

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
            specs.add(specInfo);
        }

        return new ListResult<SpecimenInfo>(specs);
    }

}
