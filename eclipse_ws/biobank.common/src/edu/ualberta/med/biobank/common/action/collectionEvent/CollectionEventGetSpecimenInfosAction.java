package edu.ualberta.med.biobank.common.action.collectionEvent;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPositionPeer;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

public class CollectionEventGetSpecimenInfosAction implements
    Action<ArrayList<SpecimenInfo>> {
    private static final long serialVersionUID = 1L;

    // @formatter:off
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
    public ArrayList<SpecimenInfo> run(User user, Session session)
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

        return specs;
    }
}
