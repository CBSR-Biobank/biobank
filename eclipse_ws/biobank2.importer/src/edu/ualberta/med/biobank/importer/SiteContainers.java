
package edu.ualberta.med.biobank.importer;

import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;

public class SiteContainers {

    private static SiteContainers instance = null;

    private SiteContainers() {}

    public static SiteContainers getInstance() {
        if (instance != null) return instance;
        instance = new SiteContainers();
        return instance;
    }

    public void insertContainers(Site site) throws Exception {
        System.out.println("adding containers ...");

        createFreezer01(site);
        createFreezer03(site);
        createCabinet01(site);
    }

    private void createFreezer01(Site site) throws Exception {
        ContainerType freezerType = SiteContainerTypes.getInstance().getContainerType(
            "Freezer-3x10");
        Container freezer01 = insertContainer(site, "01", freezerType, null, 0,
            0);

        Container hotel;
        ContainerType hotelType = SiteContainerTypes.getInstance().getContainerType(
            "Hotel-17");
        ContainerType palletType = SiteContainerTypes.getInstance().getContainerType(
            "Box-81");

        RowColPos pos = new RowColPos();
        for (int i = 0; i < 30; ++i) {
            pos.row = i % 3;
            pos.col = i / 3;
            Capacity freezerCapacity = freezerType.getCapacity();
            String hotelPosLabel = "01"
                + LabelingScheme.getPositionString(pos,
                    freezerType.getChildLabelingScheme().getId(),
                    freezerCapacity.getRowCapacity(),
                    freezerCapacity.getColCapacity());
            hotel = insertContainer(site, hotelPosLabel, hotelType, freezer01,
                pos.row, pos.col);

            for (int j = 0; j < 17; ++j) {
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
        }
    }

    private void createFreezer03(Site site) throws Exception {
        Container hotel;
        ContainerType freezerType = SiteContainerTypes.getInstance().getContainerType(
            "Freezer-5x9");
        Container freezer03 = insertContainer(site, "03", freezerType, null, 0,
            0);
        ContainerType hotel13Type = SiteContainerTypes.getInstance().getContainerType(
            "Hotel-13");
        ContainerType hotel19Type = SiteContainerTypes.getInstance().getContainerType(
            "Hotel-19");
        ContainerType palletType = SiteContainerTypes.getInstance().getContainerType(
            "Pallet-96");

        ContainerType [] hotelTypes = new ContainerType [] {
            hotel19Type, hotel13Type, hotel13Type, hotel19Type, hotel13Type,
            hotel19Type, hotel13Type, hotel19Type, hotel19Type, hotel13Type,
            hotel19Type, hotel13Type, hotel13Type, hotel13Type, hotel13Type,
            hotel13Type, hotel19Type, hotel13Type, hotel19Type, hotel13Type,
            hotel19Type, hotel19Type, hotel13Type, hotel19Type, hotel19Type,
            hotel19Type, hotel13Type, hotel19Type, hotel13Type, hotel13Type,
            hotel13Type, hotel19Type, hotel13Type, hotel13Type, hotel13Type,
            hotel19Type, hotel19Type, hotel13Type, };

        RowColPos pos = new RowColPos();
        int count = 0;
        for (ContainerType hotelType : hotelTypes) {
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

    private void createCabinet01(Site site) throws Exception {
        ContainerType binType = SiteContainerTypes.getInstance().getContainerType(
            "Bin");
        ContainerType drawerType = SiteContainerTypes.getInstance().getContainerType(
            "Drawer");
        ContainerType cabinetType = SiteContainerTypes.getInstance().getContainerType(
            "Cabinet");
        Container cabinet = insertContainer(site, "01", cabinetType, null, 0, 0);

        RowColPos pos = new RowColPos();
        Container drawer;
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

    private Container insertContainer(Site site, String label,
        ContainerType st, Container parent, int pos1, int pos2)
        throws Exception {
        Container sc = new Container();
        sc.setLabel(label);
        sc.setProductBarcode(label);
        sc.setSite(site);
        sc.setContainerType(st);
        sc.setActivityStatus("Active");

        if (parent != null) {
            ContainerPosition cp = new ContainerPosition();
            cp.setContainer(sc);
            cp.setParentContainer(parent);
            cp.setRow(pos1);
            cp.setCol(pos2);
            sc.setPosition(cp);
        }

        return (Container) BioBank2Db.getInstance().setObject(sc);
    }

}
