package edu.ualberta.med.biobank.client.reports.advanced;

import edu.ualberta.med.biobank.common.reports.QueryObject;

public class CustomQueryObject extends QueryObject {

    public CustomQueryObject(String description, String queryString,
        String[] columnNames) {
        super(description, queryString, columnNames);
    }

    @Override
    public String getName() {
        return null;
    }

}
