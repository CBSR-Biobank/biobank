
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
        System.out.println("adding storage containers ...");

        // only set up one freezer
        Container freezer3x10 = insertContainer(site, "FR01",
            SiteContainerTypes.getInstance().getContainerType("Freezer-3x10"),
            null, 0, 0);

        Container hotel;
        for (int i = 0; i < 10; ++i) {
            hotel = insertContainer(site, String.format("A%c",
                NumberingScheme.int2pos(i)),
                SiteContainerTypes.getInstance().getContainerType("Hotel-19"),
                freezer3x10, i % 3, i / 3);

            for (int j = 0; j < 17; ++j) {
                insertContainer(
                    site,
                    String.format("%02d", j + 1),
                    SiteContainerTypes.getInstance().getContainerType("Palette"),
                    hotel, j, 0);
            }
        }

        // cabinet
        Container cabinet = insertContainer(site, "Cabinet",
            SiteContainerTypes.getInstance().getContainerType("Cabinet"), null,
            0, 0);

        Container drawer;
        for (int i = 0; i < 4; ++i) {
            drawer = insertContainer(site, String.format("A%c",
                NumberingScheme.int2pos(i)),
                SiteContainerTypes.getInstance().getContainerType("Drawer"),
                cabinet, i, 0);

            for (int j = 0; j < 36; ++j) {
                insertContainer(site, String.format("%02d", j + 1),
                    SiteContainerTypes.getInstance().getContainerType("Bin"),
                    drawer, j, 0);
            }
        }
    }

    private Container insertContainer(Site site, String positionCode,
        ContainerType st, Container parent, int pos1, int pos2)
        throws Exception {
        Container sc = new Container();
        sc.setPositionCode(positionCode);
        sc.setProductBarcode(positionCode);
        sc.setSite(site);
        sc.setContainerType(st);
        sc.setActivityStatus("Active");
        ContainerPosition cp = new ContainerPosition();
        cp.setContainer(sc);
        if (parent != null) {
            cp.setParentContainer(parent);
            cp.setPositionDimensionOne(pos1);
            cp.setPositionDimensionTwo(pos2);
        }
        sc.setPosition(cp);

        return (Container) BioBank2Db.getInstance().setObject(sc);
    }

}
