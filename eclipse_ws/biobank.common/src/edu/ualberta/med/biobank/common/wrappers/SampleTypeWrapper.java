package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    protected String[] getPropertyChangeNames() {
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

    public SiteWrapper getSite() {
        Site site = wrappedObject.getSite();
        if (site == null) {
            return null;
        }
        return new SiteWrapper(appService, site);
    }

    public void setSite(Site site) {
        Site oldSite = wrappedObject.getSite();
        wrappedObject.setSite(site);
        propertyChangeSupport.firePropertyChange("site", oldSite, site);
    }

    public void setSite(SiteWrapper site) {
        if (site == null) {
            setSite((Site) null);
        } else {
            setSite(site.getWrappedObject());
        }
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

    @Override
    public Class<SampleType> getWrappedClass() {
        return SampleType.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        if (getName() == null || getNameShort() == null) {
            throw new BiobankCheckException(
                "Name and short name of this sample type cannot be null.");
        }
        checkNameUnique();
        checkNameShortUnique();
    }

    private void checkNameShortUnique() throws ApplicationException,
        BiobankCheckException {
        HQLCriteria c;
        if (isNew()) {
            c = new HQLCriteria("from " + SampleType.class.getName()
                + " where nameShort = ?", Arrays
                .asList(new Object[] { getNameShort() }));
        } else {
            c = new HQLCriteria("from " + SampleType.class.getName()
                + " where id <> ? and nameShort = ?", Arrays
                .asList(new Object[] { getId(), getNameShort() }));
        }
        List<Object> results = appService.query(c);
        if (results.size() > 0) {
            throw new BiobankCheckException("A sample with short name \""
                + getNameShort() + "\" already exists.");
        }
    }

    private void checkNameUnique() throws BiobankCheckException,
        ApplicationException {
        HQLCriteria c;
        if (isNew()) {
            c = new HQLCriteria("from " + SampleType.class.getName()
                + " where name = ?", Arrays.asList(new Object[] { getName() }));
        } else {
            c = new HQLCriteria("from " + SampleType.class.getName()
                + " where id <> ? and name = ?", Arrays.asList(new Object[] {
                getId(), getName() }));
        }
        List<Object> results = appService.query(c);
        if (results.size() > 0) {
            throw new BiobankCheckException("A sample with name \"" + getName()
                + "\" already exists.");
        }
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
        Set<SampleTypeWrapper> sampleTypes = new HashSet<SampleTypeWrapper>();
        for (ContainerTypeWrapper type : types) {
            sampleTypes.addAll(type.getSampleTypesRecursively());
        }
        return new ArrayList<SampleTypeWrapper>(sampleTypes);
    }

    @Override
    public boolean checkIntegrity() {
        return true;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
    }

    public static List<SampleTypeWrapper> getGlobalSampleTypes(
        WritableApplicationService appService, boolean sort)
        throws ApplicationException {
        HQLCriteria c = new HQLCriteria("from " + SampleType.class.getName()
            + " where site = null");

        List<SampleType> sampleTypes = appService.query(c);
        List<SampleTypeWrapper> list = new ArrayList<SampleTypeWrapper>();
        for (SampleType type : sampleTypes) {
            list.add(new SampleTypeWrapper(appService, type));
        }
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
    public static void persistGlobalSampleTypes(
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
        if (wrapper instanceof SampleTypeWrapper) {
            String name1 = wrappedObject.getName();
            String name2 = wrapper.wrappedObject.getName();
            if (name1 != null && name2 != null) {
                return name1.compareTo(name2);
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return getName();
    }
}
