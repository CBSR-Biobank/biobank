package edu.ualberta.med.biobank.common.wrappers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.peer.LogPeer;
import edu.ualberta.med.biobank.common.wrappers.base.LogBaseWrapper;
import edu.ualberta.med.biobank.model.Log;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class LogWrapper extends LogBaseWrapper {

    public LogWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public LogWrapper(WritableApplicationService appService, Log log) {
        super(appService, log);
    }

    public static List<LogWrapper> getLogs(
        WritableApplicationService appService, String site, String username,
        Date startDate, Date endDate, String action, String patientNumber,
        String inventoryId, String locationLabel, String details, String type)
        throws Exception {
        StringBuffer parametersString = new StringBuffer();
        List<Object> parametersArgs = new ArrayList<Object>();
        addParam(parametersString, parametersArgs, LogPeer.USERNAME.getName(),
            username, true);

        StringBuffer datePart = new StringBuffer();
        if ((startDate != null) && (endDate != null)) {
            datePart.append(" ").append(LogPeer.CREATED_AT.getName())
                .append(" >= ? and ").append(LogPeer.CREATED_AT.getName())
                .append(" <= ?");
            parametersArgs.add(startDate);
            parametersArgs.add(endDate);
        } else if (startDate != null) {
            datePart.append(" ").append(LogPeer.CREATED_AT.getName())
                .append(" >= ?");
            parametersArgs.add(startDate);
        } else if (endDate != null) {
            datePart.append(" ").append(LogPeer.CREATED_AT.getName())
                .append(" <= ?");
            parametersArgs.add(endDate);
        }

        if (datePart.length() > 0) {
            if (parametersString.length() > 0) {
                parametersString.append(" and");
            }
            parametersString.append(" " + datePart);
        }

        addParam(parametersString, parametersArgs, LogPeer.CENTER.getName(),
            site, true);
        addParam(parametersString, parametersArgs, LogPeer.ACTION.getName(),
            action, true);
        addParam(parametersString, parametersArgs,
            LogPeer.PATIENT_NUMBER.getName(), patientNumber, true);
        addParam(parametersString, parametersArgs,
            LogPeer.INVENTORY_ID.getName(), inventoryId, true);
        addLocationLabelParam(parametersString, parametersArgs, locationLabel);
        addParam(parametersString, parametersArgs, LogPeer.DETAILS.getName(),
            details, false);
        addParam(parametersString, parametersArgs, LogPeer.TYPE.getName(),
            type, true);
        StringBuilder qry = new StringBuilder("from ").append(Log.class
            .getName());
        if (parametersString.length() > 0) {
            qry.append(" where").append(parametersString.toString());
        }
        List<Log> logs = appService.query(new HQLCriteria(qry.toString(),
            parametersArgs));

        List<LogWrapper> wrappers = new ArrayList<LogWrapper>();
        for (Log l : logs) {
            // CASE is important for inventory id
            String logInvId = l.getInventoryId();
            if ((inventoryId == null)
                || ((inventoryId != null) && (logInvId.equals(inventoryId)))) {
                wrappers.add(new LogWrapper(appService, l));
            }
        }
        return wrappers;
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
            sb.append(" ").append(LogPeer.LOCATION_LABEL.getName())
                .append(" like ?");
            parameters.add(value + " (%");
        }
    }

    public static final String POSSIBLE_SITES_QRY = "select distinct("
        + LogPeer.CENTER.getName() + ") from " + Log.class.getName()
        + " where " + LogPeer.CENTER.getName() + "!=''";

    public static List<String> getPossibleSites(
        WritableApplicationService appService) throws ApplicationException {
        return appService.query(new HQLCriteria(POSSIBLE_SITES_QRY));
    }

    public static final String POSSIBLE_USER_NAMES_QRY = "select distinct("
        + LogPeer.USERNAME.getName() + ") from " + Log.class.getName();

    public static List<String> getPossibleUsernames(
        WritableApplicationService appService) throws ApplicationException {
        return appService.query(new HQLCriteria(POSSIBLE_USER_NAMES_QRY));
    }

    public static final String POSSIBLE_ACTIONS_QRY = "select distinct("
        + LogPeer.ACTION.getName() + " from " + Log.class.getName();

    public static List<String> getPossibleActions(
        WritableApplicationService appService) throws ApplicationException {
        return appService.query(new HQLCriteria(POSSIBLE_ACTIONS_QRY));
    }

    public static final String POSSIBLE_TYPES_QRY = "select distinct("
        + LogPeer.TYPE.getName() + " from " + Log.class.getName()
        + " where type !=''";

    public static List<String> getPossibleTypes(
        WritableApplicationService appService) throws ApplicationException {
        return appService.query(new HQLCriteria(POSSIBLE_TYPES_QRY));
    }

    @Override
    public String toString() {
        return getCreatedAt() + " -- " + LogPeer.ACTION.getName() + ": "
            + getAction() + " / " + LogPeer.TYPE.getName() + ": " + getType()
            + " / " + LogPeer.PATIENT_NUMBER.getName() + ": "
            + getPatientNumber() + " / " + LogPeer.INVENTORY_ID.getName()
            + ": " + getInventoryId() + " / "
            + LogPeer.LOCATION_LABEL.getName() + ": " + getLocationLabel()
            + " / " + LogPeer.DETAILS.getName() + ": " + getDetails();
    }

    @SuppressWarnings("rawtypes")
    private static Map<String, Property> logFields;

    /**
     * Used on the server side
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public synchronized void setLogStringValue(String attributeName,
        String value) throws Exception {
        if (logFields == null) {
            logFields = new HashMap<String, Property>();
            for (Field field : LogPeer.class.getFields()) {
                if (field.getType().getName().equals(Property.class.getName())
                    && Modifier.isStatic(field.getModifiers())) {
                    Property property = (Property) field.get(null);
                    logFields.put(property.getName(), property);
                }
            }
        }
        Property property = logFields.get(attributeName);
        if (property != null)
            setProperty(property, value);
    }
}
