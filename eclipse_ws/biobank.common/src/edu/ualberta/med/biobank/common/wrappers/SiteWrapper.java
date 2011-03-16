package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.AddressPeer;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.ContactPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.util.RequestState;
import edu.ualberta.med.biobank.common.wrappers.base.SiteBaseWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SiteWrapper extends SiteBaseWrapper {
    private Map<RequestState, List<RequestWrapper>> requestCollectionMap = new HashMap<RequestState, List<RequestWrapper>>();

    public static final List<String> PROP_NAMES;
    static {
        List<String> aList = new ArrayList<String>();
        aList.addAll(SitePeer.PROP_NAMES);
        aList.addAll(AddressPeer.PROP_NAMES);
        PROP_NAMES = Collections.unmodifiableList(aList);
    };

    public SiteWrapper(WritableApplicationService appService, Site wrappedObject) {
        super(appService, wrappedObject);
    }

    public SiteWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return PROP_NAMES;
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        checkNoDuplicates(Site.class, SitePeer.NAME.getName(), getName(),
            "A site with name");
        checkNoDuplicates(Site.class, SitePeer.NAME_SHORT.getName(),
            getNameShort(), "A site with name short");
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        if (!getContainerCollection(false).isEmpty()
            || !getContainerTypeCollection(false).isEmpty()
            || !getProcessingEventCollection(false).isEmpty()) {
            throw new BiobankCheckException(
                "Unable to delete site "
                    + getName()
                    + ". All defined children (processing events, container types, and containers) must be removed first.");
        }
    }

    private List<RequestWrapper> getRequestCollection(final RequestState state) {
        List<RequestWrapper> requestCollection = requestCollectionMap
            .get(state);

        if (requestCollection == null) {
            requestCollection = new ArrayList<RequestWrapper>();

            PredicateUtil.filterInto(requestCollection,
                new Predicate<RequestWrapper>() {
                    @Override
                    public boolean evaluate(RequestWrapper request) {
                        return state.getId().equals(request.getState());
                    }

                }, requestCollection);

            requestCollectionMap.put(state, requestCollection);
        }

        return requestCollection;
    }

    public List<RequestWrapper> getApprovedRequestCollection() {
        return getRequestCollection(RequestState.APPROVED);
    }

    // due to bug in Hibernate when using elements in query must also use a left
    // join
    private static final String STUDIES_NON_ASSOC_BASE_QRY = "select s from "
        + Study.class.getName() + " s left join s."
        + StudyPeer.SITE_COLLECTION.getName() + " where ";

    public List<StudyWrapper> getStudiesNotAssoc() throws ApplicationException {
        List<StudyWrapper> studyWrappers = new ArrayList<StudyWrapper>();
        StringBuilder qry = new StringBuilder(STUDIES_NON_ASSOC_BASE_QRY)
            .append(getId()).append(" not in elements(s.")
            .append(StudyPeer.SITE_COLLECTION.getName()).append(")");
        HQLCriteria c = new HQLCriteria(qry.toString());
        List<Study> results = appService.query(c);
        for (Study res : results) {
            studyWrappers.add(new StudyWrapper(appService, res));
        }
        return studyWrappers;
    }

    public List<ContainerTypeWrapper> getContainerTypeCollection() {
        return getContainerTypeCollection(false);
    }

    public List<ContainerWrapper> getContainerCollection() {
        return getContainerCollection(false);
    }

    private static final String TOP_CONTAINERS_QRY = "from "
        + Container.class.getName()
        + " where "
        + Property.concatNames(ContainerPeer.SITE, SitePeer.ID)
        + "=? and "
        + Property.concatNames(ContainerPeer.CONTAINER_TYPE,
            ContainerTypePeer.TOP_LEVEL) + "=true";

    @SuppressWarnings("unchecked")
    public List<ContainerWrapper> getTopContainerCollection(boolean sort)
        throws Exception {
        List<ContainerWrapper> topContainerCollection = (List<ContainerWrapper>) propertiesMap
            .get("topContainerCollection");

        if (topContainerCollection == null) {
            topContainerCollection = new ArrayList<ContainerWrapper>();
            HQLCriteria criteria = new HQLCriteria(TOP_CONTAINERS_QRY,
                Arrays.asList(new Object[] { wrappedObject.getId() }));
            List<Container> containers = appService.query(criteria);
            for (Container c : containers) {
                topContainerCollection.add(new ContainerWrapper(appService, c));
            }
            if (sort)
                Collections.sort(topContainerCollection);
            propertiesMap.put("topContainerCollection", topContainerCollection);
        }
        return topContainerCollection;
    }

    public List<ContainerWrapper> getTopContainerCollection() throws Exception {
        return getTopContainerCollection(false);
    }

    public void clearTopContainerCollection() {
        propertiesMap.put("topContainerCollection", null);
    }

    @Override
    public int compareTo(ModelWrapper<Site> wrapper) {
        if (wrapper instanceof SiteWrapper) {
            String name1 = wrappedObject.getName();
            String name2 = wrapper.wrappedObject.getName();
            return ((name1.compareTo(name2) > 0) ? 1 : (name1.equals(name2) ? 0
                : -1));
        }
        return 0;
    }

    private static final String PATIENT_COUNT_QRY = "select count(distinct patients) from "
        + Site.class.getName()
        + " as site join site."
        + SitePeer.PROCESSING_EVENT_COLLECTION.getName()
        + " as pevent join pevent."
        + ProcessingEventPeer.SPECIMEN_COLLECTION.getName()
        + " as spcs join spcs."
        + Property.concatNames(SpecimenPeer.COLLECTION_EVENT,
            CollectionEventPeer.PATIENT)
        + " as patients where site."
        + SitePeer.ID.getName() + "=?";

    public Long getPatientCount() throws Exception {
        HQLCriteria criteria = new HQLCriteria(PATIENT_COUNT_QRY,
            Arrays.asList(new Object[] { getId() }));
        return getCountResult(appService, criteria);
    }

    private static final String CHILD_SPECIMENS_COUNT_QRY = "select count(childSpcs) from "
        + Site.class.getName()
        + " site left join site."
        + SitePeer.PROCESSING_EVENT_COLLECTION.getName()
        + " as pevent join pevent."
        + ProcessingEventPeer.SPECIMEN_COLLECTION.getName()
        + " as parentSpc join parentSpc."
        + SpecimenPeer.CHILD_SPECIMEN_COLLECTION.getName()
        + " as childSpcs where site." + SitePeer.ID.getName() + "=?";

    public Long getAliquotedSpecimenCount() throws Exception {
        HQLCriteria criteria = new HQLCriteria(CHILD_SPECIMENS_COUNT_QRY,
            Arrays.asList(new Object[] { getId() }));
        return getCountResult(appService, criteria);
    }

    /**
     * get all site existing
     */
    public static List<SiteWrapper> getSites(
        WritableApplicationService appService) throws Exception {
        return getSites(appService, null);
    }

    private static final String SITES_QRY = "from " + Site.class.getName();

    /**
     * If "id" is null, then all sites are returned. If not, then only sites
     * with that id are returned.
     */
    public static List<SiteWrapper> getSites(
        WritableApplicationService appService, Integer id) throws Exception {
        StringBuilder qry = new StringBuilder(SITES_QRY);
        List<Object> qryParms = new ArrayList<Object>();

        if (id != null) {
            qry.append(" where id = ?");
            qryParms.add(id);
        }

        HQLCriteria criteria = new HQLCriteria(qry.toString(), qryParms);
        List<Site> sites = appService.query(criteria);
        List<SiteWrapper> wrappers = new ArrayList<SiteWrapper>();
        for (Site s : sites) {
            wrappers.add(new SiteWrapper(appService, s));
        }
        return wrappers;
    }

    @Override
    public String toString() {
        return getName();
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getInTransitSentDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) propertiesMap
            .get("inTransitSentDispatchCollection");
        if (shipCollection == null) {
            List<DispatchWrapper> children = getSrcDispatchCollection(false);
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper dispatch : children) {
                    if (DispatchState.IN_TRANSIT.equals(dispatch
                        .getDispatchState())) {
                        shipCollection.add(dispatch);
                    }
                }
                propertiesMap.put("inTransitSentDispatchCollection",
                    shipCollection);
            }
        }
        return shipCollection;
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getInTransitReceiveDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) propertiesMap
            .get("inTransitReceiveDispatchCollection");
        if (shipCollection == null) {
            List<DispatchWrapper> children = getDstDispatchCollection(false);
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper dispatch : children) {
                    if (DispatchState.IN_TRANSIT.equals(dispatch
                        .getDispatchState())) {
                        shipCollection.add(dispatch);
                    }
                }
                propertiesMap.put("inTransitReceiveDispatchCollection",
                    shipCollection);
            }
        }
        return shipCollection;
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getReceivingNoErrorsDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) propertiesMap
            .get("receivingDispatchCollection");
        if (shipCollection == null) {
            List<DispatchWrapper> children = getDstDispatchCollection(false);
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper dispatch : children) {
                    if (DispatchState.RECEIVED.equals(dispatch
                        .getDispatchState()) && !dispatch.hasErrors()) {
                        shipCollection.add(dispatch);
                    }
                }
                propertiesMap
                    .put("receivingDispatchCollection", shipCollection);
            }
        }
        return shipCollection;
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getReceivingWithErrorsDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) propertiesMap
            .get("receivingWithErrorsDispatchCollection");
        if (shipCollection == null) {
            List<DispatchWrapper> children = getDstDispatchCollection(false);
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper dispatch : children) {
                    if (DispatchState.RECEIVED.equals(dispatch
                        .getDispatchState()) && dispatch.hasErrors()) {
                        shipCollection.add(dispatch);
                    }
                }
                propertiesMap.put("receivingWithErrorsDispatchCollection",
                    shipCollection);
            }
        }
        return shipCollection;
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getInCreationDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) propertiesMap
            .get("inCreationDispatchCollection");
        if (shipCollection == null) {
            List<DispatchWrapper> children = getSrcDispatchCollection(false);
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper dispatch : children) {
                    if (DispatchState.CREATION.equals(dispatch
                        .getDispatchState())) {
                        shipCollection.add(dispatch);
                    }
                }
                propertiesMap.put("inCreationDispatchCollection",
                    shipCollection);
            }
        }
        return shipCollection;
    }

    @Override
    public void reload() {
        propertiesMap.clear();
        try {
            super.reload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<ClinicWrapper> getWorkingClinicCollection() {
        List<StudyWrapper> studies = getStudyCollection(false);
        Set<ClinicWrapper> clinics = new HashSet<ClinicWrapper>();
        for (StudyWrapper study : studies) {
            clinics.addAll(study.getClinicCollection());
        }
        return clinics;
    }

    private static final String WORKING_CLINIC_COLLECTION_SIZE = "select distinct contact."
        + ContactPeer.CLINIC.getName()
        + " from "
        + Site.class.getName()
        + " as site "
        + "inner join site."
        + SitePeer.STUDY_COLLECTION.getName()
        + " as study "
        + "inner join study."
        + StudyPeer.CONTACT_COLLECTION.getName()
        + " as contact where site." + SitePeer.ID.getName() + "=?";

    /**
     * Use an HQL query to quickly get the size of the collection.
     * 
     * @return The number of clinics associated to this repository stie.
     * @throws ApplicationException
     */
    public int getWorkingClinicCollectionSize() throws ApplicationException {
        HQLCriteria c = new HQLCriteria(WORKING_CLINIC_COLLECTION_SIZE,
            Arrays.asList(new Object[] { getId() }));
        List<Clinic> clinics = appService.query(c);
        return clinics.size();
    }

    /**
     * Only webadministrator can update the site object itself. To check if a
     * user can modify data inside a site, use user.canUpdateSite method
     */
    @Override
    public boolean canUpdate(User user) {
        return user.isWebsiteAdministrator();
    }

    public static Collection<? extends ModelWrapper<?>> getInTransitReceiveDispatchCollection(
        SiteWrapper site) {
        return site.getInTransitReceiveDispatchCollection();
    }

    public static Collection<? extends ModelWrapper<?>> getReceivingNoErrorsDispatchCollection(
        SiteWrapper site) {
        return site.getReceivingNoErrorsDispatchCollection();
    }

    public static Collection<? extends ModelWrapper<?>> getInCreationDispatchCollection(
        SiteWrapper site) {
        return site.getInCreationDispatchCollection();
    }

    public static Collection<? extends ModelWrapper<?>> getReceivingWithErrorsDispatchCollection(
        SiteWrapper site) {
        return site.getReceivingWithErrorsDispatchCollection();
    }

    public static Collection<? extends ModelWrapper<?>> getInTransitSentDispatchCollection(
        SiteWrapper site) {
        return site.getInTransitSentDispatchCollection();
    }

    public List<StudyWrapper> getStudyCollection() {
        return getStudyCollection(false);
    }

    @Deprecated
    public List<SiteWrapper> getStudyDispachSites(StudyWrapper study) {
        // TODO this can be removed once the gui doesn't use it anymore
        return null;
    }

    @Deprecated
    public Collection<? extends ModelWrapper<?>> getAcceptedRequestCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public List<StudyWrapper> getDispatchStudiesAsSender() {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public void addStudyDispatchSites(StudyWrapper study,
        List<SiteWrapper> addedSites) throws BiobankCheckException {
        // TODO Auto-generated method stub

    }

    public void removeStudyDispatchSites(StudyWrapper study,
        List<SiteWrapper> removedSites) {
        // TODO Auto-generated method stub

    }

    @Deprecated
    public Collection<? extends ModelWrapper<?>> getFilledRequestCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    public static final String COLLECTION_EVENT_COUNT_QRY = "select count(cevent) from "
        + Site.class.getName()
        + " as site join site."
        + SitePeer.SPECIMEN_COLLECTION.getName()
        + " as spcs join spcs."
        + SpecimenPeer.COLLECTION_EVENT.getName()
        + " as cevent where site."
        + SitePeer.ID.getName() + "=?";

    /**
     * Count events for specimen that are currently at this site
     */
    @Override
    public long getCollectionEventCount() throws ApplicationException,
        BiobankException {
        HQLCriteria criteria = new HQLCriteria(COLLECTION_EVENT_COUNT_QRY,
            Arrays.asList(new Object[] { getId() }));
        return getCountResult(appService, criteria);
    }

    private static final String COLLECTION_EVENT_COUNT_FOR_STUDY_QRY = "select count(distinct cEvent) from "
        + Site.class.getName()
        + " as site join site."
        + SitePeer.SPECIMEN_COLLECTION.getName()
        + " as specimens join specimens."
        + SpecimenPeer.COLLECTION_EVENT.getName()
        + " as cEvent where site."
        + SitePeer.ID.getName()
        + "=? and "
        + "cEvent."
        + Property.concatNames(CollectionEventPeer.PATIENT, PatientPeer.STUDY,
            StudyPeer.ID) + "=?";

    /**
     * Count events for specimen that are currently at this site
     */
    @Override
    public long getCollectionEventCountForStudy(StudyWrapper study)
        throws ApplicationException, BiobankException {
        HQLCriteria c = new HQLCriteria(COLLECTION_EVENT_COUNT_FOR_STUDY_QRY,
            Arrays.asList(new Object[] { getId(), study.getId() }));
        return getCountResult(appService, c);
    }

    public String getShipmentCount() {
        return null; // FIXME: no way to determine destination of shipinfos...
    }

    public static final String PATIENT_COUNT_FOR_STUDY_QRY = "select count(distinct patient) from "
        + Site.class.getName()
        + " as site join site."
        + SitePeer.SPECIMEN_COLLECTION.getName()
        + " as specimens where site."
        + SitePeer.ID.getName()
        + "=? and "
        + "specimens."
        + Property.concatNames(SpecimenPeer.COLLECTION_EVENT,
            CollectionEventPeer.PATIENT, PatientPeer.STUDY, StudyPeer.ID)
        + "=?";

    @Override
    public long getPatientCountForStudy(StudyWrapper study)
        throws ApplicationException, BiobankException {
        HQLCriteria c = new HQLCriteria(PATIENT_COUNT_FOR_STUDY_QRY,
            Arrays.asList(new Object[] { getId(), study.getId() }));
        return getCountResult(appService, c);
    }
}
