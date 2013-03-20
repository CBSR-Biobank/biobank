package edu.ualberta.med.biobank.test.action.helper;

import java.util.HashSet;

import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction.ContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeSaveAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.type.LabelingLayout;

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
        ctSaveAction.setLabelingLayout(LabelingLayout.VERTICAL);

        return ctSaveAction;
    }

    public static ContainerTypeSaveAction getSaveAction(ContainerTypeInfo ctypeInfo) {
        ContainerTypeSaveAction containerTypeSaveAction = new ContainerTypeSaveAction();
        containerTypeSaveAction.setId(ctypeInfo.getContainerType().getId());
        containerTypeSaveAction.setName(ctypeInfo.getContainerType().getName());
        containerTypeSaveAction.setNameShort(ctypeInfo.getContainerType().getNameShort());
        containerTypeSaveAction.setSiteId(ctypeInfo.getContainerType().getSite().getId());
        containerTypeSaveAction.setTopLevel(ctypeInfo.getContainerType().getTopLevel());

        containerTypeSaveAction.setRowCapacity(
            ctypeInfo.getContainerType().getCapacity().getRowCapacity());
        containerTypeSaveAction.setColCapacity(
            ctypeInfo.getContainerType().getCapacity().getColCapacity());

        containerTypeSaveAction.setDefaultTemperature(
            ctypeInfo.getContainerType().getDefaultTemperature());
        containerTypeSaveAction.setChildLabelingSchemeId(
            ctypeInfo.getContainerType().getChildLabelingScheme().getId());
        containerTypeSaveAction.setLabelingLayout(
            ctypeInfo.getContainerType().getLabelingLayout());

        containerTypeSaveAction.setActivityStatus(
            ctypeInfo.getContainerType().getActivityStatus());

        HashSet<Integer> ids = new HashSet<Integer>();
        for (SpecimenType specimenType : ctypeInfo.getContainerType().getSpecimenTypes()) {
            ids.add(specimenType.getId());
        }
        containerTypeSaveAction.setSpecimenTypeIds(ids);

        ids = new HashSet<Integer>();
        for (ContainerType childContainerType : ctypeInfo.getContainerType().getChildContainerTypes()) {
            ids.add(childContainerType.getId());
        }
        containerTypeSaveAction.setChildContainerTypeIds(ids);

        return containerTypeSaveAction;
    }
}
