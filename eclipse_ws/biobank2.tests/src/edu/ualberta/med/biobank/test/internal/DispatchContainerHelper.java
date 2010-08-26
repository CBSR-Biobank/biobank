package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class DispatchContainerHelper extends DbHelper {

    public static DispatchContainerWrapper newContainer(String barcode,
        ContainerTypeWrapper type) throws Exception {
        DispatchContainerWrapper container;

        container = new DispatchContainerWrapper(appService);
        container.setProductBarcode(barcode);
        container.setContainerType(type);
        container.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, "Active"));
        return container;
    }

    public static DispatchContainerWrapper newContainerRandom(SiteWrapper site,
        String name) throws Exception {
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerTypeRandom(
            site, name, false);
        return newContainer(name, type);

    }

    public static DispatchContainerWrapper addContainer(String barcode,
        ContainerTypeWrapper type) throws Exception {
        DispatchContainerWrapper container = newContainer(barcode, type);
        container.persist();
        return container;
    }

    public static DispatchContainerWrapper addContainerRandom(SiteWrapper site,
        String name) throws Exception {
        DispatchContainerWrapper container = newContainerRandom(site, name);
        container.persist();
        return container;
    }

}
