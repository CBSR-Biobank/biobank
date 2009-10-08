package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.SampleType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

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
        return new String[] { "name", "nameShort" };
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

    public static void deleteOldSampleTypes(List<SampleTypeWrapper> newTypes,
        List<SampleTypeWrapper> oldTypes) throws BiobankCheckException,
        Exception {
        Set<SampleTypeWrapper> setNewTypes = new HashSet<SampleTypeWrapper>(
            newTypes);
        Iterator<SampleTypeWrapper> it = oldTypes.iterator();
        while (it.hasNext()) {
            if (!setNewTypes.contains(it.next().getId())) {
                it.next().delete();
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
