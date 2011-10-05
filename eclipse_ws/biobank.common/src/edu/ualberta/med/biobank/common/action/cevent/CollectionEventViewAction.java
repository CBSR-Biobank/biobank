package edu.ualberta.med.biobank.common.action.cevent;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPositionPeer;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

public class CollectionEventViewAction implements
    Action<CollectionEventWithSpecimensInfo> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String CEVENT_INFO_QRY = 
        "select cevent"
        + " from " + CollectionEvent.class.getName() + " as cevent"
        + " inner join fetch cevent." + CollectionEventPeer.PATIENT.getName() + " patient"
        + " inner join fetch cevent." + CollectionEventPeer.ACTIVITY_STATUS.getName() + " status"
        + " inner join fetch patient." + PatientPeer.STUDY.getName() + " study"
        + " where cevent." + CollectionEventPeer.ID.getName() + "=?"
        + " GROUP BY cevent";
    @SuppressWarnings("nls")
    private static final String COMMON_SPEC_QRY = 
        "select spec, parent." + ContainerPeer.LABEL.getName() 
        + ", pos." + SpecimenPositionPeer.POSITION_STRING.getName() 
        + ", toptype." + ContainerTypePeer.NAME_SHORT.getName()
        + " from " + Specimen.class.getName() + " as spec"
        + " inner join fetch spec." + SpecimenPeer.SPECIMEN_TYPE.getName() 
        + " left join spec." + SpecimenPeer.SPECIMEN_POSITION.getName() + " pos"
        + " left join pos." + SpecimenPositionPeer.CONTAINER.getName() + " parent"
        + " left join parent." + ContainerPeer.TOP_CONTAINER.getName() + " topparent"
        + " left join topparent." + ContainerPeer.CONTAINER_TYPE.getName() + " toptype"
        + " inner join fetch spec." + SpecimenPeer.ACTIVITY_STATUS.getName() 
        + " inner join fetch spec." + SpecimenPeer.COLLECTION_EVENT.getName() + " cevent"
        + " inner join fetch spec." + SpecimenPeer.ORIGIN_INFO.getName() + " originInfo"
        + " inner join fetch originInfo." + OriginInfoPeer.CENTER.getName() 
        + " inner join fetch spec." + SpecimenPeer.CURRENT_CENTER.getName() 
        + " inner join fetch cevent." + CollectionEventPeer.PATIENT.getName() + " as patient"
        + " inner join fetch patient." + PatientPeer.STUDY.getName() + " study";
    @SuppressWarnings("nls")
    private static final String SOURCE_SPEC_QRY = 
        COMMON_SPEC_QRY
        + " left join fetch spec." + SpecimenPeer.PROCESSING_EVENT.getName() 
        + " where spec." + Property.concatNames(SpecimenPeer.ORIGINAL_COLLECTION_EVENT, CollectionEventPeer.ID) + " =?";
    @SuppressWarnings("nls")
    private static final String ALIQUOTED_SPEC_QRY = 
        COMMON_SPEC_QRY
        + " left join fetch spec." + SpecimenPeer.PARENT_SPECIMEN.getName() + " parentSpec"
        + " left join fetch parentSpec." + SpecimenPeer.PROCESSING_EVENT.getName()
        + " where spec." + Property.concatNames(SpecimenPeer.COLLECTION_EVENT, CollectionEventPeer.ID) + " =?"
        + " and spec." + SpecimenPeer.PARENT_SPECIMEN.getName() + " is not null";
    // @formatter:on

    private final Integer ceventId;

    public CollectionEventViewAction(Integer ceventId) {
        this.ceventId = ceventId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true; // TODO: restrict access
    }

    @Override
    public CollectionEventWithSpecimensInfo doAction(Session session)
        throws ActionException {
        CollectionEventWithSpecimensInfo ceventInfo = new CollectionEventWithSpecimensInfo();

        Query query = session.createQuery(CEVENT_INFO_QRY);
        query.setParameter(0, ceventId);

        @SuppressWarnings("unchecked")
        List<CollectionEvent> rows = query.list();
        if (rows.size() == 1) {
            ceventInfo.cevent = rows.get(0);
            ceventInfo.sourceSpecimenInfos = getSourceSpecimens(session);
            ceventInfo.sourceSpecimenCount = (long) ceventInfo.sourceSpecimenInfos
                .size();
            ceventInfo.aliquotedSpecimenInfos = getAliquotedSpecimens(session);
            ceventInfo.aliquotedSpecimenCount = (long) ceventInfo.aliquotedSpecimenInfos
                .size();
        } else {
            // TODO: throw exception?
        }

        return ceventInfo;
    }

    private List<SpecimenInfo> getSourceSpecimens(Session session) {
        List<SpecimenInfo> sourceSpecs = new ArrayList<SpecimenInfo>();

        Query query = session.createQuery(SOURCE_SPEC_QRY);
        query.setParameter(0, ceventId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        for (Object[] row : rows) {
            SpecimenInfo specInfo = new SpecimenInfo();
            specInfo.specimen = (Specimen) row[0];
            specInfo.parentLabel = (String) row[1];
            specInfo.positionString = (String) row[2];
            specInfo.topContainerTypeNameShort = (String) row[3];
            sourceSpecs.add(specInfo);
        }

        return sourceSpecs;
    }

    private List<SpecimenInfo> getAliquotedSpecimens(Session session) {
        List<SpecimenInfo> aliquotedSpecs = new ArrayList<SpecimenInfo>();

        Query query = session.createQuery(ALIQUOTED_SPEC_QRY);
        query.setParameter(0, ceventId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        for (Object[] row : rows) {
            SpecimenInfo specInfo = new SpecimenInfo();
            specInfo.specimen = (Specimen) row[0];
            specInfo.parentLabel = (String) row[1];
            specInfo.positionString = (String) row[2];
            specInfo.topContainerTypeNameShort = (String) row[3];
            aliquotedSpecs.add(specInfo);
        }
        return aliquotedSpecs;
    }
}
