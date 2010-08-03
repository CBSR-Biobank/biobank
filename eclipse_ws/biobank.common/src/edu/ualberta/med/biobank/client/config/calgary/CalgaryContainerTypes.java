package edu.ualberta.med.biobank.client.config.calgary;

import java.util.Arrays;

import edu.ualberta.med.biobank.client.config.ConfigContainerTypes;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class CalgaryContainerTypes extends ConfigContainerTypes {

    public CalgaryContainerTypes(SiteWrapper site) throws Exception {
        super(site);
        createFreezerTypes(site);
    }

    private void createFreezerTypes(SiteWrapper site) throws Exception {
        ContainerTypeWrapper pallet96 = addContainerType(site, "Pallet 96",
            "P96", null, ActivityStatusWrapper.ACTIVE_STATUS_STRING, false, 1,
            8, 12, null, pallet96SampleTypes);

        ContainerTypeWrapper hotel13 = addContainerType(site, "Hotel 13",
            "H13", -80.0, ActivityStatusWrapper.ACTIVE_STATUS_STRING, false, 3,
            13, 1, Arrays.asList(new ContainerTypeWrapper[] { pallet96 }));

        ContainerTypeWrapper hotel19 = addContainerType(site, "Hotel 19",
            "H19", -80.0, ActivityStatusWrapper.ACTIVE_STATUS_STRING, false, 3,
            19, 1, Arrays.asList(new ContainerTypeWrapper[] { pallet96 }));

        addContainerType(site, "Freezer 2x2", "F2x2", -80.0,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, true, 1, 2, 2,
            Arrays.asList(new ContainerTypeWrapper[] { hotel13, hotel19 }));
    }

}
