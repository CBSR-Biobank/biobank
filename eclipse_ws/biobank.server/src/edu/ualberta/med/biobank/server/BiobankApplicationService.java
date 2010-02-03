package edu.ualberta.med.biobank.server;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * Application service interface obtained through
 * "ApplicationServiceProvider.getApplicationServiceFromUrl" method. This
 * replace the default WritableApplicationService interface to add our own
 * methods.
 * 
 * See build.properties of the sdk for the generator configuration +
 * application-config*.xml for the generated files.
 */
public interface BiobankApplicationService extends WritableApplicationService {

    public boolean canReadObjects(Class<?> clazz) throws ApplicationException;

    public boolean canReadObject(Class<?> clazz, Integer id)
        throws ApplicationException;

    public boolean canCreateObjects(Class<?> clazz) throws ApplicationException;

    public boolean canDeleteObjects(Class<?> clazz) throws ApplicationException;

    public boolean canDeleteObject(Class<?> clazz, Integer id)
        throws ApplicationException;

    public boolean canUpdateObjects(Class<?> clazz) throws ApplicationException;

    public boolean canUpdateObject(Class<?> clazz, Integer id)
        throws ApplicationException;

    public boolean hasPrivilege(Class<?> clazz, Integer id, String privilege)
        throws ApplicationException;

    public void newSite(Integer id, String name) throws ApplicationException;

}
