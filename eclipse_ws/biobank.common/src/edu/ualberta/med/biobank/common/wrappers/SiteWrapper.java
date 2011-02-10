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
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.AddressPeer;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.util.RequestState;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SiteWrapper extends CenterWrapper<Site> {
    private Map<RequestState, List<RequestWrapper>> requestCollectionMap = new HashMap<RequestState, List<RequestWrapper>>();

    public SiteWrapper(WritableApplicationService appService, Site wrappedObject) {
        super(appService, wrappedObject);
    }

    public SiteWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        // TODO: cache this?
        List<String> names = new ArrayList<String>();
        names.addAll(SitePeer.PROP_NAMES);
        names.addAll(AddressPeer.PROP_NAMES);
        return names;
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
    protected void persistDependencies(Site origObject) throws Exception {
        for (DispatchInfoWrapper diw : removedDispatchInfoWrapper) {
            if (!diw.isNew()) {
                diw.delete();
            }
        }
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        if ((getContainerCollection() != null && getContainerCollection()
            .size() > 0)
            || (getContainerTypeCollection() != null && getContainerTypeCollection()
                .size() > 0)
            || (getCollectionEventCollection() != null && getCollectionEventCollection()
                .size() > 0)) {
            throw new BiobankCheckException(
                "Unable to delete site "
                    + getName()
                    + ". All defined children (shipments, container types, and containers) must be removed first.");
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

    public List<RequestWrapper> getShippedRequestCollection() {
        return getRequestCollection(RequestState.SHIPPED);
    }

    public List<StudyWrapper> getStudyCollection() {
        return getStudyCollection(true);
    }

    public List<StudyWrapper> getStudiesNotAssoc() throws ApplicationException {
        List<StudyWrapper> studyWrappers = new ArrayList<StudyWrapper>();
        HQLCriteria c = new HQLCriteria("from " + Study.class.getName()
            + " s where " + getId() + " not in elements(s.siteCollection)");
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

    @SuppressWarnings("unchecked")
    public List<ContainerWrapper> getTopContainerCollection(boolean sort)
        throws Exception {
        List<ContainerWrapper> topContainerCollection = (List<ContainerWrapper>) propertiesMap
            .get("topContainerCollection");

        if (topContainerCollection == null) {
            topContainerCollection = new ArrayList<ContainerWrapper>();
            HQLCriteria criteria = new HQLCriteria("from "
                + Container.class.getName()
                + " where site.id = ? and containerType.topLevel = true",
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

    public Long getPatientCount() throws Exception {
        HQLCriteria criteria = new HQLCriteria(
            "select count(distinct patient) from " + Site.class.getName()
                + " as site " + "join site.shipmentCollection as shipments "
                + "join shipments.shipmentPatientCollection as csps "
                + "join csps.patient as patient " + "where site.id = ?",
            Arrays.asList(new Object[] { getId() }));
        List<Long> result = appService.query(criteria);
        if (result.size() != 1) {
            throw new BiobankQueryResultSizeException();
        }
        return result.get(0);
    }

    public Long getAliquotCount() throws Exception {
        HQLCriteria criteria = new HQLCriteria("select count(aliquots) from "
            + Site.class.getName() + " as site "
            + "join site.shipmentCollection as shipments "
            + "join shipments.shipmentPatientCollection as csps "
            + "join csps.processingEventCollection as visits "
            + "join visits.aliquotCollection as aliquots where site.id = ?",
            Arrays.asList(new Object[] { getId() }));
        List<Long> result = appService.query(criteria);
        if (result.size() != 1) {
            throw new BiobankQueryResultSizeException();
        }
        return result.get(0);
    }

    /**
     * get all site existing
     */
    public static List<SiteWrapper> getSites(
        WritableApplicationService appService) throws Exception {
        return getSites(appService, null);
    }

    /**
     * If "id" is null, then all sites are returned. If not, then only sites
     * with that id are returned.
     */
    public static List<SiteWrapper> getSites(
        WritableApplicationService appService, Integer id) throws Exception {
        HQLCriteria criteria;

        if (id == null) {
            criteria = new HQLCriteria("from " + Site.class.getName());
        } else {
            criteria = new HQLCriteria("from " + Site.class.getName()
                + " where id = ?", Arrays.asList(new Object[] { id }));
        }

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

    public List<DispatchWrapper> getReceivedDispatchCollection() {
        return getWrapperCollection(CenterPeer.DST_DISPATCH_COLLECTION,
            DispatchWrapper.class, false);
    }

    public List<DispatchWrapper> getSentDispatchCollection() {
        return getWrapperCollection(CenterPeer.SRC_DISPATCH_COLLECTION,
            DispatchWrapper.class, false);
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getInTransitSentDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) propertiesMap
            .get("inTransitSentDispatchCollection");
        if (shipCollection == null) {
            List<DispatchWrapper> children = getSentDispatchCollection();
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper dispatch : children) {
                    if (DispatchState.IN_TRANSIT.equals(dispatch.getState())) {
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
            List<DispatchWrapper> children = getReceivedDispatchCollection();
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper dispatch : children) {
                    if (DispatchState.IN_TRANSIT.equals(dispatch.getState())) {
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
            List<DispatchWrapper> children = getReceivedDispatchCollection();
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper dispatch : children) {
                    if (DispatchState.RECEIVED.equals(dispatch.getState())
                        && !dispatch.hasErrors()) {
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
            List<DispatchWrapper> children = getReceivedDispatchCollection();
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper dispatch : children) {
                    if (DispatchState.RECEIVED.equals(dispatch.getState())
                        && dispatch.hasErrors()) {
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
            List<DispatchWrapper> children = getSentDispatchCollection();
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper dispatch : children) {
                    if (DispatchState.CREATION.equals(dispatch.getState())) {
                        shipCollection.add(dispatch);
                    }
                }
                propertiesMap.put("inCreationDispatchCollection",
                    shipCollection);
            }
        }
        return shipCollection;
    }

    public Set<ClinicWrapper> getWorkingClinicCollection() {
        List<StudyWrapper> studies = getStudyCollection();
        Set<ClinicWrapper> clinics = new HashSet<ClinicWrapper>();
        for (StudyWrapper study : studies) {
            clinics.addAll(study.getClinicCollection());
        }
        return clinics;
    }

    /**
     * Use an HQL query to quickly get the size of the collection.
     * 
     * @return The number of clinics associated to this repository stie.
     * @throws ApplicationException
     */
    public int getWorkingClinicCollectionSize() throws ApplicationException {
        HQLCriteria c = new HQLCriteria("select distinct contact.clinic "
            + "from edu.ualberta.med.biobank.model.Site as site "
            + "inner join site.studyCollection study "
            + "inner join study.contactCollection contact "
            + "where site.id = ?", Arrays.asList(new Object[] { getId() }));
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

}
