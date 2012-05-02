package edu.ualberta.med.biobank.common.action.logging;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.LogPeer;
import edu.ualberta.med.biobank.common.permission.logging.LoggingPermission;
import edu.ualberta.med.biobank.model.Log;

@SuppressWarnings("nls")
public class LogQueryAction implements Action<ListResult<Log>> {
    private static final long serialVersionUID = 8892328030007487709L;

    private String center;
    private String username;
    private Date startDate;
    private Date endDate;
    private String action;
    private String patientNumber;
    private String inventoryId;
    private String locationLabel;
    private String details;
    private String type;

    public LogQueryAction(String center,
        String username, Date startDate, Date endDate, String action,
        String patientNumber, String inventoryId, String locationLabel,
        String details, String type) {
        this.center = center;
        this.username = username;
        this.startDate = startDate;
        this.endDate = endDate;
        this.action = action;
        this.patientNumber = patientNumber;
        this.inventoryId = inventoryId;
        this.locationLabel = locationLabel;
        this.details = details;
        this.type = type;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new LoggingPermission().isAllowed(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListResult<Log> run(ActionContext context) throws ActionException {
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

        Query query = context.getSession().createQuery(qry.toString());
        for (int i = 0, n = parametersArgs.size(); i < n; i++) {
            query.setParameter(i, parametersArgs.get(i));
        }

        List<Log> rows = query.list();

        return new ListResult<Log>(rows);
    }

    private void addParam(StringBuffer sb, List<Object> parameters,
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

    private void addLocationLabelParam(StringBuffer sb,
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

}