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
        return createWidget(parent, container, containerType, false);
    }

    public static AbstractContainerDisplayWidget createWidget(Composite parent,
        ContainerTypeWrapper containerType, boolean createDefaultContainer) {
        return createWidget(parent, null, containerType, createDefaultContainer);
    }

    private static AbstractContainerDisplayWidget createWidget(
        Composite parent, ContainerWrapper container,
        ContainerTypeWrapper containerType, boolean createDefaultContainer) {
        AbstractContainerDisplayWidget widget = null;
        if (containerType == null) {
            if (createDefaultContainer) {
                widget = new GridContainerWidget(parent);
                ((GridContainerWidget) widget).setStorageSize(3, 5);
            }
        } else if (containerType.getName().equals("Drawer 36")) {
            widget = new Drawer36Widget(parent);
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
