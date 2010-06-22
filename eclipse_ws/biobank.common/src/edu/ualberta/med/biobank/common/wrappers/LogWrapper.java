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
            "inventoryId", "positionLabel", "details", "type" };
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

    public String getType() {
        return wrappedObject.getType();
    }

    public void setType(String type) {
        String old = getType();
        wrappedObject.setType(type);
        propertyChangeSupport.firePropertyChange("type", old, type);
    }

    public static List<LogWrapper> getLogs(
        WritableApplicationService appService, String username, Date startDate,
        Date endDate, String action, String patientNumber, String inventoryId,
        String locationLabel, String details, String type) throws Exception {
        StringBuffer parametersString = new StringBuffer();
        List<Object> parametersArgs = new ArrayList<Object>();
        addParam(parametersString, parametersArgs, "username", username);

        StringBuffer datePart = new StringBuffer();
        if ((startDate != null) && (endDate != null)) {
            datePart.append(" date >= ? and date <= ?");
            parametersArgs.add(startDate);
            parametersArgs.add(endDate);
        } else if (startDate != null) {
            datePart.append(" date >= ?");
            parametersArgs.add(startDate);
        } else if (endDate != null) {
            datePart.append(" date <= ?");
            parametersArgs.add(endDate);
        }

        if (datePart.length() > 0) {
            if (parametersString.length() > 0) {
                parametersString.append(" and");
            }
            parametersString.append(" " + datePart);
        }

        addParam(parametersString, parametersArgs, "action", action);
        addParam(parametersString, parametersArgs, "patientNumber",
            patientNumber);
        addParam(parametersString, parametersArgs, "inventoryId", inventoryId);
        addLocationLabelParam(parametersString, parametersArgs, locationLabel);
        addParam(parametersString, parametersArgs, "details", details, false);
        addParam(parametersString, parametersArgs, "type", type);
        String criteriaString = "from " + Log.class.getName();
        if (parametersString.length() > 0) {
            criteriaString += " where" + parametersString.toString();
        }
        List<Log> logs = appService.query(new HQLCriteria(criteriaString,
            parametersArgs));

        List<LogWrapper> wrappers = new ArrayList<LogWrapper>();
        for (Log l : logs) {
            // CASE is important for inventory id
            if ((inventoryId != null)
                && (l.getInventoryId().equals(inventoryId))) {
                wrappers.add(new LogWrapper(appService, l));
            }
        }
        return wrappers;
    }

    private static void addParam(StringBuffer sb, List<Object> parameters,
        String property, Object value) {
        addParam(sb, parameters, property, value, true);
    }

    private static void addParam(StringBuffer sb, List<Object> parameters,
        String property, Object value, boolean strict) {
        if (value != null) {
            if (sb.length() > 0) {
                sb.append(" and");
            }
            sb.append(" ").append(property);
            if (strict) {
                sb.append("=?");
                parameters.add(value);
            } else {
                sb.append(" like ?");
                parameters.add("%" + value + "%");
            }
        }
    }

    private static void addLocationLabelParam(StringBuffer sb,
        List<Object> parameters, Object value) {
        if (value != null) {
            if (sb.length() > 0) {
                sb.append(" and");
            }
            sb.append(" ").append("locationLabel").append(" like ?");
            parameters.add(value + " (%");
        }
    }

    public static List<String> getPossibleUsernames(
        WritableApplicationService appService) throws ApplicationException {
        return appService.query(new HQLCriteria(
            "select distinct(username) from " + Log.class.getName()));
    }

    public static List<String> getPossibleActions(
        WritableApplicationService appService) throws ApplicationException {
        return appService.query(new HQLCriteria("select distinct(action) from "
            + Log.class.getName()));
    }

    public static List<String> getPossibleTypes(
        WritableApplicationService appService) throws ApplicationException {
        return appService.query(new HQLCriteria("select distinct(type) from "
            + Log.class.getName()));
    }

    @Override
    public String toString() {
        return getDate() + " -- action: " + getAction() + " / type: "
            + getType() + " / patientNumber: " + getPatientNumber()
            + " / inventoryId: " + getInventoryId() + " / locationLabel: "
            + getLocationLabel() + " / details: " + getDetails();
    }
}
