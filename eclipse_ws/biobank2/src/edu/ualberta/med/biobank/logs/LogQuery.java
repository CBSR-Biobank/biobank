package edu.ualberta.med.biobank.logs;

import java.util.HashMap;

public class LogQuery {

    private static LogQuery instance = null;

    private HashMap<String, String> searchQuery = new HashMap<String, String>();

    protected LogQuery() {
        /* Define all the keys to be used here */
        searchQuery.put("user", "SELECT ALL");
        searchQuery.put("form", "SELECT ALL");
        searchQuery.put("action", "SELECT ALL");
        searchQuery.put("patientNumber", "");
        searchQuery.put("inventoryId", "");
        searchQuery.put("containerType", "");
        searchQuery.put("containerLabel", "");
        searchQuery.put("details", "");
        searchQuery.put("startDate", "");
        searchQuery.put("stopDate", "");
    }

    /* XXX figure out how to restrict write access */
    public static LogQuery getInstance() {
        if (instance == null)
            instance = new LogQuery();

        return instance;
    }

    public String getSearchQueryItem(String key) throws Exception {
        String value = searchQuery.get(key);

        if (value == null) {
            System.err.printf("Searcg Query key: %s does not exist.", value);
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
