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
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.util.RequestState;
import edu.ualberta.med.biobank.common.wrappers.base.SiteBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.AddressWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.DispatchInfoWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.DispatchInfo;
import edu.ualberta.med.biobank.model.Shipment;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SiteWrapper extends SiteBaseWrapper {

    private Map<RequestState, List<RequestWrapper>> requestCollectionMap = new HashMap<RequestState, List<RequestWrapper>>();

    private List<DispatchInfoWrapper> removedDispatchInfoWrapper = new ArrayList<DispatchInfoWrapper>();

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

    private AddressWrapper initAddress() {
        AddressWrapper address = getAddress();
        if (address == null) {
            address = new AddressWrapper(appService);
            setAddress(address);
        }
        return address;
    }

    public String getStreet1() {
        return getProperty(getAddress(), AddressPeer.STREET1);
    }

    public void setStreet1(String street1) {
        setProperty(initAddress(), AddressPeer.STREET1, street1);
    }

    public String getStreet2() {
        return getProperty(getAddress(), AddressPeer.STREET2);
    }

    public void setStreet2(String street2) {
        setProperty(initAddress(), AddressPeer.STREET2, street2);
    }

    public String getCity() {
        return getProperty(getAddress(), AddressPeer.CITY);
    }

    public void setCity(String city) {
        setProperty(initAddress(), AddressPeer.CITY, city);
    }

    public String getProvince() {
        return getProperty(getAddress(), AddressPeer.PROVINCE);
    }

    public void setProvince(String province) {
        setProperty(initAddress(), AddressPeer.PROVINCE, province);
    }

    public String getPostalCode() {
        return getProperty(getAddress(), AddressPeer.POSTAL_CODE);
    }

    public void setPostalCode(String postalCode) {
        setProperty(initAddress(), AddressPeer.POSTAL_CODE, postalCode);
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
    public Class<Site> getWrappedClass() {
        return Site.class;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        if ((getContainerCollection() != null && getContainerCollection()
            .size() > 0)
            || (getContainerTypeCollection() != null && getContainerTypeCollection()
                .size() > 0)
            || (getShipmentCollection() != null && getShipmentCollection()
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

    public List<RequestWrapper> getAcceptedRequestCollection() {
        return getRequestCollection(RequestState.ACCEPTED);
    }

    public List<RequestWrapper> getFilledRequestCollection() {
        return getRequestCollection(RequestState.FILLED);
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

    public List<ShipmentWrapper> getShipmentCollection() {
        return getShipmentCollection(true);
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
     * Search for shipments in the site with the given waybill
     * 
     * @throws BiobankCheckException
     */
    public Long getShipmentCount() throws ApplicationException,
        BiobankException {
        HQLCriteria criteria = new HQLCriteria("select count(*) from "
            + Shipment.class.getName() + " where site.id = ?",
            Arrays.asList(new Object[] { getId() }));
        List<Long> result = appService.query(criteria);
        if (result.size() != 1) {
            throw new BiobankQueryResultSizeException();
        }
        return result.get(0);
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

    public Long getPatientVisitCount() throws Exception {
        HQLCriteria criteria = new HQLCriteria("select count(visits) from "
            + Site.class.getName() + " as site "
            + "join site.shipmentCollection as shipments "
            + "join shipments.shipmentPatientCollection as csps "
            + "join csps.patientVisitCollection as visits "
            + "where site.id = ?", Arrays.asList(new Object[] { getId() }));
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
            + "join csps.patientVisitCollection as visits "
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

    @Override
    public void resetInternalFields() {
        removedDispatchInfoWrapper.clear();
    }

    public List<StudyWrapper> getDispatchStudiesAsSender() {
        Map<Integer, DispatchInfoWrapper> srcMap = getSrcDispatchInfoCollection();
        if (srcMap == null)
            return null;
        List<StudyWrapper> wrappers = new ArrayList<StudyWrapper>();
        for (DispatchInfoWrapper diw : srcMap.values()) {
            wrappers.add(diw.getStudy());
        }
        return wrappers;
    }

    @SuppressWarnings("unchecked")
    public List<StudyWrapper> getDispatchStudiesAsReceiver() {
        List<StudyWrapper> studies = (List<StudyWrapper>) propertiesMap
            .get("dispatchStudiesAsReceiver");
        if (studies == null) {
            Collection<DispatchInfo> children = wrappedObject
                .getDestDispatchInfoCollection();
            if (children != null) {
                studies = new ArrayList<StudyWrapper>();
                for (DispatchInfo di : children) {
                    studies.add(new StudyWrapper(appService, di.getStudy()));
                }
                propertiesMap.put("dispatchStudiesAsReceiver", studies);
            }
        }
        return studies;
    }

    public List<SiteWrapper> getStudyDispachSites(StudyWrapper study)
        throws BiobankException {
        if (study == null) {
            throw new BiobankException("study is null");
        }
        Map<Integer, DispatchInfoWrapper> srcMap = getSrcDispatchInfoCollection();
        if (srcMap == null)
            return null;
        DispatchInfoWrapper info = srcMap.get(study.getId());
        if (info == null)
            return null;
        return info.getDestSiteCollection();
    }

    public void addStudyDispatchSites(StudyWrapper study,
        List<SiteWrapper> sites) throws BiobankCheckException {
        if ((sites == null) || (sites.size() == 0))
            return;
        Map<Integer, DispatchInfoWrapper> infos = getSrcDispatchInfoCollection();
        DispatchInfoWrapper diw = null;
        if (infos != null) {
            diw = infos.get(study.getId());
        }
        if (diw == null) {
            List<StudyWrapper> studies = getStudyCollection();
            if (studies == null || !studies.contains(study)) {
                throw new BiobankCheckException("Site " + getNameShort()
                    + " cannot dispatch aliquots from study "
                    + study.getNameShort()
                    + ": this study is not part of its current studies list.");
            }
            diw = new DispatchInfoWrapper(appService);
            diw.setStudy(study);
            diw.setSrcSite(this);
            if (infos != null) {
                infos.put(study.getId(), diw);
            }
            Collection<DispatchInfo> allsInfoObjects = wrappedObject
                .getSrcDispatchInfoCollection();
            if (allsInfoObjects == null) {
                allsInfoObjects = new HashSet<DispatchInfo>();
            } else {
                allsInfoObjects = new HashSet<DispatchInfo>(allsInfoObjects);
            }
            allsInfoObjects.add(diw.wrappedObject);
            wrappedObject.setSrcDispatchInfoCollection(allsInfoObjects);
        }
        diw.addDestSites(sites);
    }

    public void removeStudyDispatchSites(StudyWrapper study,
        List<SiteWrapper> sites) {
        if ((sites == null) || (sites.size() == 0))
            return;
        Map<Integer, DispatchInfoWrapper> infos = getSrcDispatchInfoCollection();
        if (infos != null) {
            DispatchInfoWrapper diw = infos.get(study.getId());
            if (diw != null) {
                diw.removeDestSites(sites);
                if (diw.getDestSiteCollection().size() == 0) {
                    infos.remove(study.getId());
                    removedDispatchInfoWrapper.add(diw);
                    Collection<DispatchInfo> diList = wrappedObject
                        .getSrcDispatchInfoCollection();
                    DispatchInfo diToRemove = null;
                    for (DispatchInfo di : diList) {
                        if (di.getId().equals(diw.getId())) {
                            diToRemove = di;
                            break;
                        }
                    }
                    diList.remove(diToRemove);
                    wrappedObject.setSrcDispatchInfoCollection(diList);
                }
            }
        }
    }

    public List<DispatchWrapper> getReceivedDispatchCollection() {
        return getReceivedDispatchCollection(false);
    }

    public List<DispatchWrapper> getSentDispatchCollection() {
        return getWrapperCollection(SitePeer.SENT_DISPATCH_COLLECTION,
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
                for (DispatchWrapper ship : children) {
                    if (ship.isInTransitState()) {
                        shipCollection.add(ship);
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
                for (DispatchWrapper ship : children) {
                    if (ship.isInTransitState()) {
                        shipCollection.add(ship);
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
                for (DispatchWrapper ship : children) {
                    if (ship.isInReceivedState() && !ship.hasErrors()) {
                        shipCollection.add(ship);
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
                for (DispatchWrapper ship : children) {
                    if (ship.isInReceivedState() && ship.hasErrors()) {
                        shipCollection.add(ship);
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
                for (DispatchWrapper ship : children) {
                    if (ship.isInCreationState()) {
                        shipCollection.add(ship);
                    }
                }
                propertiesMap.put("inCreationDispatchCollection",
                    shipCollection);
            }
        }
        return shipCollection;
    }

    /**
     * For one study, this site has one source dispatch info associated.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<Integer, DispatchInfoWrapper> getSrcDispatchInfoCollection() {
        Map<Integer, DispatchInfoWrapper> infos = (Map<Integer, DispatchInfoWrapper>) propertiesMap
            .get("srcDispatchInfoCollection");
        if (infos == null) {
            Collection<DispatchInfo> children = wrappedObject
                .getSrcDispatchInfoCollection();
            if (children != null) {
                infos = new HashMap<Integer, DispatchInfoWrapper>();
                for (DispatchInfo di : children) {
                    Integer studyId = di.getStudy().getId();
                    infos.put(studyId, new DispatchInfoWrapper(appService, di));
                }
                propertiesMap.put("srcDispatchInfoCollection", infos);
            }
        }
        return infos;
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
