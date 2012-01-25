package edu.ualberta.med.biobank.test.action.helper;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction.ContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeSaveAction;

public class ContainerTypeHelper {

    public static ContainerTypeSaveAction getSaveAction(String name,
        String nameShort, Integer siteId, boolean isTopLevel,
        Integer rowCapacity, Integer colCapacity, Integer childLabelingSchemeId) {

        ContainerTypeSaveAction ctSaveAction = new ContainerTypeSaveAction();
        ctSaveAction.setName(name);
        ctSaveAction.setNameShort(nameShort);
        ctSaveAction.setSiteId(siteId);
        ctSaveAction.setTopLevel(isTopLevel);
        ctSaveAction.setRowCapacity(rowCapacity);
        ctSaveAction.setColCapacity(colCapacity);
        ctSaveAction.setActivityStatusId(ActivityStatusEnum.ACTIVE.getId());
        ctSaveAction.setChildLabelingSchemeId(childLabelingSchemeId);

        return ctSaveAction;
    }

    public static ContainerTypeSaveAction getSaveAction(
        ContainerTypeInfo containerTypeInfo) {
        ContainerTypeSaveAction containerTypeSaveAction =
            new ContainerTypeSaveAction();
        containerTypeSaveAction
            .setId(containerTypeInfo.containerType.getId());
        // TODO: requires implementation
        return containerTypeSaveAction;
    }
}
