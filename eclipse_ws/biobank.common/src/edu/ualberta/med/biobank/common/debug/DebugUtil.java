package edu.ualberta.med.biobank.common.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.ActivityStatusPeer;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.peer.SpecimenLinkPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPositionPeer;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class DebugUtil {

    private static final String RANDOM_LINKED_ALIQUOTED_SPECIMENS_QRY = "select aliquots from "
        + Center.class.getName()
        + " as c join c."
        + CenterPeer.PROCESSING_EVENT_COLLECTION.getName()
        + " as pevents join pevents."
        + ProcessingEventPeer.SPECIMEN_LINK_COLLECTION.getName()
        + "as spLink join spLink."
        + SpecimenLinkPeer.CHILD_SPECIMEN_COLLECTION.getName()
        + " as children where c." + CenterPeer.ID.getName() + "=?";

    public static List<SpecimenWrapper> getRandomLinkedAliquotedSpecimens(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            RANDOM_LINKED_ALIQUOTED_SPECIMENS_QRY,
            Arrays.asList(new Object[] { siteId }));
        List<Specimen> aliquots = appService.query(criteria);

        int items = aliquots.size();
        int maxItems = items > 10 ? 10 : items;
        return ModelWrapper.wrapModelCollection(appService,
            aliquots.subList(0, maxItems), SpecimenWrapper.class);
    }

    public static List<SpecimenWrapper> getRandomAssignedSpecimens(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        return getRandomAssignedSpecimens(appService, siteId, null);
    }

    private static final String RANDOM_ASSIGNED_SPECIMENS_BASE_QRY = "select aliquots from "
        + Site.class.getName()
        + " as s join s."
        + SitePeer.CONTAINER_COLLECTION.getName()
        + " as cc join cc."
        + ContainerPeer.SPECIMEN_POSITION_COLLECTION.getName()
        + " as spcpos as spcpos."
        + SpecimenPositionPeer.SPECIMEN.getName()
        + "as spc where s." + SitePeer.ID.getName() + "=?";

    public static List<SpecimenWrapper> getRandomAssignedSpecimens(
        WritableApplicationService appService, Integer siteId, Integer studyId)
        throws ApplicationException {
        List<Object> params = new ArrayList<Object>();
        params.add(siteId);

        StringBuilder qry = new StringBuilder(
            RANDOM_ASSIGNED_SPECIMENS_BASE_QRY);
        if (studyId != null) {
            qry.append(" and a."
                + Property.concatNames(SpecimenPeer.COLLECTION_EVENT,
                    CollectionEventPeer.PATIENT, PatientPeer.STUDY,
                    StudyPeer.ID) + "=?");
            params.add(studyId);
        }

        HQLCriteria criteria = new HQLCriteria(qry.toString(), params);
        List<Specimen> aliquots = appService.query(criteria);

        int items = aliquots.size();
        int maxItems = items > 10 ? 10 : items;
        return ModelWrapper.wrapModelCollection(appService,
            aliquots.subList(0, maxItems), SpecimenWrapper.class);
    }

    private static final String RANDOM_NON_ASSIGNED_NON_DISPATCHED_SPECIMENS_QRY = "select a from "
        + Site.class.getName()
        + " as s left join s."
        + SitePeer.PROCESSING_EVENT_COLLECTION.getName()
        + " as pe left join pe."
        + ProcessingEventPeer.SPECIMEN_LINK_COLLECTION.getName()
        + " as spLink left join spLink."
        + SpecimenLinkPeer.CHILD_SPECIMEN_COLLECTION.getName()
        + " as spc left join spc."
        + SpecimenPeer.SPECIMEN_POSITION.getName()
        + " as spcpos where spcpos is null"
        + " and s."
        + SitePeer.ID.getName()
        + "=? and spc."
        + Property.concatNames(SpecimenPeer.ACTIVITY_STATUS,
            ActivityStatusPeer.NAME) + "!='Dispatched'";

    public static List<SpecimenWrapper> getRandomNonAssignedNonDispatchedSpecimens(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            RANDOM_NON_ASSIGNED_NON_DISPATCHED_SPECIMENS_QRY,
            Arrays.asList(new Object[] { siteId }));
        List<Specimen> aliquots = appService.query(criteria);
        return ModelWrapper.wrapModelCollection(appService, aliquots,
            SpecimenWrapper.class);
    }

    // FIXME still needed ?
    // private static final String RANDOM_DISPATCHED_SPECIMENS_QRY =
    // "select aliquots from "
    // + Site.class.getName()
    // + " as s join s."
    // + SitePeer.PROCESSING_EVENT_COLLECTION.getName()
    // + " as pe join pe."
    // + ProcessingEventPeer.CHILD_SPECIMEN_COLLECTION.getName()
    // + " as aliquots where s."
    // + SitePeer.ID.getName()
    // + "=? and aliquots."
    // + Property.concatNames(SpecimenPeer.ACTIVITY_STATUS,
    // ActivityStatusPeer.NAME) + "='Dispatched'";
    //
    // public static List<SpecimenWrapper> getRandomDispatchedSpecimens(
    // WritableApplicationService appService, Integer siteId)
    // throws ApplicationException {
    // HQLCriteria criteria = new HQLCriteria(RANDOM_DISPATCHED_SPECIMENS_QRY,
    // Arrays.asList(new Object[] { siteId }));
    // List<Specimen> aliquots = appService.query(criteria);
    //
    // int items = aliquots.size();
    // int maxItems = items > 10 ? 10 : items;
    // return ModelWrapper.wrapModelCollection(appService,
    // aliquots.subList(0, maxItems), SpecimenWrapper.class);
    // }
}
