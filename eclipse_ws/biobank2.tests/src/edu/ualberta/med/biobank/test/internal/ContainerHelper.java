package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.util.RowColPos;

@Deprecated
public class ContainerHelper extends DbHelper {

    /**
     * Creates a new container object. It is not saved to the database.
     * 
     * @param label If the container is a top level container provide a label,
     *            otherwise this parameter should be null.
     * @param barcode The product barcode for this container.
     * @param site The site this container belongs to.
     * @param type The container type for this container.
     * @return The container wrapper for the container.
     * @throws Exception
     */
    public static ContainerWrapper newContainer(String label, String barcode,
        SiteWrapper site, ContainerTypeWrapper type) throws Exception {
        ContainerWrapper container;

        container = new ContainerWrapper(appService);
        container.setContainerType(type);
        if (label != null) {
            container.setLabel(label);
        }
        container.setProductBarcode(barcode);
        container.setSite(site);
        container.setActivityStatus(ActivityStatus.ACTIVE);
        return container;
    }

    /**
     * Creates a new container object. It is not saved to the database.
     * 
     * @param label If the container is a top level container provide a label,
     *            otherwise this parameter should be null.
     * @param barcode The product barcode for this container.
     * @param site The site this container belongs to.
     * @param type The container type for this container.
     * @param row If the container is a child container then this is the row
     *            where this container is located in the parent container.
     * @param col If the container is a child container then this is the column
     *            where this container is located in the parent container.
     * @return The container wrapper for the container.
     * @throws Exception Exception is thrown if a label is specified and the
     *             container is meant to be a child container.
     */
    public static ContainerWrapper newContainer(String label, String barcode,
        ContainerWrapper parent, SiteWrapper site, ContainerTypeWrapper type,
        Integer row, Integer col) throws Exception {
        ContainerWrapper container = newContainer(label, barcode, site, type);
        container.setParent(parent, new RowColPos(row, col));
        return container;
    }

    /**
     * Adds a container to the database.
     * 
     * @param label If the container is a top level container provide a label,
     *            otherwise this parameter should be null.
     * @param barcode The product barcode for this container.
     * @param site The site this container belongs to.
     * @param type The container type for this container.
     * 
     * @return The container wrapper for the container.
     * @throws Exception Exception is thrown if a label is specified and the
     *             container is meant to be a child container. An exception is
     *             thrown if the container could not be added to the database.
     */
    public static ContainerWrapper addContainer(String label, String barcode,
        SiteWrapper site, ContainerTypeWrapper type) throws Exception {
        ContainerWrapper container = newContainer(label, barcode, site, type);
        container.persist();
        return container;
    }

    /**
     * Adds a container to the database.
     * 
     * @param label If the container is a top level container provide a label,
     *            otherwise this parameter should be null.
     * @param barcode The product barcode for this container.
     * @param parent The containers parent container.
     * @param site The site this container belongs to.
     * @param type The container type for this container.
     * @param row If the container is a child container then this is the row
     *            where this container is located in the parent container.
     * @param col If the container is a child container then this is the column
     *            where this container is located in the parent container.
     * @return The container wrapper for the container.
     * @throws Exception Exception is thrown if a label is specified and the
     *             container is meant to be a child container. Exception An
     *             exception is thrown if the container could not be added to
     *             the database.
     */
    public static ContainerWrapper addContainer(String label, String barcode,
        ContainerWrapper parent, SiteWrapper site, ContainerTypeWrapper type,
        Integer row, Integer col) throws Exception {
        ContainerWrapper container = newContainer(label, barcode, parent, site,
            type, row, col);
        container.persist();
        return container;
    }

    public static ContainerWrapper addContainerRandom(SiteWrapper site,
        String name, ContainerWrapper parent) throws Exception {
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerTypeRandom(
            site, name, parent == null);
        String label = null;
        if ((type.getTopLevel() != null) && type.getTopLevel()) {
            label = String.valueOf(r.nextInt());
        }
        ContainerWrapper container = addContainer(label, name, site, type);
        if (label == null) {
            container.setParent(parent, new RowColPos(0, 0));
        }
        container.persist();
        return container;
    }

    public static ContainerWrapper addTopContainerRandom(SiteWrapper site,
        String name, int typeCapacityRow, int typeCapacityCol) throws Exception {
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerType(site,
            name, name, 1, typeCapacityRow, typeCapacityCol, true);
        ContainerWrapper container = addContainer(name, name, site, type);
        return container;
    }

    public static int addTopContainersWithChildren(SiteWrapper site,
        String barcode, int count) throws Exception {
        for (int i = 0; i < count; i++) {
            ContainerWrapper topContainer = addTopContainerRandom(site, barcode
                + "TOP" + (i + 1), 3, 6);
            ContainerTypeWrapper type = ContainerTypeHelper
                .addContainerTypeRandom(site, barcode + "children" + (i + 1),
                    false);
            topContainer.getContainerType().addToChildContainerTypeCollection(
                Arrays.asList(type));
            topContainer.getContainerType().persist();
            int maxRow = topContainer.getRowCapacity();
            for (int j = 0; j < 5; j++) {
                ContainerWrapper child = newContainer(null, barcode + "child"
                    + (i + 1) + "_" + j, site, type);
                int posRow = j % maxRow;
                int posCol = j / maxRow;
                topContainer.addChild(posRow, posCol, child);
            }
            topContainer.persist();
        }
        site.reload();
        return count + (count * 5);
    }
}
