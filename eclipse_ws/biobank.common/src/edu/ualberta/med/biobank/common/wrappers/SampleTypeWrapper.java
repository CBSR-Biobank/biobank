package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SampleTypeWrapper extends ModelWrapper<SampleType> {

    public SampleTypeWrapper(WritableApplicationService appService,
        SampleType wrappedObject) {
        super(appService, wrappedObject);
    }

    public SampleTypeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "name", "nameShort", "site",
            "containerTypeCollection" };
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String name) {
        String oldName = getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange("name", oldName, name);
    }

    public String getNameShort() {
        return wrappedObject.getNameShort();
    }

    public void setNameShort(String nameShort) {
        String oldNameShort = getNameShort();
        wrappedObject.setNameShort(nameShort);
        propertyChangeSupport.firePropertyChange("nameShort", oldNameShort,
            nameShort);
    }

    public Site getSite() {
        return wrappedObject.getSite();
    }

    public void setSite(Site site) {
        String oldNameShort = getNameShort();
        wrappedObject.setSite(site);
        propertyChangeSupport.firePropertyChange("site", oldNameShort, site);
    }

    public void setSite(SiteWrapper site) {
        setSite(site.wrappedObject);
    }

    @SuppressWarnings("unchecked")
    public List<ContainerTypeWrapper> getContainerTypeCollection(boolean sort) {
        List<ContainerTypeWrapper> containerTypeCollection = (List<ContainerTypeWrapper>) propertiesMap
            .get("containerTypeCollection");
        if (containerTypeCollection == null) {
            Collection<ContainerType> children = wrappedObject
                .getContainerTypeCollection();
            if (children != null) {
                containerTypeCollection = new ArrayList<ContainerTypeWrapper>();
                for (ContainerType type : children) {
                    containerTypeCollection.add(new ContainerTypeWrapper(
                        appService, type));
                }
                propertiesMap.put("containerTypeCollection",
                    containerTypeCollection);
            }
        }
        if ((containerTypeCollection != null) && sort)
            Collections.sort(containerTypeCollection);
        return containerTypeCollection;
    }

    public List<ContainerTypeWrapper> getContainerTypeCollection() {
        return getContainerTypeCollection(false);
    }

    public void setContainerTypeCollection(Collection<ContainerType> types,
        boolean setNull) {
        Collection<ContainerType> oldTypes = wrappedObject
            .getContainerTypeCollection();
        wrappedObject.setContainerTypeCollection(types);
        propertyChangeSupport.firePropertyChange("containerTypeCollection",
            oldTypes, types);
        if (setNull) {
            propertiesMap.put("containerTypeCollection", null);
        }
    }

    public void setContainerTypeCollection(List<ContainerTypeWrapper> types) {
        Collection<ContainerType> typeObjects = new HashSet<ContainerType>();
        for (ContainerTypeWrapper type : types) {
            typeObjects.add(type.getWrappedObject());
        }
        setContainerTypeCollection(typeObjects, false);
        propertiesMap.put("containerTypeCollection", types);
    }

    @Override
    public Class<SampleType> getWrappedClass() {
        return SampleType.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
    }

    /**
     * get all sample types in a site for containers which type name contains
     * "typeNameContains"
     */
    public static List<SampleTypeWrapper> getSampleTypeForContainerTypes(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        String typeNameContains) throws ApplicationException {
        List<ContainerTypeWrapper> types = ContainerTypeWrapper
            .getContainerTypesInSite(appService, siteWrapper, typeNameContains,
                false);
        List<SampleTypeWrapper> sampleTypes = new ArrayList<SampleTypeWrapper>();
        for (ContainerTypeWrapper type : types) {
            sampleTypes.addAll(type.getSampleTypeCollectionRecursively());
        }
        return sampleTypes;
    }

    @Override
    public boolean checkIntegrity() {
        return true;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        // do nothing for now
    }

    public static List<SampleTypeWrapper> transformToWrapperList(
        WritableApplicationService appService, List<SampleType> sampleTypes) {
        List<SampleTypeWrapper> list = new ArrayList<SampleTypeWrapper>();
        for (SampleType type : sampleTypes) {
            list.add(new SampleTypeWrapper(appService, type));
        }
        return list;
    }

    public static List<SampleTypeWrapper> getGlobalSampleTypes(
        WritableApplicationService appService, boolean sort)
        throws ApplicationException {
        HQLCriteria c = new HQLCriteria("from " + SampleType.class.getName()
            + " where site = null");

        List<SampleType> sampleTypes = appService.query(c);
        List<SampleTypeWrapper> list = transformToWrapperList(appService,
            sampleTypes);
        if (sort)
            Collections.sort(list);
        return list;
    }

    /**
     * This method should only be called to save the new sample type list. The
     * differences between the old list and the new list will be deleted and the
     * new list written to the database.
     * 
     * @param appService
     * @param newGlobalSampleTypes
     * 
     * @throws BiobankCheckException
     * @throws Exception
     */
    public static void setGlobalSampleTypes(
        WritableApplicationService appService,
        List<SampleTypeWrapper> newGlobalSampleTypes)
        throws BiobankCheckException, Exception {
        SampleTypeWrapper
            .deleteOldSampleTypes(appService, newGlobalSampleTypes);
        for (SampleTypeWrapper ss : newGlobalSampleTypes) {
            ss.persist();
        }
    }

    private static void deleteOldSampleTypes(
        WritableApplicationService appService, List<SampleTypeWrapper> newTypes)
        throws BiobankCheckException, Exception {
        List<SampleTypeWrapper> oldTypes = getGlobalSampleTypes(appService,
            false);
        if (oldTypes != null) {
            for (SampleTypeWrapper ss : oldTypes) {
                if ((newTypes == null) || !newTypes.contains(ss)) {
                    ss.delete();
                }
            }
        }
    }

    @Override
    public int compareTo(ModelWrapper<SampleType> wrapper) {
        String name1 = wrappedObject.getName();
        String name2 = wrapper.wrappedObject.getName();

        int compare = name1.compareTo(name2);
        if (compare == 0) {
            String nameShort1 = wrappedObject.getNameShort();
            String nameShort2 = wrapper.wrappedObject.getNameShort();

            return ((nameShort1.compareTo(nameShort2) > 0) ? 1 : (nameShort1
                .equals(nameShort2) ? 0 : -1));
        }
        return (compare > 0) ? 1 : -1;
    }
}
