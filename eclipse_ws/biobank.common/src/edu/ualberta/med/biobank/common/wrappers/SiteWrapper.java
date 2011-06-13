package edu.ualberta.med.biobank.common.wrappers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.ContactPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.util.RequestState;
import edu.ualberta.med.biobank.common.wrappers.base.SiteBaseWrapper;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SiteWrapper extends SiteBaseWrapper {
    private static final String TOP_CONTAINER_COLLECTION_CACHE_KEY = "topContainerCollection";
    private static final String EXISTING_CHILDREN_MSG = "Unable to delete site {0}. All defined children (processing events, container types, and containers) must be removed first.";

    private Map<RequestState, List<RequestWrapper>> requestCollectionMap = new HashMap<RequestState, List<RequestWrapper>>();

    public SiteWrapper(WritableApplicationService appService, Site wrappedObject) {
        super(appService, wrappedObject);
    }

    public SiteWrapper(WritableApplicationService appService) {
        super(appService);
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
        List<ContainerWrapper> topContainerCollection = (List<ContainerWrapper>) cache
            .get(TOP_CONTAINER_COLLECTION_CACHE_KEY);

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
            cache.put(TOP_CONTAINER_COLLECTION_CACHE_KEY,
                topContainerCollection);
        }
        return topContainerCollection;
    }

    public List<ContainerWrapper> getTopContainerCollection() throws Exception {
        return getTopContainerCollection(false);
    }

    public void clearTopContainerCollection() {
        cache.put(TOP_CONTAINER_COLLECTION_CACHE_KEY, null);
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

    private static final String PATIENT_COUNT_QRY = "select count(distinct cevent."
        + CollectionEventPeer.PATIENT.getName()
        + ") from "
        + Center.class.getName()
        + " as center join center."
        + SitePeer.SPECIMEN_COLLECTION.getName()
        + " as spcs join spcs."
        + SpecimenPeer.COLLECTION_EVENT.getName()
        + " as cevent where center."
        + SitePeer.ID.getName() + "=?";

    @Override
    public Long getPatientCount() throws Exception {
        HQLCriteria criteria = new HQLCriteria(PATIENT_COUNT_QRY,
            Arrays.asList(new Object[] { getId() }));
        return getCountResult(appService, criteria);
    }

    /**
     * Only webadministrator can update the site object itself. To check if a
     * user can modify data inside a site, use user.canUpdateSite method
     */
    @Override
    public boolean canUpdate(User user) {
        return user.isSuperAdministrator();
    }

    public List<StudyWrapper> getStudyCollection() {
        return getStudyCollection(false);
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

    @Override
    protected TaskList getPersistTasks() {
        TaskList tasks = new TaskList();

        // TODO: move to center?
        tasks.add(check().uniqueAndNotNull(SitePeer.NAME));
        tasks.add(check().uniqueAndNotNull(SitePeer.NAME_SHORT));

        tasks.add(super.getPersistTasks());

        return tasks;
    }

    @Override
    protected TaskList getDeleteTasks() {
        TaskList tasks = new TaskList();

        String errMsg = MessageFormat.format(EXISTING_CHILDREN_MSG, getName());
        tasks.add(check().empty(SitePeer.CONTAINER_COLLECTION, errMsg));
        tasks.add(check().empty(SitePeer.CONTAINER_TYPE_COLLECTION, errMsg));
        tasks.add(check().empty(SitePeer.PROCESSING_EVENT_COLLECTION, errMsg));

        tasks.add(super.getDeleteTasks());

        return tasks;
    }

    // TODO: remove this override when all persist()-s are like this!
    @Override
    public void persist() throws Exception {
        WrapperTransaction.persist(this, appService);
    }

    @Override
    public void delete() throws Exception {
        WrapperTransaction.delete(this, appService);
    }
}
