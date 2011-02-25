package edu.ualberta.med.biobank.common.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.ActivityStatusPeer;
import edu.ualberta.med.biobank.common.peer.AliquotPeer;
import edu.ualberta.med.biobank.common.peer.AliquotPositionPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.peer.SourceVesselPeer;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class DebugUtil {

    private static final String RANDOM_LINKED_ALIQUOTS_QRY = "select aliquots from "
        + Site.class.getName()
        + " as s join s."
        + SitePeer.PROCESSING_EVENT_COLLECTION.getName()
        + " as pe join pe."
        + ProcessingEventPeer.ALIQUOT_COLLECTION.getName()
        + " as aliquots where s." + SitePeer.ID.getName() + "=?";

    public static List<SpecimenWrapper> getRandomLinkedAliquots(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(RANDOM_LINKED_ALIQUOTS_QRY,
            Arrays.asList(new Object[] { siteId }));
        List<Aliquot> aliquots = appService.query(criteria);

        int items = aliquots.size();
        int maxItems = items > 10 ? 10 : items;
        return ModelWrapper.wrapModelCollection(appService,
            aliquots.subList(0, maxItems), SpecimenWrapper.class);
    }

    public static List<SpecimenWrapper> getRandomAssignedAliquots(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        return getRandomAssignedAliquots(appService, siteId, null);
    }

    private static final String RANDOM_ASSIGNED_ALIQUOTS_BASE_QRY = "from "
        + Aliquot.class.getName()
        + " as a where a in (select ap."
        + AliquotPositionPeer.ALIQUOT.getName()
        + " from "
        + AliquotPosition.class.getName()
        + " as ap) and a."
        + Property.concatNames(AliquotPeer.ALIQUOT_POSITION,
            AliquotPositionPeer.CONTAINER, ContainerPeer.SITE, SitePeer.ID)
        + "=?";

    public static List<SpecimenWrapper> getRandomAssignedAliquots(
        WritableApplicationService appService, Integer siteId, Integer studyId)
        throws ApplicationException {
        List<Object> params = new ArrayList<Object>();
        params.add(siteId);

        StringBuilder qry = new StringBuilder(RANDOM_ASSIGNED_ALIQUOTS_BASE_QRY);
        if (studyId != null) {
            qry.append(" and a."
                + Property.concatNames(AliquotPeer.PROCESSING_EVENT,
                    ProcessingEventPeer.SOURCE_VESSEL_COLLECTION,
                    SourceVesselPeer.PATIENT, PatientPeer.STUDY, StudyPeer.ID)
                + "=?");
            params.add(studyId);
        }

        HQLCriteria criteria = new HQLCriteria(qry.toString(), params);
        List<Aliquot> aliquots = appService.query(criteria);

        int items = aliquots.size();
        int maxItems = items > 10 ? 10 : items;
        return ModelWrapper.wrapModelCollection(appService,
            aliquots.subList(0, maxItems), SpecimenWrapper.class);
    }

    private static final String RANDOM_NON_ASSIGNED_NON_DISPATCHED_ALIQUOT_QRY = "select a from "
        + Site.class.getName()
        + " as s left join s."
        + SitePeer.PROCESSING_EVENT_COLLECTION.getName()
        + " as pe left join pe."
        + ProcessingEventPeer.ALIQUOT_COLLECTION.getName()
        + " as a left join a."
        + AliquotPeer.ALIQUOT_POSITION.getName()
        + " as ap where ap is null"
        + " and s."
        + SitePeer.ID.getName()
        + "=? and a."
        + Property.concatNames(AliquotPeer.ACTIVITY_STATUS,
            ActivityStatusPeer.NAME) + "!='Dispatched'";

    public static List<SpecimenWrapper> getRandomNonAssignedNonDispatchedAliquots(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            RANDOM_NON_ASSIGNED_NON_DISPATCHED_ALIQUOT_QRY,
            Arrays.asList(new Object[] { siteId }));
        List<Aliquot> aliquots = appService.query(criteria);
        return ModelWrapper.wrapModelCollection(appService, aliquots,
            SpecimenWrapper.class);
    }

    private static final String RANDOM_DISPATCHED_ALIQUOT_QRY = "select aliquots from "
        + Site.class.getName()
        + " as s join s."
        + SitePeer.PROCESSING_EVENT_COLLECTION.getName()
        + " as pe join pe."
        + ProcessingEventPeer.ALIQUOT_COLLECTION.getName()
        + " as aliquots where s."
        + SitePeer.ID.getName()
        + "=? and aliquots."
        + Property.concatNames(AliquotPeer.ACTIVITY_STATUS,
            ActivityStatusPeer.NAME) + "='Dispatched'";

    public static List<SpecimenWrapper> getRandomDispatchedAliquots(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(RANDOM_DISPATCHED_ALIQUOT_QRY,
            Arrays.asList(new Object[] { siteId }));
        List<Aliquot> aliquots = appService.query(criteria);

        int items = aliquots.size();
        int maxItems = items > 10 ? 10 : items;
        return ModelWrapper.wrapModelCollection(appService,
            aliquots.subList(0, maxItems), SpecimenWrapper.class);
    }
}
