package edu.ualberta.med.biobank.common;

public class DatabaseResult {

    public static final DatabaseResult OK = new DatabaseResult("");

    private String message;

    public DatabaseResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
