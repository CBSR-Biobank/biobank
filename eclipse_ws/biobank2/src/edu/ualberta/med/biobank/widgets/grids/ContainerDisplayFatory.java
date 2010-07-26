package edu.ualberta.med.biobank.widgets.grids;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;

/**
 * Create widget to display container. Use the correct Widget class depending on
 * the container type
 */
public class ContainerDisplayFatory {
    @Deprecated
    public static AbstractContainerDisplay createWidget(Composite parent,
        ContainerWrapper container) {
        ContainerTypeWrapper containerType = null;
        if (container != null) {
            containerType = container.getContainerType();
        }
        return createWidget(parent, container, containerType, false);
    }

    @Deprecated
    public static AbstractContainerDisplay createWidget(Composite parent,
        ContainerTypeWrapper containerType, boolean createDefaultContainer) {
        return createWidget(parent, null, containerType, createDefaultContainer);
    }

    @Deprecated
    private static AbstractContainerDisplay createWidget(Composite parent,
        ContainerWrapper container, ContainerTypeWrapper containerType,
        boolean createDefaultContainer) {
        AbstractContainerDisplay widget = null;
        // if (containerType == null) {
        // if (createDefaultContainer) {
        // widget = new GridContainerWidget(parent);
        // ((GridContainerWidget) widget).setStorageSize(3, 5);
        // }
        // } else if (containerType.getName().equals("Drawer 36")) {
        // widget = new Drawer36Widget(parent);
        // } else {
        // widget = new GridContainerWidget(parent);
        // }
        // if (container != null) {
        // widget.setContainer(container);
        // } else if (containerType != null) {
        // widget.setContainerType(containerType);
        // }
        return widget;

    }
}
