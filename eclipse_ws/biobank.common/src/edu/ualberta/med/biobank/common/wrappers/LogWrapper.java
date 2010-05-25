package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.Log;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class LogWrapper extends ModelWrapper<Log> {

    public LogWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public LogWrapper(WritableApplicationService appService, Log log) {
        super(appService, log);
    }

    @Override
    protected void deleteChecks() throws Exception {
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "username", "date", "action", "patientNumber",
            "inventoryId", "positionLabel", "details" };
    }

    @Override
    public Class<Log> getWrappedClass() {
        return Log.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
    }

    @Override
    public int compareTo(ModelWrapper<Log> o) {
        return 0;
    }

    public String getUsername() {
        return wrappedObject.getUsername();
    }

    public void setUsername(String username) {
        String old = getUsername();
        wrappedObject.setUsername(username);
        propertyChangeSupport.firePropertyChange("username", old, username);
    }

    public Date getDate() {
        return wrappedObject.getDate();
    }

    public void setDate(Date date) {
        Date old = getDate();
        wrappedObject.setDate(date);
        propertyChangeSupport.firePropertyChange("date", old, date);
    }

    public String getAction() {
        return wrappedObject.getAction();
    }

    public void setAction(String action) {
        String old = getAction();
        wrappedObject.setAction(action);
        propertyChangeSupport.firePropertyChange("action", old, action);
    }

    public String getPatientNumber() {
        return wrappedObject.getPatientNumber();
    }

    public void setPatientNumber(String patientNumber) {
        String old = getPatientNumber();
        wrappedObject.setPatientNumber(patientNumber);
        propertyChangeSupport.firePropertyChange("patientNumber", old,
            patientNumber);
    }

    public String getInventoryId() {
        return wrappedObject.getInventoryId();
    }

    public void setInventoryId(String inventoryId) {
        String old = getInventoryId();
        wrappedObject.setInventoryId(inventoryId);
        propertyChangeSupport.firePropertyChange("inventoryId", old,
            inventoryId);
    }

    public String getLocationLabel() {
        return wrappedObject.getLocationLabel();
    }

    public void setLocationLabel(String locationLabel) {
        String old = getLocationLabel();
        wrappedObject.setLocationLabel(locationLabel);
        propertyChangeSupport.firePropertyChange("locationLabel", old,
            locationLabel);
    }

    public String getDetails() {
        return wrappedObject.getDetails();
    }

    public void setDetails(String details) {
        String old = getDetails();
        wrappedObject.setDetails(details);
        propertyChangeSupport.firePropertyChange("details", old, details);
    }

    public static List<LogWrapper> getLogs(
        WritableApplicationService appService, String username, Date date,
        String action, String patientNumber, String inventoryId,
        String locationLabel, String details) throws Exception {
        HQLCriteria criteria = new HQLCriteria("from " + Log.class.getName());
        List<Log> logs = appService.query(criteria);

        List<LogWrapper> wrappers = new ArrayList<LogWrapper>();
        for (Log l : logs) {
            wrappers.add(new LogWrapper(appService, l));
        }
        return wrappers;
    }

}
