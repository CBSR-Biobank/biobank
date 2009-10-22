package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;

public class ContainerHelper extends DbHelper {

    /**
     * Creates a new container object. It is not saved to the database.
     * 
     * @param label If the container is a top level container provide a label,
     *            otherwise this parameter should be null.
     * @param barcode The product barcode for this container.
     * @param parent The containers parent container.
     * @param site The site this container belongs to.
     * @param type The container type for this container.
     * @return The container wrapper for the container.
     * @throws Exception
     */
    public static ContainerWrapper newContainer(String label, String barcode,
        ContainerWrapper parent, SiteWrapper site, ContainerTypeWrapper type)
        throws Exception {
        ContainerWrapper container;

        container = new ContainerWrapper(appService);
        if (label != null) {
            if (type.getTopLevel()) {
                container.setLabel(label);
            } else {
                throw new Exception(
                    "cannot set label on non top level containers");
            }
        }
        container.setProductBarcode(barcode);
        if (parent != null) {
            container.setParent(parent);
        }
        if (site != null) {
            container.setSite(site);
        }
        container.setContainerType(type);
        return container;
    }

    /**
     * Creates a new container object. It is not saved to the database.
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
     *             container is meant to be a child container.
     */
    public static ContainerWrapper newContainer(String label, String barcode,
        ContainerWrapper parent, SiteWrapper site, ContainerTypeWrapper type,
        Integer row, Integer col) throws Exception {
        ContainerWrapper container = newContainer(label, barcode, parent, site,
            type);
        container.setPosition(row, col);
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
     * @return The container wrapper for the container.
     * @throws Exception Exception is thrown if a label is specified and the
     *             container is meant to be a child container. An exception is
     *             thrown if the container could not be added to the database.
     */
    public static ContainerWrapper addContainer(String label, String barcode,
        ContainerWrapper parent, SiteWrapper site, ContainerTypeWrapper type)
        throws Exception {
        ContainerWrapper container = newContainer(label, barcode, parent, site,
            type);
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
        String name) throws Exception {
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerTypeRandom(
            site, name);
        String label = null;
        if ((type.getTopLevel() != null) && type.getTopLevel()) {
            label = String.valueOf(r.nextInt());
        }
        ContainerWrapper container = addContainer(label, name, null, site, type);
        if (label == null) {
            container.setPosition(0, 0);
        }
        container.persist();
        return container;
    }

    public static void addContainersRandom(SiteWrapper site, String barcode,
        int count) throws Exception {
        for (int i = 0; i < count; i++) {
            addContainerRandom(site, barcode + (i + 1));
        }
        site.reload();
    }

    public static ContainerLabelingSchemeWrapper newContainerLabelingScheme() {
        ContainerLabelingSchemeWrapper clsw = new ContainerLabelingSchemeWrapper(
            appService, new ContainerLabelingScheme());
        clsw.setName("SchemeName");
        return clsw;
    }
}
