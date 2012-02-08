package edu.ualberta.med.biobank.test.action.helper;

import java.util.HashSet;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction.ContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeSaveAction;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SpecimenType;

public class ContainerTypeHelper {

    public static ContainerTypeSaveAction getSaveAction(String name,
        String nameShort, Integer siteId, boolean isTopLevel,
        Integer rowCapacity, Integer colCapacity,
        Integer childLabelingSchemeId, Double defaultTemp) {

        ContainerTypeSaveAction ctSaveAction = new ContainerTypeSaveAction();
        ctSaveAction.setName(name);
        ctSaveAction.setNameShort(nameShort);
        ctSaveAction.setSiteId(siteId);
        ctSaveAction.setTopLevel(isTopLevel);
        ctSaveAction.setRowCapacity(rowCapacity);
        ctSaveAction.setColCapacity(colCapacity);
        ctSaveAction.setActivityStatus(ActivityStatusEnum.ACTIVE.getId());
        ctSaveAction.setDefaultTemperature(defaultTemp);
        ctSaveAction.setChildLabelingSchemeId(childLabelingSchemeId);

        return ctSaveAction;
    }

    public static ContainerTypeSaveAction getSaveAction(
        ContainerTypeInfo containerTypeInfo) {
        ContainerTypeSaveAction containerTypeSaveAction =
            new ContainerTypeSaveAction();
        containerTypeSaveAction
            .setId(containerTypeInfo.containerType.getId());
        containerTypeSaveAction.setName(containerTypeInfo.containerType
            .getName());
        containerTypeSaveAction.setNameShort(containerTypeInfo.containerType
            .getNameShort());
        containerTypeSaveAction.setSiteId(containerTypeInfo.containerType
            .getSite().getId());
        containerTypeSaveAction.setTopLevel(containerTypeInfo.containerType
            .getTopLevel());

        containerTypeSaveAction.setRowCapacity(containerTypeInfo.containerType
            .getCapacity().getRowCapacity());
        containerTypeSaveAction.setColCapacity(containerTypeInfo.containerType
            .getCapacity().getColCapacity());

        containerTypeSaveAction.setDefaultTemperature(
            containerTypeInfo.containerType.getDefaultTemperature());
        containerTypeSaveAction.setChildLabelingSchemeId(
            containerTypeInfo.containerType.getChildLabelingScheme().getId());

        containerTypeSaveAction.setActivityStatus(
            containerTypeInfo.containerType.getActivityStatus().getId());

        HashSet<Integer> ids = new HashSet<Integer>();
        for (SpecimenType specimenType : containerTypeInfo.containerType
            .getSpecimenTypeCollection()) {
            ids.add(specimenType.getId());
        }
        containerTypeSaveAction.setSpecimenTypeIds(ids);

        ids = new HashSet<Integer>();
        for (ContainerType childContainerType : containerTypeInfo.containerType
            .getChildContainerTypeCollection()) {
            ids.add(childContainerType.getId());
        }
        containerTypeSaveAction.setChildContainerTypeIds(ids);

        return containerTypeSaveAction;
    }
}
