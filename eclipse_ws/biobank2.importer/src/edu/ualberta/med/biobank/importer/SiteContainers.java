
package edu.ualberta.med.biobank.importer;

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

        // only set up one freezer
        Container freezer3x10 = insertContainer(site, "01",
            SiteContainerTypes.getInstance().getContainerType("Freezer-3x10"),
            null, 0, 0);

        Container hotel;
        for (int i = 0; i < 10; ++i) {
            hotel = insertContainer(site, String.format("01A%c",
                LabelingScheme.int2pos(i)),
                SiteContainerTypes.getInstance().getContainerType("Hotel-19"),
                freezer3x10, i % 3, i / 3);

            for (int j = 0; j < 17; ++j) {
                insertContainer(site, String.format("01A%c%02d",
                    LabelingScheme.int2pos(i), j + 1),
                    SiteContainerTypes.getInstance().getContainerType(
                        "Palette-96"), hotel, j, 0);
            }
        }

        // cabinet
        Container cabinet = insertContainer(site, "01",
            SiteContainerTypes.getInstance().getContainerType("Cabinet"), null,
            0, 0);

        Container drawer;
        for (int i = 0; i < 4; ++i) {
            drawer = insertContainer(site, String.format("01A%c",
                LabelingScheme.int2pos(i)),
                SiteContainerTypes.getInstance().getContainerType("Drawer"),
                cabinet, i, 0);

            for (int j = 0; j < 36; ++j) {
                insertContainer(site, String.format("01A%c%02d",
                    LabelingScheme.int2pos(i), j + 1),
                    SiteContainerTypes.getInstance().getContainerType("Bin"),
                    drawer, j, 0);
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
            cp.setPositionDimensionOne(pos1);
            cp.setPositionDimensionTwo(pos2);
            sc.setPosition(cp);
        }

        return (Container) BioBank2Db.getInstance().setObject(sc);
    }

}
