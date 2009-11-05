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
        if (containerType != null) {
            if (containerType.getName().startsWith("Drawer")) {
                DrawerWidget drawer = new DrawerWidget(parent);
                return drawer;
            } else {
                GridContainerWidget containerWidget = new GridContainerWidget(
                    parent);
                if (container == null) {
                    containerWidget.setContainerType(containerType);
                } else {
                    containerWidget.setContainer(container);
                }
                return containerWidget;
            }
        }
        GridContainerWidget containerWidget = new GridContainerWidget(parent);
        containerWidget.setStorageSize(3, 5);
        return containerWidget;
    }

}
