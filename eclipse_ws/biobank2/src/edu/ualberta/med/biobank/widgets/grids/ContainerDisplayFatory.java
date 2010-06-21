package edu.ualberta.med.biobank.widgets.grids;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;

/**
 * Create widget to display container. Use the correct Widget class depending on
 * the container type
 */
public class ContainerDisplayFatory {

    public static AbstractContainerDisplayWidget createWidget(Composite parent,
        ContainerWrapper container) {
        ContainerTypeWrapper containerType = null;
        if (container != null) {
            containerType = container.getContainerType();
        }
        return createWidget(parent, container, containerType);
    }

    public static AbstractContainerDisplayWidget createWidget(Composite parent,
        ContainerTypeWrapper containerType) {
        return createWidget(parent, null, containerType);
    }

    private static AbstractContainerDisplayWidget createWidget(
        Composite parent, ContainerWrapper container,
        ContainerTypeWrapper containerType) {
        AbstractContainerDisplayWidget widget;
        if (containerType == null) {
            widget = new GridContainerWidget(parent);
            ((GridContainerWidget) widget).setStorageSize(3, 5);
        } else if (containerType.getName().startsWith("Drawer")) {
            widget = new DrawerWidget(parent);
        } else {
            widget = new GridContainerWidget(parent);
        }
        if (container != null) {
            widget.setContainer(container);
        } else if (containerType != null) {
            widget.setContainerType(containerType);
        }
        return widget;

    }
}
