package edu.ualberta.med.biobank.common.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.peer.SitePeer;
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

    private static final String RANDOM_LINKED_ALIQUOTED_SPECIMENS_QRY =
        "select children from " //$NON-NLS-1$
            + Center.class.getName()
            + " as center join center." //$NON-NLS-1$
            + CenterPeer.PROCESSING_EVENTS.getName()
            + " as pevents join pevents." //$NON-NLS-1$
            + ProcessingEventPeer.SPECIMENS.getName()
            + " as srcSpcs join srcSpcs." //$NON-NLS-1$
            + SpecimenPeer.CHILD_SPECIMENS.getName()
            + " as children where center." + CenterPeer.ID.getName() + "=?"; //$NON-NLS-1$ //$NON-NLS-2$

    public static List<SpecimenWrapper> getRandomLinkedAliquotedSpecimens(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            RANDOM_LINKED_ALIQUOTED_SPECIMENS_QRY,
            Arrays.asList(new Object[] { siteId }));
        List<Specimen> res = appService.query(criteria);
        List<Specimen> specimens = res.size() > 10 ? null : res;
        if (specimens == null) {
            specimens = new ArrayList<Specimen>();
            int i = 0;
            while (i < 10) {
                specimens.add(res.get(i));
                i++;
            }
        }
        return ModelWrapper.wrapModelCollection(appService, specimens,
            SpecimenWrapper.class);
    }

    public static List<SpecimenWrapper> getRandomAssignedSpecimens(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        return getRandomAssignedSpecimens(appService, siteId, null);
    }

    private static final String RANDOM_ASSIGNED_SPECIMENS_BASE_QRY =
        "select specimen from " //$NON-NLS-1$
            + Site.class.getName()
            + " as site join site." //$NON-NLS-1$
            + SitePeer.CONTAINERS.getName()
            + " as cont join cont." //$NON-NLS-1$
            + ContainerPeer.SPECIMEN_POSITIONS.getName()
            + " as spcpos join spcpos." //$NON-NLS-1$
            + SpecimenPositionPeer.SPECIMEN.getName()
            + " as specimen where site." + SitePeer.ID.getName() + "=?"; //$NON-NLS-1$ //$NON-NLS-2$

    public static List<SpecimenWrapper> getRandomAssignedSpecimens(
        WritableApplicationService appService, Integer siteId, Integer studyId)
        throws ApplicationException {
        List<Object> params = new ArrayList<Object>();
        params.add(siteId);

        StringBuilder qry = new StringBuilder(
            RANDOM_ASSIGNED_SPECIMENS_BASE_QRY);
        if (studyId != null) {
            qry.append(" and a." //$NON-NLS-1$
                + Property.concatNames(SpecimenPeer.COLLECTION_EVENT,
                    CollectionEventPeer.PATIENT, PatientPeer.STUDY,
                    StudyPeer.ID) + "=?"); //$NON-NLS-1$
            params.add(studyId);
        }

        HQLCriteria criteria = new HQLCriteria(qry.toString(), params);
        List<Specimen> res = appService.query(criteria);
        List<Specimen> specimens = res.size() > 10 ? null : res;
        if (specimens == null) {
            specimens = new ArrayList<Specimen>();
            int i = 0;
            while (i < 10) {
                specimens.add(res.get(i));
                i++;
            }
        }
        return ModelWrapper.wrapModelCollection(appService, specimens,
            SpecimenWrapper.class);
    }

    private static final String RANDOM_NON_ASSIGNED_NON_DISPATCHED_SPECIMENS_QRY =
        "select spec from " //$NON-NLS-1$
            + Site.class.getName()
            + " as site left join site." //$NON-NLS-1$
            + SitePeer.PROCESSING_EVENTS.getName()
            + " as pe left join pe." //$NON-NLS-1$
            + ProcessingEventPeer.SPECIMENS.getName()
            + " as srcSpcs left join srcSpcs." //$NON-NLS-1$
            + SpecimenPeer.CHILD_SPECIMENS.getName()
            + " as spec left join spec." //$NON-NLS-1$
            + SpecimenPeer.SPECIMEN_POSITION.getName()
            + " as spcpos where spcpos is null" //$NON-NLS-1$
            + " and site." //$NON-NLS-1$
            + SitePeer.ID.getName()
            + "=?"; //$NON-NLS-1$

    // TODO: the check on activityStatus.name makes no sense;

    public static List<SpecimenWrapper> getRandomNonAssignedNonDispatchedSpecimens(
        WritableApplicationService appService, Integer siteId, Integer maxSize)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            RANDOM_NON_ASSIGNED_NON_DISPATCHED_SPECIMENS_QRY,
            Arrays.asList(new Object[] { siteId }));
        List<Specimen> res = appService.query(criteria);
        List<Specimen> specimens;
        if (maxSize == null)
            specimens = res;
        else {
            maxSize = Math.min(maxSize, res.size());
            specimens = new ArrayList<Specimen>();
            int i = 0;
            if (res.size() > 0)
                while (i < maxSize) {
                    specimens.add(res.get(i));
                    i++;
                }
        }
        return ModelWrapper.wrapModelCollection(appService, specimens,
            SpecimenWrapper.class);
    }

    private static final String RANDOM_NON_DISPATCHED_SPECIMENS_QRY = "from " //$NON-NLS-1$
        + Specimen.class.getName()
        + " where " //$NON-NLS-1$
        + Property.concatNames(SpecimenPeer.CURRENT_CENTER, CenterPeer.ID)
        + "=?"; //$NON-NLS-1$

    // TODO: the check on activityStatus.name makes no sense;

    public static List<SpecimenWrapper> getRandomNonDispatchedSpecimens(
        WritableApplicationService appService, Integer centerId, Integer maxSize)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            RANDOM_NON_DISPATCHED_SPECIMENS_QRY,
            Arrays.asList(new Object[] { centerId }));
        List<Specimen> res = appService.query(criteria);
        List<Specimen> specimens;
        if (maxSize == null)
            specimens = res;
        else {
            specimens = new ArrayList<Specimen>();
            int i = 0;
            while (i < maxSize) {
                specimens.add(res.get(i));
                i++;
            }
        }
        return ModelWrapper.wrapModelCollection(appService, specimens,
            SpecimenWrapper.class);
    }
}
