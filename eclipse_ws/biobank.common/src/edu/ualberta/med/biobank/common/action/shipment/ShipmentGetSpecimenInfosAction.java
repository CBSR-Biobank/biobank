package edu.ualberta.med.biobank.common.action.shipment;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoReadPermission;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

public class ShipmentGetSpecimenInfosAction implements
    Action<ListResult<Specimen>> {

    @SuppressWarnings("nls")
    public static final String SPECIMEN_INFO_HQL = "select spec from "
        + Specimen.class.getName() + " as spec" + " inner join fetch spec."
        + SpecimenPeer.SPECIMEN_TYPE.getName() + " left join fetch spec."
        + SpecimenPeer.COMMENT_COLLECTION.getName() + " inner join fetch spec."
        + SpecimenPeer.ACTIVITY_STATUS.getName() + " inner join fetch spec."
        + SpecimenPeer.COLLECTION_EVENT.getName() + " cevent"
        + " inner join fetch spec." + SpecimenPeer.ORIGIN_INFO.getName()
        + " originInfo inner join fetch originInfo."
        + OriginInfoPeer.CENTER.getName() + " inner join fetch spec."
        + SpecimenPeer.CURRENT_CENTER.getName() + " as center"
        + " inner join fetch cevent." + CollectionEventPeer.PATIENT.getName()
        + " as patient inner join fetch patient." + PatientPeer.STUDY.getName()
        + " study where originInfo.id=?";

    private static final long serialVersionUID = 1L;
    private Integer oiId;

    public ShipmentGetSpecimenInfosAction(Integer oiId) {
        this.oiId = oiId;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new OriginInfoReadPermission(oiId).isAllowed(user, session);
    }

    @Override
    public ListResult<Specimen> run(User user, Session session)
        throws ActionException {
        ArrayList<Specimen> specInfos = new ArrayList<Specimen>();

        Query query = session.createQuery(SPECIMEN_INFO_HQL);
        query.setParameter(0, oiId);

        @SuppressWarnings("unchecked")
        List<Object> rows = query.list();
        for (Object row : rows) {
            specInfos.add((Specimen) row);
        }
        return new ListResult<Specimen>(specInfos);
    }
}
