package edu.ualberta.med.biobank.common.config.calgary;

import java.util.Arrays;

import edu.ualberta.med.biobank.common.config.ConfigContainerTypes;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class CalgaryContainerTypes extends ConfigContainerTypes {

    public void createContainerTypes(SiteWrapper site) throws Exception {
        createFreezerTypes(site);
        createCabinetTypes(site);
    }

    private void createFreezerTypes(SiteWrapper site) throws Exception {
        ContainerTypeWrapper biopsyPallet96 = addContainerType(site,
            "Biopsy Pallet 96", "BP96", null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, false, 1, 8, 12, null,
            biopsyPallet96SampleTypes);

        ContainerTypeWrapper box81 = addContainerType(site, "Box 81", "B81",
            null, ActivityStatusWrapper.ACTIVE_STATUS_STRING, false, 1, 9, 9,
            null, box81SampleTypes);

        ContainerTypeWrapper cellPallet96 = addContainerType(site,
            "Cell Pallet 96", "CP96", null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, false, 1, 9, 9, null,
            cellPallet96SampleTypes);

        ContainerTypeWrapper pallet96 = addContainerType(site, "Pallet 96",
            "P96", null, ActivityStatusWrapper.ACTIVE_STATUS_STRING, false, 1,
            8, 12, null, pallet96SampleTypes);

        ContainerTypeWrapper hotel13 = addContainerType(site, "Hotel 13",
            "H13", null, ActivityStatusWrapper.ACTIVE_STATUS_STRING, false, 3,
            13, 1, Arrays.asList(new ContainerTypeWrapper[] { pallet96 }));

        ContainerTypeWrapper hotel17 = addContainerType(site, "Hotel 17",
            "H17", null, ActivityStatusWrapper.ACTIVE_STATUS_STRING, false, 3,
            17, 1, Arrays.asList(new ContainerTypeWrapper[] { box81 }));

        ContainerTypeWrapper hotel18 = addContainerType(site, "Hotel 18",
            "H18", null, ActivityStatusWrapper.ACTIVE_STATUS_STRING, false, 3,
            18, 1, Arrays.asList(new ContainerTypeWrapper[] { box81 }));

        ContainerTypeWrapper hotel19 = addContainerType(site, "Hotel 19",
            "H19", null, ActivityStatusWrapper.ACTIVE_STATUS_STRING, false, 3,
            19, 1, Arrays.asList(new ContainerTypeWrapper[] { biopsyPallet96,
                cellPallet96, pallet96 }));

        addContainerType(site, "Freezer 3x10", "F3x10", -80.0,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, true, 2, 3, 10, Arrays
                .asList(new ContainerTypeWrapper[] { hotel17 }));

        addContainerType(site, "Freezer 4x12", "F4x12", -80.0,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, true, 2, 4, 12, Arrays
                .asList(new ContainerTypeWrapper[] { hotel18 }));

        addContainerType(site, "Freezer 5x9", "F5x9", -80.0,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, true, 2, 5, 9, Arrays
                .asList(new ContainerTypeWrapper[] { hotel13, hotel19 }));

        addContainerType(site, "Freezer 4x17", "F4x17", -80.0,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, true, 2, 4, 17, Arrays
                .asList(new ContainerTypeWrapper[] { hotel13, hotel19 }));

        addContainerType(site, "Freezer 4x6", "F4x6", -80.0,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, true, 2, 4, 6, Arrays
                .asList(new ContainerTypeWrapper[] { hotel13, hotel19 }));
    }

    private void createCabinetTypes(SiteWrapper site) throws Exception {
        ContainerTypeWrapper ftaBin = addContainerType(site, "FTA Bin", "FBin",
            null, ActivityStatusWrapper.ACTIVE_STATUS_STRING, false, 2, 120, 1,
            null, ftaBinSampleTypes);

        ContainerTypeWrapper hairBin = addContainerType(site, "Hair Bin",
            "HBin", null, ActivityStatusWrapper.ACTIVE_STATUS_STRING, false, 2,
            120, 1, null, hairBinSampleTypes);

        ContainerTypeWrapper ftaBinLoersch = addContainerType(site,
            "FTA Bin Loersch", "FBinL", null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, false, 2, 68, 1, null,
            ftaBinSampleTypes);

        ContainerTypeWrapper drawer36 = addContainerType(site, "Drawer 36",
            "D36", null, ActivityStatusWrapper.ACTIVE_STATUS_STRING, false, 3,
            36, 1, Arrays.asList(new ContainerTypeWrapper[] { ftaBin,
                ftaBinLoersch, hairBin }));
        addContainerType(site, "Cabinet 4 drawer", "Cabinet 4", null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, true, 2, 4, 1, Arrays
                .asList(new ContainerTypeWrapper[] { drawer36 }));
    }
}
