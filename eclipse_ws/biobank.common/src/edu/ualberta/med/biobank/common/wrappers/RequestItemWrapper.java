package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.RequestItemBaseWrapper;
import edu.ualberta.med.biobank.model.RequestItem;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class RequestItemWrapper extends RequestItemBaseWrapper<RequestItem> {

    public RequestItemWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public RequestItemWrapper(WritableApplicationService appService,
        RequestItem wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public Class getWrappedClass() {
        return RequestItem.class;
    }

}
