package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SampleTypeWrapper extends ModelWrapper<SampleType> implements
    Comparable<SampleTypeWrapper> {

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

    @Override
    public int compareTo(SampleTypeWrapper wrapper) {
        String myName = wrappedObject.getName();
        String wrapperName = wrapper.wrappedObject.getName();

        int compare = myName.compareTo(wrapperName);
        if (compare == 0) {
            String myNameShort = wrappedObject.getNameShort();
            String wrapperNameShort = wrapper.wrappedObject.getNameShort();

            return ((myNameShort.compareTo(wrapperNameShort) > 0) ? 1
                : (myNameShort.equals(wrapperNameShort) ? 0 : -1));
        }
        return (compare > 0) ? 1 : -1;
    }

    public static List<SampleTypeWrapper> transformToWrapperList(
        WritableApplicationService appService, List<SampleType> sampleTypes) {
        List<SampleTypeWrapper> list = new ArrayList<SampleTypeWrapper>();
        for (SampleType type : sampleTypes) {
            list.add(new SampleTypeWrapper(appService, type));
        }
        return list;
    }

    public static List<SampleTypeWrapper> getAllWrappers(
        WritableApplicationService appService, boolean sort)
        throws ApplicationException {
        List<SampleType> sampleTypes = appService.search(SampleType.class,
            new SampleType());
        List<SampleTypeWrapper> list = transformToWrapperList(appService,
            sampleTypes);
        if (sort)
            Collections.sort(list);
        return list;
    }

    public static List<SampleTypeWrapper> getAllWrappers(
        WritableApplicationService appService) throws ApplicationException {
        return getAllWrappers(appService, false);
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

    public static void setGlobalSampleTypes(
        List<SampleTypeWrapper> newGlobalSampleTypes,
        List<SampleTypeWrapper> oldGlobalSampleTypes)
        throws BiobankCheckException, Exception {
        deleteOldSampleTypes(newGlobalSampleTypes, oldGlobalSampleTypes);
        for (SampleTypeWrapper ss : newGlobalSampleTypes) {
            ss.persist();
        }
    }

    private static void deleteOldSampleTypes(List<SampleTypeWrapper> newTypes,
        List<SampleTypeWrapper> oldTypes) throws BiobankCheckException,
        Exception {
        if (newTypes.size() == 0) {
            // remove all
            Iterator<SampleTypeWrapper> it = oldTypes.iterator();
            while (it.hasNext()) {
                it.next().delete();
            }
            return;
        }

        List<Integer> idList = new ArrayList<Integer>();
        for (SampleTypeWrapper ss : newTypes) {
            idList.add(ss.getId());
        }
        Iterator<SampleTypeWrapper> it = oldTypes.iterator();
        while (it.hasNext()) {
            SampleTypeWrapper ss = it.next();
            if (!idList.contains(ss.getId())) {
                ss.delete();
            }
        }
    }
}
