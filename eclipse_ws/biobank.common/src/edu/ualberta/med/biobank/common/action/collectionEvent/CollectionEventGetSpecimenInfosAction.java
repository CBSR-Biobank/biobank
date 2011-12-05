package edu.ualberta.med.biobank.common.action.collectionEvent;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

public class CollectionEventGetSpecimenInfosAction implements
    Action<ListResult<SpecimenInfo>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SPEC_BASE_QRY =
        "SELECT spec,parent.label,pos.positionString,toptype.nameShort"
            + " FROM " + Specimen.class.getName() + " spec"
            + " INNER JOIN FETCH spec.specimenType"
            + " LEFT JOIN spec.specimenPosition pos"
            + " LEFT JOIN pos.container parent"
            + " LEFT JOIN parent.topContainer topparent"
            + " LEFT JOIN topparent.containerType toptype"
            + " INNER JOIN FETCH spec.activityStatus"
            + " INNER JOIN FETCH spec.collectionEvent cevent"
            + " INNER JOIN FETCH spec.originInfo originInfo"
            + " INNER JOIN FETCH originInfo.center"
            + " INNER JOIN FETCH spec.currentCenter"
            + " LEFT JOIN FETCH spec.commentCollection"
            + " INNER JOIN FETCH cevent.patient patient"
            + " INNER JOIN FETCH patient.study study";

    @SuppressWarnings("nls")
    private static final String SPEC_END_QRY = " GROUP BY spec";

    @SuppressWarnings("nls")
    private static final String SOURCE_SPEC_QRY =
        SPEC_BASE_QRY
            + " LEFT JOIN FETCH spec.processingEvent"
            + " WHERE spec.originalCollectionEvent.id=?"
            + SPEC_END_QRY;

    @SuppressWarnings("nls")
    private static final String ALIQUOTED_SPEC_QRY =
        SPEC_BASE_QRY
            + " LEFT JOIN FETCH spec.parentSpecimen parentSpec"
            + " LEFT JOIN FETCH parentSpec.processingEvent"
            + " WHERE spec.collectionEvent.id=?"
            + " AND spec.parentSpecimen IS NOT null"
            + SPEC_END_QRY;

    private Integer ceventId;
    private boolean aliquotedSpecimens = false;

    public CollectionEventGetSpecimenInfosAction(Integer cevenId,
        boolean aliquotedSpecimens) {
        this.ceventId = cevenId;
        this.aliquotedSpecimens = aliquotedSpecimens;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true;
    }

    @Override
    public ListResult<SpecimenInfo> run(User user, Session session)
        throws ActionException {
        ArrayList<SpecimenInfo> specs = new ArrayList<SpecimenInfo>();

        Query query = session
            .createQuery(aliquotedSpecimens ? ALIQUOTED_SPEC_QRY
                : SOURCE_SPEC_QRY);
        query.setParameter(0, ceventId);

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
