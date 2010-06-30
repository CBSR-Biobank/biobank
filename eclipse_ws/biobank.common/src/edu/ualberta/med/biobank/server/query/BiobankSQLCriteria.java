package edu.ualberta.med.biobank.server.query;

import java.io.Serializable;

public class BiobankSQLCriteria implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String sqlString;

    public BiobankSQLCriteria(String sqlString) {
        setSqlString(sqlString);
    }

    public void setSqlString(String sqlString) {
        this.sqlString = sqlString;
    }

    public String getSqlString() {
        return this.sqlString;
    }

}
