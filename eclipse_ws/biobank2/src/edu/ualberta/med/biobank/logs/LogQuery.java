package edu.ualberta.med.biobank.logs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.eclipse.osgi.util.NLS;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.logging.LogQueryAction;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.LogPeer;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.LogWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Log;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class LogQuery {
    private static final I18n i18n = I18nFactory
        .getI18n(LogQuery.class);

    private static LogQuery instance = null;

    private final HashMap<String, String> searchQuery =
        new HashMap<String, String>();
    private ListResult<Log> dbResults;

    @SuppressWarnings("nls")
    public static final String START_DATE_KEY = "startDate";
    @SuppressWarnings("nls")
    public static final String END_DATE_KEY = "endDate";

    @SuppressWarnings("nls")
    // set the time on end date to midnight (00:00 AM)
    public static final String DEFAULT_START_TIME = "00:00";

    @SuppressWarnings("nls")
    // set the time on end date to 11:59 PM
    public static final String DEFAULT_END_TIME = "23:59";

    @SuppressWarnings("nls")
    public static final String NONE = "NONE";
    @SuppressWarnings("nls")
    public static final String ALL = "ALL";

    protected LogQuery() {
        /* Define all the keys to be used here */
        searchQuery.put(LogPeer.CENTER.getName(), StringUtil.EMPTY_STRING);
        searchQuery.put(LogPeer.USERNAME.getName(), StringUtil.EMPTY_STRING);
        searchQuery.put(LogPeer.TYPE.getName(), StringUtil.EMPTY_STRING);
        searchQuery.put(LogPeer.ACTION.getName(), StringUtil.EMPTY_STRING);
        searchQuery.put(LogPeer.PATIENT_NUMBER.getName(),
            StringUtil.EMPTY_STRING);
        searchQuery
            .put(LogPeer.INVENTORY_ID.getName(), StringUtil.EMPTY_STRING);
        searchQuery.put(LogPeer.LOCATION_LABEL.getName(),
            StringUtil.EMPTY_STRING);
        searchQuery.put(LogPeer.DETAILS.getName(), StringUtil.EMPTY_STRING);
        searchQuery.put(START_DATE_KEY, StringUtil.EMPTY_STRING);
        searchQuery.put(END_DATE_KEY, StringUtil.EMPTY_STRING);
    }

    public static LogQuery getInstance() {
        if (instance == null)
            instance = new LogQuery();

        return instance;
    }

    public List<LogWrapper> getDatabaseResults() {
        List<LogWrapper> logs = new ArrayList<LogWrapper>();
        for (Log log : dbResults.getList()) {
            logs.add(new LogWrapper(SessionManager.getAppService(), log));
        }
        return logs;
    }

    @SuppressWarnings("nls")
    public void queryDatabase() {
        String center = searchQuery.get(LogPeer.CENTER.getName());
        center = getValueForNoneAll(center);

        String user = searchQuery.get(LogPeer.USERNAME.getName());
        user = getValueForNoneAll(user);

        String action = searchQuery.get(LogPeer.ACTION.getName());
        action = getValueForNoneAll(action);

        String type = searchQuery.get(LogPeer.TYPE.getName());
        type = getValueForNoneAll(type);

        String patientNumber = searchQuery
            .get(LogPeer.PATIENT_NUMBER.getName());
        patientNumber = setValueIfEmpty(patientNumber);

        String inventoryId = searchQuery.get(LogPeer.INVENTORY_ID.getName());
        inventoryId = setValueIfEmpty(inventoryId);

        String details = searchQuery.get(LogPeer.DETAILS.getName());
        details = setValueIfEmpty(details);

        String location = searchQuery.get(LogPeer.LOCATION_LABEL.getName());
        location = setValueIfEmpty(location);

        String startDateText = searchQuery.get(START_DATE_KEY);
        startDateText = setValueIfEmpty(startDateText);
        Date startDate = formatDate(startDateText, DEFAULT_START_TIME);

        String endDateText = searchQuery.get(END_DATE_KEY);
        endDateText = setValueIfEmpty(endDateText);
        Date endDate = formatDate(endDateText, DEFAULT_END_TIME);

        try {
            dbResults =
                SessionManager.getAppService().doAction(
                    new LogQueryAction(center,
                        user, startDate, endDate, action, patientNumber,
                        inventoryId,
                        location, details, type));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                // TR: error dialog title
                i18n.tr("Unable to retrieve logs"), e);
        }
    }

    @SuppressWarnings("nls")
    private Date formatDate(String dateText, String time) {
        Date date = null;
        if (dateText != null) {
            date = DateFormatter.parseToDateTime(dateText + " " + time);
        }
        return date;
    }

    private String setValueIfEmpty(String value) {
        if (StringUtil.EMPTY_STRING.equals(value))
            return null;
        return value;
    }

    private String getValueForNoneAll(String value) {
        if (value.equals(NONE))
            return StringUtil.EMPTY_STRING;
        if (value.equals(ALL))
            return null;
        return value;
    }

    @SuppressWarnings("nls")
    public String getSearchQueryItem(String key) throws Exception {
        String value = searchQuery.get(key);
        if (value == null) {
            throw new NullPointerException(NLS.bind(
                // exception error message
                i18n.tr("Search Query key: {0} does not exist."), key));
        }
        return value;

    }

    public void setSearchQueryItem(String field, String value) throws Exception {
        if (searchQuery.get(field) == null || value == null)
            throw new NullPointerException();
        searchQuery.put(field, value);

    }
}
