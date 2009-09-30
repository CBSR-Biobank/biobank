package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

//FIXME todo by delphine
public class ContainerTypeWrapper extends ModelWrapper<ContainerType> {

    public ContainerTypeWrapper(WritableApplicationService appService,
        ContainerType wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected void firePropertyChanges(ContainerType oldWrappedObject,
        ContainerType newWrappedObject) throws Exception {
        String[] members = new String[] { "name", "comment", "nameShort",
            "topLevel", "defaultTemperature", "activityStatus",
            "sampleTypeCollection", "childContainerTypeCollection", "site",
            "capacity", "childLabelingScheme" };
        firePropertyChanges(members, oldWrappedObject, newWrappedObject);
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
        checkNameUnique();
        if (!isNew()) {
            boolean exists = existsContainerWithType();
            ContainerType oldObject = getObjectFromDatabase();
            checkNewCapacity(oldObject, exists);
            checkTopLevel(oldObject, exists);
            checkLabelingScheme(oldObject, exists);
        }
    }

    private void checkNameUnique() throws ApplicationException,
        BiobankCheckException {
        String notSameTypeString = "";
        List<Object> params = new ArrayList<Object>(Arrays.asList(new Object[] {
            getSite(), getName() }));
        if (!isNew()) {
            notSameTypeString = " and id<>?";
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria("from " + ContainerType.class.getName()
            + " where site = ? and name = ?" + notSameTypeString, params);

        List<Object> results = appService.query(c);
        if (results.size() != 0) {
            throw new BiobankCheckException("A storage type with name \""
                + getName() + "\" already exists.");
        }
    }

    @Override
    protected Class<ContainerType> getWrappedClass() {
        return ContainerType.class;
    }

    public Collection<ContainerType> getAllChildren() {
        List<ContainerType> allChildren = new ArrayList<ContainerType>();
        for (ContainerType type : getChildContainerTypeCollection()) {
            allChildren.addAll(new ContainerTypeWrapper(appService, type)
                .getAllChildren());
            allChildren.add(type);
        }
        return allChildren;
    }

    public Collection<SampleType> getSampleTypes(boolean useChildrenRecursively) {
        List<SampleType> sampleTypes = new ArrayList<SampleType>();
        sampleTypes.addAll(getSampleTypeCollection());
        if (useChildrenRecursively) {
            for (ContainerType type : getChildContainerTypeCollection()) {
                sampleTypes.addAll(new ContainerTypeWrapper(appService, type)
                    .getSampleTypes(useChildrenRecursively));
            }
        }
        return sampleTypes;
    }

    /**
     * Get containers types defined in a site. if useStrictName is true, then
     * the container type name should be exactly containerName, otherwise, it
     * will contains containerName.
     */
    public static List<ContainerType> getContainerTypesInSite(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        String containerName, boolean useStrictName)
        throws ApplicationException {
        String nameComparison = "=";
        String containerNameParameter = containerName;
        if (!useStrictName) {
            nameComparison = "like";
            containerNameParameter = "%" + containerName + "%";
        }
        String query = "from " + ContainerType.class.getName()
            + " where site = ? and name " + nameComparison + " ?";
        HQLCriteria criteria = new HQLCriteria(query, Arrays
            .asList(new Object[] { siteWrapper.getWrappedObject(),
                containerNameParameter }));
        return appService.query(criteria);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        String queryString = "select c.containerType from "
            + Container.class.getName() + " as c where c.containerType=?)";
        HQLCriteria c = new HQLCriteria(queryString, Arrays
            .asList(new Object[] { wrappedObject }));
        List<Object> results = appService.query(c);
        if (results.size() > 0) {
            throw new BiobankCheckException("Unable to delete container type "
                + getName() + ". A container of this type exists in storage."
                + " Remove all instances before deleting this type.");
        }
    }

    public void setName(String name) {
        String oldName = wrappedObject.getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange("name", oldName, name);
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setComment(String comment) {
        String oldComment = wrappedObject.getComment();
        wrappedObject.setName(comment);
        propertyChangeSupport
            .firePropertyChange("comment", oldComment, comment);
    }

    public String getComment() {
        return wrappedObject.getComment();
    }

    public void setNameShort(String nameShort) {
        String oldNameShort = wrappedObject.getNameShort();
        wrappedObject.setNameShort(nameShort);
        propertyChangeSupport.firePropertyChange("nameShort", oldNameShort,
            nameShort);
    }

    public String getNameShort() {
        return wrappedObject.getNameShort();
    }

    public void setTopLevel(Boolean topLevel) {
        Boolean oldTopLevel = wrappedObject.getTopLevel();
        wrappedObject.setTopLevel(topLevel);
        propertyChangeSupport.firePropertyChange("topLevel", oldTopLevel,
            topLevel);
    }

    public Boolean getTopLevel() {
        return wrappedObject.getTopLevel();
    }

    public void setDefaultTemperature(Double temperature) {
        Double oldTemp = wrappedObject.getDefaultTemperature();
        wrappedObject.setDefaultTemperature(temperature);
        propertyChangeSupport.firePropertyChange("defaultTemperature", oldTemp,
            temperature);
    }

    public Double getDefaultTemperature() {
        return wrappedObject.getDefaultTemperature();
    }

    public void setActivityStatus(String activityStatus) {
        String oldActivityStatus = wrappedObject.getActivityStatus();
        wrappedObject.setActivityStatus(activityStatus);
        propertyChangeSupport.firePropertyChange("activityStatus",
            oldActivityStatus, activityStatus);
    }

    public String getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setSampleTypeCollection(Collection<SampleType> sampleTypes) {
        Collection<SampleType> oldSampleTypes = wrappedObject
            .getSampleTypeCollection();
        wrappedObject.setSampleTypeCollection(sampleTypes);
        propertyChangeSupport.firePropertyChange("sampleTypeCollection",
            oldSampleTypes, sampleTypes);
    }

    public Collection<SampleType> getSampleTypeCollection() {
        return wrappedObject.getSampleTypeCollection();
    }

    public void setChildContainerTypeCollection(
        Collection<ContainerType> containerTypes) {
        Collection<ContainerType> oldContainerTypes = wrappedObject
            .getChildContainerTypeCollection();
        wrappedObject.setChildContainerTypeCollection(containerTypes);
        propertyChangeSupport.firePropertyChange(
            "childContainerTypeCollection", oldContainerTypes, containerTypes);
    }

    public Collection<ContainerType> getChildContainerTypeCollection() {
        return wrappedObject.getChildContainerTypeCollection();
    }

    public void setSite(Site site) {
        Site oldSite = wrappedObject.getSite();
        wrappedObject.setSite(site);
        propertyChangeSupport.firePropertyChange("site", oldSite, site);
    }

    public Site getSite() {
        return wrappedObject.getSite();
    }

    public void setCapacity(Capacity capacity) {
        Capacity oldCapacity = wrappedObject.getCapacity();
        wrappedObject.setCapacity(capacity);
        propertyChangeSupport.firePropertyChange("capacity", oldCapacity,
            capacity);
    }

    public Capacity getCapacity() {
        return wrappedObject.getCapacity();
    }

    public void setChildLabelingScheme(ContainerLabelingScheme scheme) {
        ContainerLabelingScheme oldLbl = wrappedObject.getChildLabelingScheme();
        wrappedObject.setChildLabelingScheme(scheme);
        propertyChangeSupport.firePropertyChange("childLabelingScheme", oldLbl,
            scheme);
    }

    public ContainerLabelingScheme getChildLabelingScheme() {
        return wrappedObject.getChildLabelingScheme();
    }

    public void setSampleTypes(List<Integer> sampleTypesIds,
        List<SampleType> allSampleTypes) throws BiobankCheckException {
        Set<SampleType> selSampleTypes = new HashSet<SampleType>();
        if (sampleTypesIds != null) {
            for (SampleType sampleType : allSampleTypes) {
                int id = sampleType.getId();
                if (sampleTypesIds.indexOf(id) >= 0) {
                    selSampleTypes.add(sampleType);
                }
            }
            if (selSampleTypes.size() != sampleTypesIds.size()) {
                throw new BiobankCheckException(
                    "Problem with sample type selections");
            }
        }
        setSampleTypeCollection(selSampleTypes);
    }

    public void setChildContainerTypes(List<Integer> containerTypesIds,
        List<ContainerType> allContainerTypes) throws BiobankCheckException,
        ApplicationException {
        Collection<ContainerType> selContainerTypes = new HashSet<ContainerType>();
        if (containerTypesIds != null) {
            if (allContainerTypes != null) {
                for (ContainerType containerType : allContainerTypes) {
                    int id = containerType.getId();
                    if (containerTypesIds.indexOf(id) >= 0) {
                        selContainerTypes.add(containerType);
                    }
                }
            }
        }
        Collection<ContainerType> children = getChildContainerTypeCollection();
        List<Integer> missing = new ArrayList<Integer>();
        if (children != null) {
            for (ContainerType child : children) {
                int id = child.getId();
                if (containerTypesIds.indexOf(id) < 0) {
                    missing.add(id);
                }
            }
        }

        if (missing.size() == 0 || canRemoveChildrenContainer(missing)) {
            setChildContainerTypeCollection(selContainerTypes);
        } else {
            throw new BiobankCheckException(
                "Unable to remove child type. This parent/child relationship exists in storage. Remove all instances before attempting to delete a child type.");
        }
    }

    private boolean canRemoveChildrenContainer(List<Integer> missing)
        throws ApplicationException {
        String queryString = "from "
            + ContainerPosition.class.getName()
            + " as cp inner join cp.parentContainer as cparent"
            + " where cparent.containerType.id=? and cp.container.containerType.id in (select id from "
            + ContainerType.class.getName() + " as ct where ct.id=?";
        List<Object> params = new ArrayList<Object>();
        params.add(getId());
        params.add(missing.get(0));
        for (int i = 1; i < missing.size(); i++) {
            queryString += "OR ct.id=?";
            params.add(missing.get(i));
        }
        queryString += ")";

        List<Object> results = appService.query(new HQLCriteria(queryString,
            params));
        return results.size() == 0;
    }

    /**
     * Check if we can use the new capacity
     */
    private void checkNewCapacity(ContainerType oldObject,
        boolean existsContainersWithType) throws BiobankCheckException,
        Exception {
        Capacity currentCapacity = getCapacity();
        Capacity dbCapacity = oldObject.getCapacity();
        if (!(currentCapacity.getRowCapacity().equals(
            dbCapacity.getRowCapacity()) && currentCapacity.getColCapacity()
            .equals(dbCapacity.getColCapacity()))
            && existsContainersWithType) {
            throw new BiobankCheckException(
                "Unable to alter dimensions. A container of this type exists in storage. Remove all instances before attempting to modify this container type.");
        }
    }

    /**
     * return true if at least one container exists with this container type
     */
    private boolean existsContainerWithType() throws ApplicationException {
        HQLCriteria c = new HQLCriteria("select c.containerType from "
            + Container.class.getName() + " as c where c.containerType=?)",
            Arrays.asList(new Object[] { wrappedObject }));
        List<Object> results = appService.query(c);
        return results.size() > 0;
    }

    private void checkTopLevel(ContainerType oldObject,
        boolean existsContainersWithType) throws BiobankCheckException {
        if (!getTopLevel().equals(oldObject.getTopLevel())
            && existsContainersWithType) {
            throw new BiobankCheckException(
                "Unable to change the \"Top Level\" property. A container requiring this property exists in storage. Remove all instances before attempting to modify this container type.");
        }
    }

    private void checkLabelingScheme(ContainerType oldObject,
        boolean existsContainersWithType) throws BiobankCheckException {
        if (!getChildLabelingScheme().getId().equals(
            oldObject.getChildLabelingScheme().getId())
            && existsContainersWithType) {
            throw new BiobankCheckException(
                "Unable to change the \"Child Labeling scheme\" property. A container requiring this property exists in storage. Remove all instances before attempting to modify this container type.");
        }
    }

    public static Collection<ContainerType> getTopContainerTypesInSite(
        WritableApplicationService appService, SiteWrapper site)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + ContainerType.class.getName()
            + " where site = ? and topLevel=true", Arrays
            .asList(new Object[] { site.getWrappedObject() }));
        return appService.query(criteria);
    }
}
