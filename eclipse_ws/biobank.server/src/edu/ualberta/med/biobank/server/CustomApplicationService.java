package edu.ualberta.med.biobank.server;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public interface CustomApplicationService extends WritableApplicationService {

    public boolean canAccess(Class<?> clazz, String userLogin,
        Integer objectId, String privilegeName) throws ApplicationException;

}
