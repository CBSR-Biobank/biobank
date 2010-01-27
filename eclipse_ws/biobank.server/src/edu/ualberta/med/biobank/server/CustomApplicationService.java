package edu.ualberta.med.biobank.server;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public interface CustomApplicationService extends WritableApplicationService {

    public boolean canReadObjects(String userLogin, Class<?> clazz)
        throws ApplicationException;

    public boolean canReadObject(String userLogin, Class<?> clazz, Integer id)
        throws ApplicationException;

    public boolean canCreateObjects(String userLogin, Class<?> clazz)
        throws ApplicationException;

    public boolean canUpdateObjects(String userLogin, Class<?> clazz)
        throws ApplicationException;

    public boolean canUpdateObject(String userLogin, Class<?> clazz, Integer id)
        throws ApplicationException;

    public boolean hasPrivilege(String userLogin, Class<?> clazz, Integer id,
        String privilege) throws ApplicationException;

    public void newSite(Integer id, String name) throws ApplicationException;

}
