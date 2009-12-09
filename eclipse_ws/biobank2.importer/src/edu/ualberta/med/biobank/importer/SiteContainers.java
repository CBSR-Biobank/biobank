
package edu.ualberta.med.biobank.importer;

import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class SiteContainers {

    private static SiteContainers instance = null;

    private SiteContainers() {}

    public static SiteContainers getInstance() {
        if (instance != null) return instance;
        instance = new SiteContainers();
        return instance;
    }

    public void insertContainers(SiteWrapper site) throws Exception {
        System.out.println("adding containers ...");

        createFreezer01(site);
        createFreezer03(site);
        createCabinet01(site);
    }

    private void createFreezer01(SiteWrapper site) throws Exception {
        ContainerTypeWrapper freezerType = SiteContainerTypes.getInstance().getContainerType(
            "Freezer-3x10");
        ContainerWrapper freezer01 = insertTopLevelContainer(site, "01",
            freezerType);

        ContainerWrapper hotel;
        ContainerTypeWrapper hotelType = SiteContainerTypes.getInstance().getContainerType(
            "Hotel-17");
        ContainerTypeWrapper palletType = SiteContainerTypes.getInstance().getContainerType(
            "Box-81");

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 10; ++col) {
                hotel = insertContainer(site, hotelType, freezer01, row, col);

                for (int j = 0; j < 17; ++j) {
                    insertContainer(site, palletType, hotel, j, 0);
                }
            }
        }
    }

    private void createFreezer03(SiteWrapper site) throws Exception {
        ContainerWrapper hotel;
        ContainerTypeWrapper freezerType = SiteContainerTypes.getInstance().getContainerType(
            "Freezer-5x9");
        ContainerWrapper freezer03 = insertTopLevelContainer(site, "03",
            freezerType);
        ContainerTypeWrapper hotel13Type = SiteContainerTypes.getInstance().getContainerType(
            "Hotel-13");
        ContainerTypeWrapper hotel19Type = SiteContainerTypes.getInstance().getContainerType(
            "Hotel-19");
        ContainerTypeWrapper palletType = SiteContainerTypes.getInstance().getContainerType(
            "Pallet-96");

        ContainerTypeWrapper [] hotelTypes = new ContainerTypeWrapper [] {
            hotel19Type, hotel13Type, hotel13Type, hotel19Type, hotel13Type,
            hotel19Type, hotel13Type, hotel19Type, hotel19Type, hotel13Type,
            hotel19Type, hotel13Type, hotel13Type, hotel13Type, hotel13Type,
            hotel13Type, hotel19Type, hotel13Type, hotel19Type, hotel13Type,
            hotel19Type, hotel19Type, hotel13Type, hotel19Type, hotel19Type,
            hotel19Type, hotel13Type, hotel19Type, hotel13Type, hotel13Type,
            hotel13Type, hotel19Type, hotel13Type, hotel13Type, hotel13Type,
            hotel19Type, hotel19Type, hotel13Type };

        RowColPos pos = new RowColPos();
        int count = 0;
        for (ContainerTypeWrapper hotelType : hotelTypes) {
            pos.row = count % 5;
            pos.col = count / 5;
            hotel = insertContainer(site, hotelType, freezer03, pos.row,
                pos.col);

            for (int j = 0, n = hotelType.getRowCapacity(); j < n; ++j) {
                insertContainer(site, palletType, hotel, j, 0);
            }
            ++count;
        }
    }

    private void createCabinet01(SiteWrapper site) throws Exception {
        ContainerTypeWrapper binType = SiteContainerTypes.getInstance().getContainerType(
            "Bin");
        ContainerTypeWrapper drawerType = SiteContainerTypes.getInstance().getContainerType(
            "Drawer");
        ContainerTypeWrapper cabinetType = SiteContainerTypes.getInstance().getContainerType(
            "Cabinet");
        ContainerWrapper cabinet = insertTopLevelContainer(site, "01",
            cabinetType);

        ContainerWrapper drawer;
        for (int i = 0; i < 4; ++i) {
            drawer = insertContainer(site, drawerType, cabinet, i, 0);

            for (int j = 0; j < 36; ++j) {
                insertContainer(site, binType, drawer, j, 0);
            }
        }
    }

    private ContainerWrapper insertTopLevelContainer(SiteWrapper site,
        String label, ContainerTypeWrapper type) throws Exception {
        ContainerWrapper container = new ContainerWrapper(site.getAppService());
        container.setProductBarcode(label);
        container.setSite(site);
        container.setLabel(label);
        container.setContainerType(type);
        container.setActivityStatus("Active");
        container.persist();
        container.reload();
        return container;
    }

    private ContainerWrapper insertContainer(SiteWrapper site,
        ContainerTypeWrapper type, ContainerWrapper parent, int pos1, int pos2)
        throws Exception {
        ContainerWrapper container = new ContainerWrapper(site.getAppService());
        container.setSite(site);
        container.setContainerType(type);
        container.setActivityStatus("Active");
        container.setPosition(pos1, pos2);
        container.persist();
        container.reload();
        return container;
    }

}
