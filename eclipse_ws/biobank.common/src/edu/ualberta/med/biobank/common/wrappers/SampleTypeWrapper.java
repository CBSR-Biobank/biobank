package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SampleType;
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
        return new String[] { "name", "nameShort", "containerTypeCollection" };
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

    /**
     * Get the list of container type that support this sample type. Use
     * ContainerType.setSampleTypeCollection to link objects together
     */
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
        checkNotEmpty(getName(), "Name");
        checkNotEmpty(getNameShort(), "Short Name");
        checkNameAndShortNameUnique();
    }

    public void checkNameAndShortNameUnique() throws ApplicationException,
        BiobankCheckException {
        checkNoDuplicates("name", getName(), "A sample type with name \""
            + getName() + "\" already exists");

        checkNoDuplicates("nameShort", getNameShort(),
            "A sample type with short name \"" + getNameShort()
                + "\" already exists");
    }

    private void checkNoDuplicates(String propertyName, String value,
        String errorMessage) throws ApplicationException, BiobankCheckException {
        List<Object> parameters = new ArrayList<Object>(
            Arrays.asList(new Object[] { value }));

        String notSameObject = "";
        if (!isNew()) {
            notSameObject = " and id <> ?";
            parameters.add(getId());
        }
        HQLCriteria criteria = new HQLCriteria("select count(*) from "
            + SampleType.class.getName() + " where " + propertyName + "=? "
            + notSameObject, parameters);
        List<Long> result = appService.query(criteria);
        if (result.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        if (result.get(0) > 0) {
            throw new BiobankCheckException(errorMessage);
        }
    }

    /**
     * get all sample types in a site for containers which type name contains
     * "typeNameContains" (go recursively inside found containers)
     */
    public static List<SampleTypeWrapper> getSampleTypeForContainerTypes(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        String typeNameContains) throws ApplicationException {
        List<ContainerTypeWrapper> containerTypes = ContainerTypeWrapper
            .getContainerTypesInSite(appService, siteWrapper, typeNameContains,
                false);
        Set<SampleTypeWrapper> sampleTypes = new HashSet<SampleTypeWrapper>();
        for (ContainerTypeWrapper containerType : containerTypes) {
            sampleTypes.addAll(containerType.getSampleTypesRecursively());
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
        if (isUsedBySamples()) {
            throw new BiobankCheckException("Unable to delete sample type "
                + getName() + ". Aliquots of this type exists in storage."
                + " Remove all instances before deleting this type.");
        }
    }

    public static List<SampleTypeWrapper> getAllSampleTypes(
        WritableApplicationService appService, boolean sort)
        throws ApplicationException {
        HQLCriteria c = new HQLCriteria("from " + SampleType.class.getName());

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
     * This method should only be called to save the new sample type list.
     */
    public static void persistGlobalSampleTypes(
        List<SampleTypeWrapper> addedOrModifiedTypes,
        List<SampleTypeWrapper> typesToDelete) throws BiobankCheckException,
        Exception {
        if (addedOrModifiedTypes != null) {
            for (SampleTypeWrapper ss : addedOrModifiedTypes) {
                ss.persist();
            }
        }
        if (typesToDelete != null) {
            for (SampleTypeWrapper ss : typesToDelete) {
                ss.delete();
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

    public boolean isUsedBySamples() throws ApplicationException,
        BiobankCheckException {
        String queryString = "select count(s) from " + Aliquot.class.getName()
            + " as s where s.sampleType=?)";
        HQLCriteria c = new HQLCriteria(queryString,
            Arrays.asList(new Object[] { wrappedObject }));
        List<Long> results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return results.get(0) > 0;
    }

    @Override
    public void reload() throws Exception {
        super.reload();
    }

}
