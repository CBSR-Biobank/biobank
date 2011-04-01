package edu.ualberta.med.biobank.logs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.LogWrapper;

public class LogQuery {

    private static LogQuery instance = null;

    private static HashMap<String, String> searchQuery = new HashMap<String, String>();
    private static List<LogWrapper> dbResults = new ArrayList<LogWrapper>();

    protected LogQuery() {
        /* Define all the keys to be used here */
        // searchQuery.put("containerType", "");
        // searchQuery.put("containerLabel", "");

        searchQuery.put("center", "");
        searchQuery.put("user", "");
        searchQuery.put("type", "");
        searchQuery.put("action", "");
        searchQuery.put("patientNumber", "");
        searchQuery.put("inventoryId", "");
        searchQuery.put("location", "");
        searchQuery.put("details", "");
        searchQuery.put("startDate", "");
        searchQuery.put("endDate", "");
        dbResults.clear();
    }

    public static LogQuery getInstance() {
        if (instance == null)
            instance = new LogQuery();

        return instance;
    }

    public List<LogWrapper> getDatabaseResults() {
        return dbResults;
    }

    public boolean queryDatabase() {
        try {
            String center = searchQuery.get("center");
            center = center.equals("NONE") ? "" : center;
            center = center.equals("ALL") ? null : center;

            String user = searchQuery.get("user");
            user = user.equals("NONE") ? "" : user;
            user = user.equals("ALL") ? null : user;

            String action = searchQuery.get("action");
            action = action.equals("NONE") ? "" : action;
            action = action.equals("ALL") ? null : action;

            String type = searchQuery.get("type");
            type = type.equals("NONE") ? "" : type;
            type = type.equals("ALL") ? null : type;

            String patientNumber = searchQuery.get("patientNumber");
            patientNumber = patientNumber.equals("") ? null : patientNumber;

            String inventoryId = searchQuery.get("inventoryId");
            inventoryId = inventoryId.equals("") ? null : inventoryId;

            String details = searchQuery.get("details");
            details = details.equals("") ? null : details;

            String location = searchQuery.get("location");
            location = location.equals("") ? null : location;

            String startDateText = searchQuery.get("startDate");
            startDateText = startDateText.equals("") ? null : startDateText;

            String endDateText = searchQuery.get("endDate");
            endDateText = endDateText.equals("") ? null : endDateText;

            Date startDate = null;

            if (startDateText != null) {
                // set the time on end date to midnight (00:00 AM)
                startDate = DateFormatter.parseToDateTime(startDateText
                    + " 00:00");
            }

            Date endDate = null;

            if (endDateText != null) {
                // set the time on end date to 11:59 PM
                endDate = DateFormatter.parseToDateTime(endDateText + " 23:59");
            }

            dbResults = LogWrapper.getLogs(SessionManager.getAppService(),
                center, user, startDate, endDate, action, patientNumber,
                inventoryId, location, details, type);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getSearchQueryItem(String key) throws Exception {
        String value = searchQuery.get(key);

        if (value == null) {
            System.err.printf("Search Query key: %s does not exist.", value);
            throw new NullPointerException();
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
