
package edu.ualberta.med.biobank.importer;

import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.StorageType;

public class SiteStorageContainers {

    private static SiteStorageContainers instance = null;

    private SiteStorageContainers() {}

    public static SiteStorageContainers getInstance() {
        if (instance != null) return instance;
        instance = new SiteStorageContainers();
        return instance;
    }

    public void insertStorageContainers(Site site) throws Exception {
        StorageContainer freezer3x10 = insertStorageContainer(site, "FR01",
            SiteStorageTypes.getInstance().getStorageType("Freezer-3x10"),
            null, 0, 0);
        StorageContainer hotel1 = insertStorageContainer(site, "AA",
            SiteStorageTypes.getInstance().getStorageType("Hotel-19"),
            freezer3x10, 1, 1);
        insertStorageContainer(site, "Palette1",
            SiteStorageTypes.getInstance().getStorageType("Palette"), hotel1,
            1, 1);
        insertStorageContainer(site, "Palette2",
            SiteStorageTypes.getInstance().getStorageType("Palette"), hotel1,
            3, 1);
        StorageContainer hotel2 = insertStorageContainer(site, "AB",
            SiteStorageTypes.getInstance().getStorageType("Hotel-13"),
            freezer3x10, 2, 2);
        insertStorageContainer(site, "Palette3",
            SiteStorageTypes.getInstance().getStorageType("Palette"), hotel2,
            1, 1);
        insertStorageContainer(site, "Palette4",
            SiteStorageTypes.getInstance().getStorageType("Palette"), hotel2,
            5, 1);

        // cabinet
        StorageContainer cabinet = insertStorageContainer(site, "Cabinet",
            SiteStorageTypes.getInstance().getStorageType("Cabinet"), null, 0,
            0);

        StorageContainer drawer;
        for (int i = 0; i < 4; ++i) {
            drawer = insertStorageContainer(site,
                String.format("A%c", 'A' + i),
                SiteStorageTypes.getInstance().getStorageType("Drawer"),
                cabinet, 1, i + 1);

            for (int j = 0; j < 36; ++j) {
                insertStorageContainer(site, String.format("%02d", j + 1),
                    SiteStorageTypes.getInstance().getStorageType("Bin"),
                    drawer, 1, j + 1);
            }
        }
    }

    private StorageContainer insertStorageContainer(Site site, String name,
        StorageType st, StorageContainer parent, int pos1, int pos2)
        throws Exception {
        StorageContainer sc = new StorageContainer();
        sc.setName(name);
        sc.setBarcode(name);
        sc.setSite(site);
        sc.setStorageType(st);
        ContainerPosition cp = new ContainerPosition();
        cp.setOccupiedContainer(sc);
        if (parent != null) {
            cp.setParentContainer(parent);
            cp.setPositionDimensionOne(pos1);
            cp.setPositionDimensionTwo(pos2);
        }
        sc.setLocatedAtPosition(cp);

        return (StorageContainer) BioBank2Db.getInstance().setObject(sc);
    }

}
