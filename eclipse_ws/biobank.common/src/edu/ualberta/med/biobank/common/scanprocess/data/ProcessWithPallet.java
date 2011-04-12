package edu.ualberta.med.biobank.common.scanprocess.data;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class ProcessWithPallet implements ProcessData {

    private static final long serialVersionUID = 1L;

    private Integer palletId;

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
            pallet = new ContainerWrapper(appService);
            pallet.getWrappedObject().setId(palletId);
            pallet.reload();
        }
        return pallet;
    }

}
