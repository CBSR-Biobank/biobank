package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class ContainerTypeHelper extends DbHelper {

    /**
     * Adds a new container type to the database.
     * 
     * @param site The site the container type belongs to.
     * @param name The container type's name.
     * @param nameShort The container type's short name.
     * @param labelingScheme The container type's labeling scheme.
     * @param rowCapacity The maximum number of rows for the container type.
     * @param colCapacity The maximum number of columns for the container type.
     * @param isTopLevel Whether this container type is for a container that is
     *            not contained by other container types.
     * @return
     * @throws BiobankCheckException
     * @throws Exception
     */
    public static ContainerTypeWrapper newContainerType(SiteWrapper site,
        String name, String nameShort, Integer labelingScheme,
        Integer rowCapacity, Integer colCapacity, boolean isTopLevel) {
        ContainerTypeWrapper ct = new ContainerTypeWrapper(appService);
        ct.setSite(site);
        ct.setName(name);
        ct.setNameShort(nameShort);
        ct.setChildLabelingScheme(labelingScheme);
        if (rowCapacity != null)
            ct.setRowCapacity(rowCapacity);
        if (colCapacity != null)
            ct.setColCapacity(colCapacity);
        ct.setTopLevel(isTopLevel);
        return ct;
    }

    /**
     * Adds a new container type to the database.
     * 
     * @param site The site the container type belongs to.
     * @param name The container type's name.
     * @param nameShort The container type's short name.
     * @param labelingScheme The container type's labeling scheme.
     * @param rowCapacity The maximum number of rows for the container type.
     * @param colCapacity The maximum number of columns for the container type.
     * @param isTopLevel Whether this container type is for a container that is
     *            not contained by other container types.
     * @return
     * @throws BiobankCheckException
     * @throws Exception
     */
    public static ContainerTypeWrapper addContainerType(SiteWrapper site,
        String name, String nameShort, Integer labelingScheme,
        Integer rowCapacity, Integer colCapacity, boolean isTopLevel)
        throws BiobankCheckException, Exception {
        ContainerTypeWrapper container = newContainerType(site, name,
            nameShort, labelingScheme, rowCapacity, colCapacity, isTopLevel);
        container.persist();
        return container;
    }

    /**
     * 
     * @param site The site the container type belongs to.
     * @param name The container type's name.
     * @param topContainer Whether this container type is for a container that
     *            is not contained by other container types.
     * @return
     * @throws Exception
     */
    public static ContainerTypeWrapper addContainerTypeRandom(SiteWrapper site,
        String name, boolean topContainer) throws Exception {
        return addContainerType(site, name, "", 1, r.nextInt(10) + 1, r
            .nextInt(10) + 1, topContainer);
    }

    public static ContainerTypeWrapper addContainerTypeRandom(SiteWrapper site,
        String name) throws Exception {
        return addContainerTypeRandom(site, name, r.nextBoolean());
    }

    public static void addContainerTypesRandom(SiteWrapper site, String name,
        int count) throws Exception {
        for (int i = 0; i < count; i++) {
            addContainerTypeRandom(site, name + (i + 1));
        }
        site.reload();
    }

}
