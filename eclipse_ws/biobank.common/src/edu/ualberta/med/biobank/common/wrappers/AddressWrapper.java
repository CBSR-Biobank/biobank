package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.AddressBaseWrapper;
import edu.ualberta.med.biobank.model.Address;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class AddressWrapper extends AddressBaseWrapper {
    public AddressWrapper(WritableApplicationService appService,
        Address wrappedObject) {
        super(appService, wrappedObject);
    }

    public AddressWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    public int compareTo(ModelWrapper<Address> o) {
        return 0;
    }

}
