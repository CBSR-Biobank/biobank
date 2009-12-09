
package edu.ualberta.med.biobank.importer;

import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class SiteContainers {

    public static void createContainers(SiteWrapper site) throws Exception {
        System.out.println("adding containers ...");

        createFreezer01(site);
        createFreezer02(site);
        createFreezer03(site);
        createFreezer04(site);
        createCabinet01(site);
    }

    private static void createFreezer01(SiteWrapper site) throws Exception {
        ContainerWrapper freezer01 = insertTopLevelContainer(site, "01",
            SiteContainerTypes.getContainerType("Freezer 3x10"));

        ContainerWrapper hotel;
        ContainerTypeWrapper hotelType = SiteContainerTypes.getContainerType("Hotel 17");
        ContainerTypeWrapper palletType = SiteContainerTypes.getContainerType("Box 81");

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 10; ++col) {
                hotel = insertContainer(site, hotelType, freezer01, row, col);

                for (int j = 0; j < 17; ++j) {
                    insertContainer(site, palletType, hotel, j, 0);
                }
            }
        }
    }

    private static void createFreezer02(SiteWrapper site) throws Exception {
        ContainerWrapper freezer02 = insertTopLevelContainer(site, "02",
            SiteContainerTypes.getContainerType("Freezer 4x12"));
    }

    private static void createFreezer03(SiteWrapper site) throws Exception {
        ContainerWrapper hotel;
        ContainerWrapper freezer03 = insertTopLevelContainer(site, "03",
            SiteContainerTypes.getContainerType("Freezer 5x9"));
        ContainerTypeWrapper hotel13Type = SiteContainerTypes.getContainerType("Hotel 13");
        ContainerTypeWrapper hotel19Type = SiteContainerTypes.getContainerType("Hotel 19");
        ContainerTypeWrapper palletType = SiteContainerTypes.getContainerType("Pallet 96");

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

    private static void createFreezer04(SiteWrapper site) throws Exception {
        ContainerWrapper freezer02 = insertTopLevelContainer(site, "04",
            SiteContainerTypes.getContainerType("Freezer 6x12"));
    }

    private static void createCabinet01(SiteWrapper site) throws Exception {
        ContainerTypeWrapper ftaBinType = SiteContainerTypes.getContainerType("FTA Bin");
        ContainerTypeWrapper drawerType = SiteContainerTypes.getContainerType("Drawer 36");
        ContainerTypeWrapper cabinetType = SiteContainerTypes.getContainerType("Cabinet 4 drawer");
        ContainerWrapper cabinet = insertTopLevelContainer(site, "01",
            cabinetType);

        ContainerWrapper drawer;
        for (int i = 0; i < 4; ++i) {
            drawer = insertContainer(site, drawerType, cabinet, i, 0);

            // don't know how FTA and Hair bins are layed out yet
            // for (int j = 0; j < 36; ++j) {
            // insertContainer(site, ftaBinType, drawer, j, 0);
            // }
        }
    }

    private static ContainerWrapper insertTopLevelContainer(SiteWrapper site,
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

    private static ContainerWrapper insertContainer(SiteWrapper site,
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
