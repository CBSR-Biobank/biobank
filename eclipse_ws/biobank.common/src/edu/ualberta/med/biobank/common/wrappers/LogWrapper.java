package edu.ualberta.med.biobank.common.wrappers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.peer.LogPeer;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.PostProcessListProxy;
import edu.ualberta.med.biobank.common.wrappers.base.LogBaseWrapper;
import edu.ualberta.med.biobank.model.Log;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

@SuppressWarnings("nls")
public class LogWrapper extends LogBaseWrapper {

    public LogWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public LogWrapper(WritableApplicationService appService, Log log) {
        super(appService, log);
    }

    public static List<LogWrapper> getLogs(
        final WritableApplicationService appService, String center,
        String username, Date startDate, Date endDate, String action,
        String patientNumber, String inventoryId, String locationLabel,
        String details, String type) {
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
            center, true);
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
        List<LogWrapper> logs = new PostProcessListProxy<LogWrapper>(
            appService, new HQLCriteria(qry.toString(), parametersArgs),
            new AbstractRowPostProcess() {
                private static final long serialVersionUID = 1L;

                @Override
                public Object rowPostProcess(Object element) {
                    return new LogWrapper(appService, (Log) element);
                }
            });

        return logs;
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

    public static final String POSSIBLE_CENTERS_QRY = "select distinct("
        + LogPeer.CENTER.getName() + ") from " + Log.class.getName()
        + " where " + LogPeer.CENTER.getName() + "!=''  ORDER BY "
        + LogPeer.CENTER.getName();

    public static List<String> getPossibleCenters(
        WritableApplicationService appService) throws ApplicationException {
        return appService.query(new HQLCriteria(POSSIBLE_CENTERS_QRY));
    }

    public static final String POSSIBLE_USER_NAMES_QRY = "select distinct("
        + LogPeer.USERNAME.getName() + ") from " + Log.class.getName()
        + " ORDER BY " + LogPeer.USERNAME.getName();

    public static List<String> getPossibleUsernames(
        WritableApplicationService appService) throws ApplicationException {
        return appService.query(new HQLCriteria(POSSIBLE_USER_NAMES_QRY));
    }

    public static final String POSSIBLE_ACTIONS_QRY = "select distinct("
        + LogPeer.ACTION.getName() + ") from " + Log.class.getName()
        + " ORDER BY " + LogPeer.ACTION.getName();

    public static List<String> getPossibleActions(
        WritableApplicationService appService) throws ApplicationException {
        return appService.query(new HQLCriteria(POSSIBLE_ACTIONS_QRY));
    }

    public static final String POSSIBLE_TYPES_QRY = "select distinct("
        + LogPeer.TYPE.getName() + ") from " + Log.class.getName()
        + " where type !='' ORDER BY " + LogPeer.TYPE.getName();

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
