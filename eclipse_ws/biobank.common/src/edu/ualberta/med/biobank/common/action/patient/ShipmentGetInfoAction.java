package edu.ualberta.med.biobank.common.action.patient;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.patient.ShipmentGetInfoAction.ShipInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.permission.patient.OriginInfoReadPermission;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.User;

/**
 * Retrieve a patient information using a patient id
 * 
 * @author aaron
 * 
 */
public class ShipmentGetInfoAction implements Action<ShipInfo> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String ORIGIN_INFO_HQL = "SELECT patient, COUNT(DISTINCT sourceSpecs), COUNT(DISTINCT allSpecs) - COUNT(DISTINCT sourceSpecs)"
        + " FROM "
        + Patient.class.getName()
        + " patient"
        + " INNER JOIN FETCH patient."
        + PatientPeer.STUDY.getName()
        + " study"
        + " LEFT JOIN patient."
        + PatientPeer.COLLECTION_EVENT_COLLECTION.getName()
        + " AS cevents"
        + " LEFT JOIN cevents."
        + CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION.getName()
        + " AS sourceSpecs"
        + " LEFT JOIN cevents."
        + CollectionEventPeer.ALL_SPECIMEN_COLLECTION.getName()
        + " AS allSpecs"
        + " WHERE patient.id = ?"
        + " GROUP BY patient";
    // @formatter:on

    private final Integer oiId;

    public static class ShipInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;

        public OriginInfo oi;
        public String sourceCenter;
        public String receiverSite;
        public Collection<SpecimenInfo> specimens;

    }

    public ShipmentGetInfoAction(Integer oiId) {
        this.oiId = oiId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new OriginInfoReadPermission(oiId).isAllowed(user, session);
    }

    @Override
    public ShipInfo run(User user, Session session) throws ActionException {
        ShipInfo sInfo = new ShipInfo();

        Query query = session.createQuery(ORIGIN_INFO_HQL);
        query.setParameter(0, oiId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        if (rows.size() == 1) {
            Object[] row = rows.get(0);

            sInfo.oi = (OriginInfo) row[0];
            sInfo.sourceCenter = (String) row[1];
            sInfo.receiverSite = (String) row[2];
            sInfo.specimens = (Collection<SpecimenInfo>) new ShipmentGetSpecimenInfosAction(
                oiId)
                .run(user, session);

        } else {
            throw new ActionException("No patient found with id:" + oiId); //$NON-NLS-1$
        }

        return sInfo;
    }

}
