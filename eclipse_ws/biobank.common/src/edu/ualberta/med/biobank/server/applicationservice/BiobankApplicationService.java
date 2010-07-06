package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.common.util.ReportOption;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.List;

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

    public boolean isContainerAdministrator() throws ApplicationException;

    public <E> List<E> query(BiobankSQLCriteria sqlCriteria,
        String targetClassName) throws ApplicationException;

    public void logActivity(String action, String patientNumber,
        String inventoryID, String locationLabel, String details, String type);

    public List<Object> launchReport(String reportClassName,
        List<Object> parameters, List<ReportOption> options,
        String siteOperator, Integer siteId) throws ApplicationException;
}
