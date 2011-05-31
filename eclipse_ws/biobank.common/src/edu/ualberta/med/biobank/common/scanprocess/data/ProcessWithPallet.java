package edu.ualberta.med.biobank.common.scanprocess.data;

import java.util.Arrays;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.model.Container;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public abstract class ProcessWithPallet implements ProcessData {

    private static final long serialVersionUID = 1L;

    protected Integer palletId;

    // will retrieve on server side
    private transient ContainerWrapper pallet;

    public ProcessWithPallet(ContainerWrapper pallet) {
        if (pallet != null) {
            palletId = pallet.getId();
        }
    }

    public ContainerWrapper getPallet(WritableApplicationService appService)
        throws Exception {
        if (palletId != null && pallet == null) {
            Container c = (Container) appService.query(
                new HQLCriteria("from " + Container.class.getName()
                    + " where id=?", Arrays.asList(palletId))).get(0);
            // System.out.println(c.getSpecimenPositionCollection().size());
            pallet = new ContainerWrapper(appService, c);
        }
        return pallet;
    }
}
