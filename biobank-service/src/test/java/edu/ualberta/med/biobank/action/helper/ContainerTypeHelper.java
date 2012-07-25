package edu.ualberta.med.biobank.action.helper;

import java.util.HashSet;

import edu.ualberta.med.biobank.action.containerType.ContainerTypeGetInfoAction.ContainerTypeInfo;
import edu.ualberta.med.biobank.action.containerType.ContainerTypeSaveAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
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
        ctSaveAction.setActivityStatus(ActivityStatus.ACTIVE);
        ctSaveAction.setDefaultTemperature(defaultTemp);
        ctSaveAction.setChildLabelingSchemeId(childLabelingSchemeId);

        return ctSaveAction;
    }

    public static ContainerTypeSaveAction getSaveAction(
        ContainerTypeInfo containerTypeInfo) {
        ContainerTypeSaveAction containerTypeSaveAction =
            new ContainerTypeSaveAction();
        containerTypeSaveAction
            .setId(containerTypeInfo.getContainerType().getId());
        containerTypeSaveAction.setName(containerTypeInfo.getContainerType()
            .getName());
        containerTypeSaveAction.setNameShort(containerTypeInfo
            .getContainerType()
            .getNameShort());
        containerTypeSaveAction.setSiteId(containerTypeInfo.getContainerType()
            .getSite().getId());
        containerTypeSaveAction.setTopLevel(containerTypeInfo
            .getContainerType()
            .getTopLevel());

        containerTypeSaveAction.setRowCapacity(containerTypeInfo
            .getContainerType()
            .getCapacity().getRowCapacity());
        containerTypeSaveAction.setColCapacity(containerTypeInfo
            .getContainerType()
            .getCapacity().getColCapacity());

        containerTypeSaveAction.setDefaultTemperature(
            containerTypeInfo.getContainerType().getDefaultTemperature());
        containerTypeSaveAction.setChildLabelingSchemeId(
            containerTypeInfo.getContainerType().getChildLabelingScheme()
                .getId());

        containerTypeSaveAction.setActivityStatus(
            containerTypeInfo.getContainerType().getActivityStatus());

        HashSet<Integer> ids = new HashSet<Integer>();
        for (SpecimenType specimenType : containerTypeInfo.getContainerType()
            .getSpecimenTypes()) {
            ids.add(specimenType.getId());
        }
        containerTypeSaveAction.setSpecimenTypeIds(ids);

        ids = new HashSet<Integer>();
        for (ContainerType childContainerType : containerTypeInfo
            .getContainerType()
            .getChildContainerTypes()) {
            ids.add(childContainerType.getId());
        }
        containerTypeSaveAction.setChildContainerTypeIds(ids);

        return containerTypeSaveAction;
    }
}
