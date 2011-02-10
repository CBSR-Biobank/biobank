package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.RequestItemBaseWrapper;

public class RequestItemWrapper extends RequestItemBaseWrapper {

    public RequestItemWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public RequestItemWrapper(WritableApplicationService appService,
        E wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public int compareTo(Object o) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Class getWrappedClass() {
        // TODO Auto-generated method stub
        return null;
    }

}
