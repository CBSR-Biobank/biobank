package edu.ualberta.med.biobank.common.cbsr;

import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class CbsrContainers {

    public static void createContainers(SiteWrapper site) throws Exception {
        createFreezer01(site);
        createFreezer02(site);
        createFreezer03(site);
        createFreezer05(site);
        createSentSamplesFreezer(site);
        createCabinet01(site);
        createCabinet02(site);
    }

    private static void createFreezer01(SiteWrapper site) throws Exception {
        ContainerWrapper freezer01 = addTopLevelContainer(site, "01",
            CbsrContainerTypes.getContainerType("Freezer 3x10"));

        ContainerWrapper hotel;
        ContainerTypeWrapper hotelType = CbsrContainerTypes
            .getContainerType("Hotel 17");
        ContainerTypeWrapper palletType = CbsrContainerTypes
            .getContainerType("Box 81");

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 10; ++col) {
                hotel = addContainer(site, hotelType, freezer01, row, col);

                for (int j = 0; j < 17; ++j) {
                    addContainer(site, palletType, hotel, j, 0);
                }
            }
        }
    }

    private static void createFreezer02(SiteWrapper site) throws Exception {
        ContainerWrapper freezer02 = addTopLevelContainer(site, "02",
            CbsrContainerTypes.getContainerType("Freezer 4x12"));

        ContainerWrapper hotel;
        ContainerTypeWrapper hotelType = CbsrContainerTypes
            .getContainerType("Hotel 18");
        ContainerTypeWrapper palletType = CbsrContainerTypes
            .getContainerType("Box 81");

        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < 12; ++col) {
                hotel = addContainer(site, hotelType, freezer02, row, col);

                for (int j = 0; j < 18; ++j) {
                    addContainer(site, palletType, hotel, j, 0);
                }
            }
        }
    }

    private static void createFreezer03(SiteWrapper site) throws Exception {
        ContainerWrapper hotel;
        ContainerWrapper freezer03 = addTopLevelContainer(site, "03",
            CbsrContainerTypes.getContainerType("Freezer 5x9"));
        ContainerTypeWrapper hotel13Type = CbsrContainerTypes
            .getContainerType("Hotel 13");
        ContainerTypeWrapper hotel19Type = CbsrContainerTypes
            .getContainerType("Hotel 19");
        ContainerTypeWrapper palletType = CbsrContainerTypes
            .getContainerType("Pallet 96");

        ContainerTypeWrapper[] hotelTypes = new ContainerTypeWrapper[] {
            hotel19Type, hotel13Type, hotel13Type, hotel19Type, hotel13Type,
            hotel19Type, hotel13Type, hotel19Type, hotel19Type, hotel13Type,
            hotel19Type, hotel13Type, hotel13Type, hotel13Type, hotel13Type,
            hotel13Type, hotel19Type, hotel13Type, hotel19Type, hotel13Type,
            hotel19Type, hotel19Type, hotel13Type, hotel19Type, hotel19Type,
            hotel19Type, hotel13Type, hotel19Type, hotel13Type, hotel13Type,
            hotel13Type, hotel19Type, hotel13Type, hotel13Type, hotel13Type,
            hotel19Type, hotel19Type, hotel13Type, hotel19Type, hotel19Type,
            hotel13Type, hotel13Type, hotel19Type, hotel19Type, hotel19Type };

        RowColPos pos = new RowColPos();
        int count = 0;
        for (ContainerTypeWrapper hotelType : hotelTypes) {
            pos.row = count % 5;
            pos.col = count / 5;
            hotel = addContainer(site, hotelType, freezer03, pos.row, pos.col);

            for (int j = 0, n = hotelType.getRowCapacity(); j < n; ++j) {
                addContainer(site, palletType, hotel, j, 0);
            }
            ++count;
        }
    }

    private static void createFreezer05(SiteWrapper site) throws Exception {
        ContainerWrapper hotel;
        ContainerWrapper freezer05 = addTopLevelContainer(site, "05",
            CbsrContainerTypes.getContainerType("Freezer 4x17"));
        ContainerTypeWrapper h13Type = CbsrContainerTypes
            .getContainerType("Hotel 13");
        ContainerTypeWrapper h19Type = CbsrContainerTypes
            .getContainerType("Hotel 19");
        ContainerTypeWrapper palletType = CbsrContainerTypes
            .getContainerType("Pallet 96");

        ContainerTypeWrapper[] hotelTypes = new ContainerTypeWrapper[] {
            h19Type, // AA
            h19Type, // AB
            h19Type, // AC
            h19Type, // AD
            h13Type, // AE
            h13Type, // AF
            h13Type, // AG
            h13Type, // AH
            h19Type, // AJ
            h19Type, // AK
            h19Type, // AL
            h19Type, // AM
            h19Type, // AN
            h19Type, // AP
            h19Type, // AQ
            h19Type, // AR
            h19Type, // AS
            h19Type, // AT
            h19Type, // AU
            h19Type, // AV
            h13Type, // AW
            h13Type, // AX
            h13Type, // AY
            h13Type, // AZ
            h19Type, // BA
            h19Type, // BB
            h19Type, // BC
            h19Type, // BD
            h13Type, // BE
            h13Type, // BF
            null, // BG
            null, // BH
            h19Type, // BJ
            h19Type, // BK
        };

        RowColPos pos = new RowColPos();
        int count = 0;
        int maxRows = freezer05.getRowCapacity();
        for (ContainerTypeWrapper hotelType : hotelTypes) {
            pos.row = count % maxRows;
            pos.col = count / maxRows;
            if (hotelType != null) {
                hotel = addContainer(site, hotelType, freezer05, pos.row,
                    pos.col);

                for (int j = 0, n = hotelType.getRowCapacity(); j < n; ++j) {
                    addContainer(site, palletType, hotel, j, 0);
                }
            }
            ++count;
        }
    }

    private static void createSentSamplesFreezer(SiteWrapper site)
        throws Exception {
        ContainerWrapper hotel;
        ContainerWrapper freezerSS = addTopLevelContainer(site, "SS",
            CbsrContainerTypes.getContainerType("Freezer 4x6"));
        freezerSS
            .setComment("This freezer holds samples that have been sent out.");
        freezerSS.persist();
        freezerSS.reload();
        ContainerTypeWrapper h13Type = CbsrContainerTypes
            .getContainerType("Hotel 13");
        ContainerTypeWrapper h19Type = CbsrContainerTypes
            .getContainerType("Hotel 19");
        ContainerTypeWrapper palletType = CbsrContainerTypes
            .getContainerType("Pallet 96");

        ContainerTypeWrapper[] hotelTypes = new ContainerTypeWrapper[] {
            h19Type, h13Type, h19Type, h19Type };

        RowColPos pos = new RowColPos();
        int count = 0;
        for (ContainerTypeWrapper hotelType : hotelTypes) {
            pos.row = count % 4;
            pos.col = count / 4;
            hotel = addContainer(site, hotelType, freezerSS, pos.row, pos.col);

            for (int j = 0, n = hotelType.getRowCapacity(); j < n; ++j) {
                addContainer(site, palletType, hotel, j, 0);
            }
            ++count;
        }
    }

    private static void createCabinet01(SiteWrapper site) throws Exception {
        ContainerTypeWrapper ftaBinType = CbsrContainerTypes
            .getContainerType("FTA Bin");
        ContainerTypeWrapper hairBinType = CbsrContainerTypes
            .getContainerType("Hair Bin");
        ContainerTypeWrapper drawerType = CbsrContainerTypes
            .getContainerType("Drawer 36");
        ContainerTypeWrapper cabinetType = CbsrContainerTypes
            .getContainerType("Cabinet 4 drawer");
        ContainerWrapper cabinet = addTopLevelContainer(site, "01", cabinetType);

        ContainerTypeWrapper[] binTypes = new ContainerTypeWrapper[] {
            // drawer AA
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType };

        ContainerWrapper drawer = addContainer(site, drawerType, cabinet, 0, 0);
        int count = 0;

        for (ContainerTypeWrapper binType : binTypes) {
            addContainer(site, binType, drawer, count, 0);
            ++count;
        }

        binTypes = new ContainerTypeWrapper[] {
            // drawer AB
            hairBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, hairBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType };

        drawer = addContainer(site, drawerType, cabinet, 1, 0);
        count = 0;

        for (ContainerTypeWrapper binType : binTypes) {
            addContainer(site, binType, drawer, count, 0);
            ++count;
        }

        // drawer AC
        binTypes = new ContainerTypeWrapper[] {
            // drawer AC
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            hairBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, hairBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType };

        drawer = addContainer(site, drawerType, cabinet, 2, 0);
        count = 0;
        for (ContainerTypeWrapper binType : binTypes) {
            addContainer(site, binType, drawer, count, 0);
            ++count;
        }

        // drawer AC
        binTypes = new ContainerTypeWrapper[] {
            // drawer AC
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, hairBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, hairBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType, ftaBinType, ftaBinType, ftaBinType, ftaBinType,
            ftaBinType };

        drawer = addContainer(site, drawerType, cabinet, 3, 0);
        count = 0;
        for (ContainerTypeWrapper binType : binTypes) {
            addContainer(site, binType, drawer, count, 0);
            ++count;
        }
    }

    private static void createCabinet02(SiteWrapper site) throws Exception {
        ContainerTypeWrapper ftaBinLoerschType = CbsrContainerTypes
            .getContainerType("FTA Bin Loersch");
        ContainerTypeWrapper hairBinType = CbsrContainerTypes
            .getContainerType("Hair Bin");
        ContainerTypeWrapper drawerType = CbsrContainerTypes
            .getContainerType("Drawer 36");
        ContainerTypeWrapper cabinetType = CbsrContainerTypes
            .getContainerType("Cabinet 4 drawer");
        ContainerWrapper cabinet = addTopLevelContainer(site, "02", cabinetType);

        ContainerTypeWrapper[] binTypes = new ContainerTypeWrapper[] {
            // drawer AA
            ftaBinLoerschType, ftaBinLoerschType, ftaBinLoerschType,
            ftaBinLoerschType, ftaBinLoerschType, ftaBinLoerschType,
            ftaBinLoerschType, ftaBinLoerschType, ftaBinLoerschType,
            ftaBinLoerschType, hairBinType, ftaBinLoerschType,
            ftaBinLoerschType, ftaBinLoerschType, ftaBinLoerschType,
            ftaBinLoerschType, ftaBinLoerschType, ftaBinLoerschType,
            ftaBinLoerschType, ftaBinLoerschType, ftaBinLoerschType,
            ftaBinLoerschType, ftaBinLoerschType, ftaBinLoerschType,
            ftaBinLoerschType, hairBinType, hairBinType, ftaBinLoerschType,
            ftaBinLoerschType, ftaBinLoerschType, ftaBinLoerschType,
            ftaBinLoerschType, ftaBinLoerschType, hairBinType,
            ftaBinLoerschType, ftaBinLoerschType };

        ContainerWrapper drawer = addContainer(site, drawerType, cabinet, 0, 0);
        int count = 0;

        for (ContainerTypeWrapper binType : binTypes) {
            addContainer(site, binType, drawer, count, 0);
            ++count;
        }

        // drawer AB
        binTypes = new ContainerTypeWrapper[] { ftaBinLoerschType,
            ftaBinLoerschType, ftaBinLoerschType, ftaBinLoerschType,
            ftaBinLoerschType, ftaBinLoerschType, ftaBinLoerschType,
            ftaBinLoerschType, ftaBinLoerschType, hairBinType,
            ftaBinLoerschType, ftaBinLoerschType, ftaBinLoerschType,
            ftaBinLoerschType, ftaBinLoerschType, ftaBinLoerschType,
            ftaBinLoerschType, ftaBinLoerschType, ftaBinLoerschType,
            ftaBinLoerschType, ftaBinLoerschType, ftaBinLoerschType,
            hairBinType, ftaBinLoerschType, ftaBinLoerschType,
            ftaBinLoerschType, ftaBinLoerschType, ftaBinLoerschType,
            ftaBinLoerschType, ftaBinLoerschType, ftaBinLoerschType,
            hairBinType, hairBinType, ftaBinLoerschType, ftaBinLoerschType,
            ftaBinLoerschType };

        drawer = addContainer(site, drawerType, cabinet, 1, 0);
        count = 0;

        for (ContainerTypeWrapper binType : binTypes) {
            addContainer(site, binType, drawer, count, 0);
            ++count;
        }

        // drawer AC
        binTypes = new ContainerTypeWrapper[] { hairBinType, // 01
            ftaBinLoerschType, // 02
            ftaBinLoerschType, // 03
            ftaBinLoerschType, // 04
            ftaBinLoerschType, // 05
            ftaBinLoerschType, // 06
            ftaBinLoerschType, // 07
            ftaBinLoerschType, // 08
            ftaBinLoerschType, // 09
            ftaBinLoerschType, // 10
            ftaBinLoerschType, // 11
            ftaBinLoerschType, // 12
            ftaBinLoerschType, // 13
            ftaBinLoerschType, // 14
            ftaBinLoerschType, // 15
            ftaBinLoerschType, // 16
            ftaBinLoerschType, // 17
            ftaBinLoerschType, // 18
            ftaBinLoerschType, // 19
            ftaBinLoerschType, // 20
            ftaBinLoerschType, // 21
            ftaBinLoerschType, // 22
            hairBinType, // 23
            hairBinType, // 24
            ftaBinLoerschType, // 25
            ftaBinLoerschType, // 26
            ftaBinLoerschType, // 27
            ftaBinLoerschType, // 28
            ftaBinLoerschType, // 29
            ftaBinLoerschType, // 30
            ftaBinLoerschType, // 31
            hairBinType, // 32
            hairBinType, // 33
            ftaBinLoerschType, // 34
            ftaBinLoerschType, // 35
            ftaBinLoerschType // 36
        };

        drawer = addContainer(site, drawerType, cabinet, 2, 0);
        count = 0;
        for (ContainerTypeWrapper binType : binTypes) {
            if (binType != null) {
                addContainer(site, binType, drawer, count, 0);
            }
            ++count;
        }

        // drawer AD
        binTypes = new ContainerTypeWrapper[] {
        //
            ftaBinLoerschType, // 01
            ftaBinLoerschType, // 02
            ftaBinLoerschType, // 03
            ftaBinLoerschType, // 04
            ftaBinLoerschType, // 05
            ftaBinLoerschType, // 06
            ftaBinLoerschType, // 07
            ftaBinLoerschType, // 08
            ftaBinLoerschType, // 09
            ftaBinLoerschType, // 10
            ftaBinLoerschType, // 11
            ftaBinLoerschType, // 12
            ftaBinLoerschType, // 13
            ftaBinLoerschType, // 14
            ftaBinLoerschType, // 15
            ftaBinLoerschType, // 16
            ftaBinLoerschType, // 17
            ftaBinLoerschType, // 18
            ftaBinLoerschType, // 19
            ftaBinLoerschType, // 20
            ftaBinLoerschType, // 21
            ftaBinLoerschType, // 22
            ftaBinLoerschType, // 23
            ftaBinLoerschType, // 24
            ftaBinLoerschType, // 25
            ftaBinLoerschType, // 26
            hairBinType, // 27,
            null, // 28
            ftaBinLoerschType, // 29
            ftaBinLoerschType, // 30
            ftaBinLoerschType, // 31
            ftaBinLoerschType, // 32
            ftaBinLoerschType, // 33
        };

        drawer = addContainer(site, drawerType, cabinet, 3, 0);
        count = 0;
        for (ContainerTypeWrapper binType : binTypes) {
            if (binType != null) {
                addContainer(site, binType, drawer, count, 0);
            }
            ++count;
        }
    }

    private static ContainerWrapper addTopLevelContainer(SiteWrapper site,
        String label, ContainerTypeWrapper type) throws Exception {
        ContainerWrapper container = new ContainerWrapper(site.getAppService());
        container.setLabel(label);
        container.setSite(site);
        container.setContainerType(type);
        container.setActivityStatus(CbsrSite.getActiveActivityStatus());
        container.persist();
        container.reload();
        return container;
    }

    private static ContainerWrapper addContainer(SiteWrapper site,
        ContainerTypeWrapper type, ContainerWrapper parent, int row, int col)
        throws Exception {
        ContainerWrapper container = new ContainerWrapper(site.getAppService());
        container.setSite(site);
        container.setContainerType(type);
        container.setActivityStatus(CbsrSite.getActiveActivityStatus());
        container.setPosition(row, col);
        container.setParent(parent);
        container.persist();
        container.reload();
        return container;
    }

}
