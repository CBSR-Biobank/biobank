
package edu.ualberta.med.biobank.importer;

import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Capacity;

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
            freezerType, null);

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
            freezerType, null);
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
            Capacity freezerCapacity = freezerType.getCapacity();
            String hotelPosLabel = "03"
                + LabelingScheme.getPositionString(pos,
                    freezerType.getChildLabelingScheme().getId(),
                    freezerCapacity.getRowCapacity(),
                    freezerCapacity.getColCapacity());
            hotel = insertContainer(site, hotelPosLabel, hotelType, freezer03,
                pos.row, pos.col);

            for (int j = 0, n = hotelType.getCapacity().getRowCapacity(); j < n; ++j) {
                pos.row = j;
                pos.col = 0;
                Capacity hotelCapacity = hotelType.getCapacity();
                insertContainer(site, hotelPosLabel
                    + LabelingScheme.getPositionString(pos,
                        hotelType.getChildLabelingScheme().getId(),
                        hotelCapacity.getRowCapacity(),
                        hotelCapacity.getColCapacity()), palletType, hotel,
                    pos.row, pos.col);
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
        ContainerWrapper cabinet = insertContainer(site, "01", cabinetType,
            null, 0, 0);

        RowColPos pos = new RowColPos();
        ContainerWrapper drawer;
        for (int i = 0; i < 4; ++i) {
            pos.row = i;
            pos.col = 0;
            Capacity cabinetCapacity = cabinetType.getCapacity();
            String drawerPosLabel = "01"
                + LabelingScheme.getPositionString(pos,
                    cabinetType.getChildLabelingScheme().getId(),
                    cabinetCapacity.getRowCapacity(),
                    cabinetCapacity.getColCapacity());
            drawer = insertContainer(site, drawerPosLabel,
                SiteContainerTypes.getInstance().getContainerType("Drawer"),
                cabinet, pos.row, pos.col);

            for (int j = 0; j < 36; ++j) {
                pos.row = j;
                pos.col = 0;
                Capacity drawerCapacity = drawerType.getCapacity();
                insertContainer(site, drawerPosLabel
                    + LabelingScheme.getPositionString(pos,
                        drawerType.getChildLabelingScheme().getId(),
                        drawerCapacity.getRowCapacity(),
                        drawerCapacity.getColCapacity()), binType, drawer,
                    pos.row, pos.col);
            }
        }
    }

    private ContainerWrapper insertTopLevelContainer(SiteWrapper site,
        String label, ContainerTypeWrapper st, ContainerWrapper parent)
        throws Exception {
        ContainerWrapper container = new ContainerWrapper(site.getAppService());
        container.setProductBarcode(label);
        container.setSite(site);
        container.setLabel(label);
        container.setContainerType(st);
        container.setActivityStatus("Active");
        container.persist();
        container.reload();
        return container;
    }

    private ContainerWrapper insertContainer(SiteWrapper site,
        ContainerTypeWrapper st, ContainerWrapper parent, int pos1, int pos2)
        throws Exception {
        ContainerWrapper container = new ContainerWrapper(site.getAppService());
        container.setSite(site);
        container.setContainerType(st);
        container.setActivityStatus("Active");
        container.setPosition(pos1, pos2);
        container.persist();
        container.reload();
        return container;
    }

}
