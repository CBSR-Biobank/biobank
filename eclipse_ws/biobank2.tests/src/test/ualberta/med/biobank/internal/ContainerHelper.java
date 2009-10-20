package test.ualberta.med.biobank.internal;

import java.util.Iterator;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class ContainerHelper extends DbHelper {

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

    public static ContainerWrapper newContainer(String label, String barcode,
        ContainerWrapper parent, SiteWrapper site, ContainerTypeWrapper type,
        Integer row, Integer col) throws Exception {
        ContainerWrapper container = newContainer(label, barcode, parent, site,
            type);
        container.setPosition(row, col);
        return container;
    }

    public static ContainerWrapper addContainer(String label, String barcode,
        ContainerWrapper parent, SiteWrapper site, ContainerTypeWrapper type)
        throws Exception {
        ContainerWrapper container = newContainer(label, barcode, parent, site,
            type);
        container.persist();
        return container;
    }

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
        ContainerWrapper container = addContainer(label, name, null, site,
            type);
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

    // recursive method to delete child containers
    public static void deleteContainers(List<ContainerWrapper> containerList)
        throws BiobankCheckException, Exception {
        if ((containerList == null) || (containerList.size() == 0))
            return;

        Iterator<ContainerWrapper> it = containerList.iterator();
        while (it.hasNext()) {
            ContainerWrapper container = it.next();
            if (container.getChildren().size() > 0) {
                deleteContainers(container.getChildren());
            }
            container.reload();
            container.delete();
        }
    }

}
