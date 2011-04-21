package edu.ualberta.med.biobank.logs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.LogPeer;
import edu.ualberta.med.biobank.common.wrappers.LogWrapper;

public class LogQuery {

    private static LogQuery instance = null;

    private HashMap<String, String> searchQuery = new HashMap<String, String>();
    private List<LogWrapper> dbResults = new ArrayList<LogWrapper>();

    public static final String START_DATE_KEY = "startDate";
    public static final String END_DATE_KEY = "endDate";

    public static final String NONE = "NONE";
    public static final String ALL = "ALL";

    protected LogQuery() {
        /* Define all the keys to be used here */
        // searchQuery.put("containerType", "");
        // searchQuery.put("containerLabel", "");

        searchQuery.put(LogPeer.CENTER.getName(), "");
        searchQuery.put(LogPeer.USERNAME.getName(), "");
        searchQuery.put(LogPeer.TYPE.getName(), "");
        searchQuery.put(LogPeer.ACTION.getName(), "");
        searchQuery.put(LogPeer.PATIENT_NUMBER.getName(), "");
        searchQuery.put(LogPeer.INVENTORY_ID.getName(), "");
        searchQuery.put(LogPeer.LOCATION_LABEL.getName(), "");
        searchQuery.put(LogPeer.DETAILS.getName(), "");
        searchQuery.put(START_DATE_KEY, "");
        searchQuery.put(END_DATE_KEY, "");
    }

    public static LogQuery getInstance() {
        if (instance == null)
            instance = new LogQuery();

        return instance;
    }

    public List<LogWrapper> getDatabaseResults() {
        return dbResults;
    }

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
        // set the time on end date to midnight (00:00 AM)
        Date startDate = formatDate(startDateText, "00:00");

        String endDateText = searchQuery.get(END_DATE_KEY);
        endDateText = setValueIfEmpty(endDateText);
        // set the time on end date to 11:59 PM
        Date endDate = formatDate(endDateText, "23:59");

        dbResults = LogWrapper.getLogs(SessionManager.getAppService(), center,
            user, startDate, endDate, action, patientNumber, inventoryId,
            location, details, type);
    }

    private Date formatDate(String dateText, String time) {
        Date date = null;
        if (dateText != null) {
            date = DateFormatter.parseToDateTime(dateText + " " + time);
        }
        return date;
    }

    private String setValueIfEmpty(String value) {
        if ("".equals(value))
            return null;
        return value;
    }

    private String getValueForNoneAll(String value) {
        if (value.equals(NONE))
            return "";
        if (value.equals(ALL))
            return null;
        return value;
    }

    public String getSearchQueryItem(String key) throws Exception {
        String value = searchQuery.get(key);
        if (value == null) {
            throw new NullPointerException("Search Query key: " + key
                + " does not exist.");
        }
        return value;

    }

    public void setSearchQueryItem(String field, String value) throws Exception {
        if (searchQuery.get(field) == null || value == null)
            throw new NullPointerException();
        else
            searchQuery.put(field, value);

    }
}
