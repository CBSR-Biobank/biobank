package edu.ualberta.med.biobank.common.wrappers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenTypePeer;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenTypeBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.checks.Check;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SpecimenTypeWrapper extends SpecimenTypeBaseWrapper {
    private static final String HAS_SPECIMENS_MSG = "Unable to delete specimen type {0}. Specimens of this type exists in storage. Remove all instances before deleting this type.";

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

    public static final String HAS_SPECIMENS_HQL = "select count(s) from "
        + Specimen.class.getName() + " as s where s."
        + SpecimenPeer.SPECIMEN_TYPE.getName() + "=?)";

    public boolean isUsedBySpecimens() throws ApplicationException,
        BiobankQueryResultSizeException {
        if (isNew())
            return false;
        HQLCriteria c = new HQLCriteria(HAS_SPECIMENS_HQL,
            Arrays.asList(new Object[] { wrappedObject }));
        return getCountResult(appService, c) > 0;
    }

    @Override
    protected TaskList getPersistTasks() {
        TaskList tasks = new TaskList();

        tasks.add(Check.unique(this, SpecimenTypePeer.NAME));
        tasks.add(Check.unique(this, SpecimenTypePeer.NAME_SHORT));

        tasks.add(Check.notNull(this, SpecimenTypePeer.NAME));
        tasks.add(Check.notNull(this, SpecimenTypePeer.NAME_SHORT));

        tasks.add(super.getPersistTasks());

        return tasks;
    }

    @Override
    protected TaskList getDeleteTasks() {
        TaskList tasks = new TaskList();

        String isUsedMsg = MessageFormat.format(HAS_SPECIMENS_MSG, getName());
        tasks.add(Check.notUsed(this, SpecimenPeer.SPECIMEN_TYPE,
            Specimen.class, isUsedMsg));

        tasks.add(super.getDeleteTasks());

        return tasks;
    }

    // TODO: remove this override when all persist()-s are like this!
    @Override
    public void persist() throws Exception {
        getPersistTasks().execute(appService);
    }

    @Override
    public void delete() throws Exception {
        getDeleteTasks().execute(appService);
    }
}
