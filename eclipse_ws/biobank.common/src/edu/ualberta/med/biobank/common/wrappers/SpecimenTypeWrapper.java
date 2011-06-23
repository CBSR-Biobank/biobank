package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankDeleteException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenTypePeer;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenTypeBaseWrapper;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SpecimenTypeWrapper extends SpecimenTypeBaseWrapper {

    public SpecimenTypeWrapper(WritableApplicationService appService,
        SpecimenType wrappedObject) {
        super(appService, wrappedObject);
    }

    public SpecimenTypeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    /**
     * get all sample types in a site for containers which type name contains
     * "typeNameContains" (go recursively inside found containers)
     */
    public static List<SpecimenTypeWrapper> getSpecimenTypeForContainerTypes(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        String typeNameContains) throws ApplicationException {
        List<ContainerTypeWrapper> containerTypes = ContainerTypeWrapper
            .getContainerTypesInSite(appService, siteWrapper, typeNameContains,
                false);
        Set<SpecimenTypeWrapper> SpecimenTypes = new HashSet<SpecimenTypeWrapper>();
        for (ContainerTypeWrapper containerType : containerTypes) {
            SpecimenTypes.addAll(containerType.getSpecimenTypesRecursively());
        }
        return new ArrayList<SpecimenTypeWrapper>(SpecimenTypes);
    }

    /**
     * get all sample types in a site for pallet containers (8*12 size) (go
     * recursively inside found containers)
     */
    public static List<SpecimenTypeWrapper> getSpecimenTypeForPallet96(
        WritableApplicationService appService, SiteWrapper siteWrapper)
        throws ApplicationException {
        List<ContainerTypeWrapper> containerTypes = ContainerTypeWrapper
            .getContainerTypesPallet96(appService, siteWrapper);
        Set<SpecimenTypeWrapper> SpecimenTypes = new HashSet<SpecimenTypeWrapper>();
        for (ContainerTypeWrapper containerType : containerTypes) {
            SpecimenTypes.addAll(containerType.getSpecimenTypesRecursively());
        }
        return new ArrayList<SpecimenTypeWrapper>(SpecimenTypes);
    }

    @Override
    public boolean checkIntegrity() {
        return true;
    }

    @Override
    protected void deleteChecks() throws BiobankException, ApplicationException {
        if (isUsedBySpecimens()) {
            throw new BiobankDeleteException("Unable to delete specimen type "
                + getName() + ". Specimens of this type exists in storage."
                + " Remove all instances before deleting this type.");
        }
    }

    public static final String ALL_SAMPLE_TYPES_QRY = "from "
        + SpecimenType.class.getName();

    public static List<SpecimenTypeWrapper> getAllSpecimenTypes(
        WritableApplicationService appService, boolean sort)
        throws ApplicationException {
        HQLCriteria c = new HQLCriteria(ALL_SAMPLE_TYPES_QRY);

        List<SpecimenType> SpecimenTypes = appService.query(c);
        List<SpecimenTypeWrapper> list = new ArrayList<SpecimenTypeWrapper>();
        for (SpecimenType type : SpecimenTypes) {
            list.add(new SpecimenTypeWrapper(appService, type));
        }
        if (sort)
            Collections.sort(list);
        return list;
    }

    public static final String ALL_SOURCE_ONLY_SPECIMEN_TYPES_QRY = "from "
        + SpecimenType.class.getName() + " where "
        + SpecimenTypePeer.PARENT_SPECIMEN_TYPE_COLLECTION.getName()
        + ".size = 0";

    public static List<SpecimenTypeWrapper> getAllSourceOnlySpecimenTypes(
        WritableApplicationService appService, boolean sort)
        throws ApplicationException {
        HQLCriteria c = new HQLCriteria(ALL_SOURCE_ONLY_SPECIMEN_TYPES_QRY);

        List<SpecimenType> SpecimenTypes = appService.query(c);
        List<SpecimenTypeWrapper> list = new ArrayList<SpecimenTypeWrapper>();
        for (SpecimenType type : SpecimenTypes) {
            list.add(new SpecimenTypeWrapper(appService, type));
        }
        if (sort)
            Collections.sort(list);
        return list;
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        checkNameAndShortNameUnique();
    }

    /**
     * This method should only be called to save the new sample type list.
     */
    public static void persistSpecimenTypes(
        List<SpecimenTypeWrapper> addedOrModifiedTypes,
        List<SpecimenTypeWrapper> typesToDelete) throws BiobankCheckException,
        Exception {
        if (addedOrModifiedTypes != null) {
            for (SpecimenTypeWrapper ss : addedOrModifiedTypes) {
                ss.persist();
            }
        }
        if (typesToDelete != null) {
            for (SpecimenTypeWrapper ss : typesToDelete) {
                ss.delete();
            }
        }
    }

    @Override
    public int compareTo(ModelWrapper<SpecimenType> wrapper) {
        if (wrapper instanceof SpecimenTypeWrapper) {
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

    public static final String IS_USED_BY_SPECIMENS_QRY = "select count(s) from "
        + Specimen.class.getName()
        + " as s where s."
        + SpecimenPeer.SPECIMEN_TYPE.getName() + "=?)";

    public boolean isUsedBySpecimens() throws ApplicationException,
        BiobankQueryResultSizeException {
        HQLCriteria c = new HQLCriteria(IS_USED_BY_SPECIMENS_QRY,
            Arrays.asList(new Object[] { wrappedObject }));
        return getCountResult(appService, c) > 0;
    }

    @Override
    public void reload() throws Exception {
        super.reload();
    }

    public void checkNameAndShortNameUnique() throws ApplicationException,
        BiobankException {
        checkNoDuplicates(SpecimenType.class, SpecimenTypePeer.NAME.getName(),
            getName(), "A specimen type with name");
        checkNoDuplicates(SpecimenType.class,
            SpecimenTypePeer.NAME_SHORT.getName(), getNameShort(),
            "A specimen type with name short");
    }

}
