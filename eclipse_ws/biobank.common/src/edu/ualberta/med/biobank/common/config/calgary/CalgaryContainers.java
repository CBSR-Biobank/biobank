package edu.ualberta.med.biobank.common.config.calgary;

import edu.ualberta.med.biobank.common.config.ConfigContainers;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.util.RowColPos;

public class CalgaryContainers extends ConfigContainers {

    public CalgaryContainers(SiteWrapper site) throws Exception {
        super(site);
        createFreezer01(site);

    }

    private void createFreezer01(SiteWrapper site) throws Exception {
        ContainerTypeWrapper freezerType = getContainerType("Freezer 2x2");
        ContainerWrapper freezer01 = addTopLevelContainer(site, "FR01",
            freezerType);
        ContainerTypeWrapper hotel13Type = getContainerType("Hotel 13");
        ContainerTypeWrapper hotel19Type = getContainerType("Hotel 19");

        ContainerTypeWrapper[] hotelTypes = new ContainerTypeWrapper[] {
            hotel19Type, hotel13Type, hotel19Type, hotel13Type };

        int numRows = freezer01.getRowCapacity();
        RowColPos pos = new RowColPos();
        int count = 0;
        for (ContainerTypeWrapper hotelType : hotelTypes) {
            pos.col = count % numRows;
            pos.row = count / numRows;
            addContainer(site, hotelType, freezer01, pos.row, pos.col);
            ++count;
        }
    }

}
