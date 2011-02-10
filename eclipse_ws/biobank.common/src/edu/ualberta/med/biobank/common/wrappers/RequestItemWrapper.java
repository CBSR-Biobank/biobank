package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.RequestItemBaseWrapper;
import edu.ualberta.med.biobank.model.RequestItem;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class RequestItemWrapper<E extends RequestItem> extends
    RequestItemBaseWrapper<RequestItem> {

    public RequestItemWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public RequestItemWrapper(WritableApplicationService appService,
        E wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public Class<RequestItem> getWrappedClass() {
        return null;
    }

}
